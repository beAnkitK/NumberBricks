package io.github.beankitk.numberbricks.core.geometry

import androidx.collection.MutableScatterMap

/**
 * Provides the digit-scoped environment for provider execution and inter-provider data exchange.
 *
 * A [ProviderScope] is created and owned by [DigitBuilder] for each digit during geometry
 * construction. It represents a short-lived, per-digit environment where all registered
 * [GeometryProvider]s are executed to compute their result.
 *
 * Within this scope, providers can coordinate and communicate with each other by:
 * - Accessing the current [digit] they are computing for
 * - Reading results produced by other providers
 * - Sharing meta values across providers
 *
 * This enables providers to build upon each other's outputs and contribute their own results back
 * to [DigitBuilder], which then assembles the final brick model for the digit associated with this
 * scope.
 *
 * The scope is disposed once the final brick model for the digit is constructed.
 *
 * **Important:** For correct inter-provider communication -
 * 1. [GeometryProvider]s must declare all dependencies for results and meta values via `dependsOn`.
 * 2. [DigitBuilder] must resolve these dependencies, execute providers accordingly, and store their
 *    results in this scope.
 *
 * Otherwise, required data may be unavailable and access will fail with runtime errors.
 */
interface ProviderScope {

    /**
     * Represents the digit for which this scope is created and associated, used by all providers to
     * compute their data, with value constrained within `0..9`, or `-1` when computing default
     * bricks.
     */
    val digit: Int

    /**
     * Returns whether a result for the given provider key or family key is available in this scope.
     *
     * @param R The type of the provider result.
     * @param key The provider key or family key identifying the result.
     * @return `true` if a result is available for [key], `false` otherwise.
     */
    fun <R : Any> hasResult(key: ProviderKey<R>): Boolean

    /**
     * Returns the result for the given provider key or family key, or throws if the result is not
     * available in this scope.
     *
     * The result is expected to be available when the provider dependency graph is correctly
     * declared and executed by [DigitBuilder]. Accessing a result before its provider has executed,
     * or when no result is available for the key, throws an [IllegalStateException].
     *
     * @param R The type of the provider result.
     * @param key The provider key or family key identifying the result.
     * @return The provider result, with values aligned to the digit's brick structure.
     * @throws IllegalStateException If no result is available for [key].
     */
    fun <R : Any> resultOf(key: ProviderKey<R>): List<R>

    /**
     * Returns whether a meta value for [metaKey] has been provided in this scope.
     *
     * @param M The type of the meta value.
     * @param metaKey The key identifying the meta value.
     * @return `true` if a value has been provided for [metaKey], `false` otherwise.
     */
    fun <M> hasMeta(metaKey: MetaKey<*, M>): Boolean

    /**
     * Returns the meta value associated with [metaKey], or `null` if no value has been provided in
     * this scope.
     *
     * A value may be unavailable if the owning provider is not registered with the [DigitBuilder]
     * or does not provide a value for the key during execution.
     *
     * @param M The type of the meta value.
     * @param metaKey The key identifying the meta value.
     * @return The provided meta value, or `null` if no value is available.
     */
    fun <M> metaOf(metaKey: MetaKey<*, M>): M?

    /**
     * Provides meta owned by this provider to the current [ProviderScope].
     *
     * The receiver provider determines which [MetaKey]s can be provided. Within [block], values are
     * associated with meta keys using [MetaProviderScope.providedBy]. Only meta keys owned by the
     * receiver provider can be used, enforcing ownership at compile time.
     *
     * The provided values are stored in the current [ProviderScope] and can be checked or retrieved
     * by other providers using [hasMeta] and [metaOf].
     *
     * Example:
     * ```kotlin
     * this@SomeProvider.provideMeta {
     *     SomeProvider.Padding providedBy 8f
     * }
     * ```
     *
     * @param P The type of the provider owning the meta keys.
     * @param block The block that provides values for the provider's meta keys.
     * @receiver The provider that owns the meta keys being provided.
     * @see MetaProviderScope
     */
    fun <P : GeometryProvider<*>> P.provideMeta(block: MetaProviderScope<P>.() -> Unit)
}

/**
 * Mutable [ProviderScope] that supports managing data associated with geometry providers during
 * geometry composition for a digit. Used by [DigitBuilder] to manage data produced by
 * [GeometryProvider]s, including provider results.
 */
interface MutableProviderScope : ProviderScope {

    /**
     * Stores a provider's result in this scope for the given provider key or family key.
     *
     * The result is stored using the provider's family key and must contain one value for each
     * brick defined by the [NumberComposer.digitGridSpec]
     *
     * @param R The type of the provider result.
     * @param key The provider key or family key identifying the result.
     * @param result The provider result, with one value for each brick.
     */
    fun <R : Any> storeResult(key: ProviderKey<R>, result: List<R>)

    /**
     * Removes and returns the result for the given provider key or family key, if present in this
     * scope.
     *
     * @param R The type of the provider result.
     * @param key The provider key or family key identifying the result.
     * @return The removed provider result, or `null` if no result is available for [key].
     */
    fun <R : Any> removeResult(key: ProviderKey<R>): List<R>?

    /**
     * Stores the given [value] for [metaKey] in this scope. If a value has already been stored for
     * [metaKey], it is replaced by the new value.
     *
     * @param P The [GeometryProvider] that owns the meta key.
     * @param M The type of the meta value.
     * @param metaKey The meta key identifying the value.
     * @param value The meta value to store.
     */
    fun <P : GeometryProvider<*>, M> storeMeta(metaKey: MetaKey<P, M>, value: M)

    /** Clears all stored results and meta, resetting this scope. */
    fun dispose()
}

/**
 * Factory function that creates a default [ProviderScope] implementation for the given [digit].
 *
 * @return a new [DefaultProviderScope] instance, which acts as the mutable scope
 */
@Suppress("NOTHING_TO_INLINE")
inline fun ProviderScope(digit: Int): DefaultProviderScope = DefaultProviderScope(digit)

/**
 * Default implementation of [MutableProviderScope].
 *
 * Manages provider results and meta values for a single digit during geometry composition. This
 * scope is created by the [DigitBuilder] per digit and disposed once computation completes.
 */
class DefaultProviderScope(override val digit: Int) : MutableProviderScope, AutoCloseable {

    private val resultStore = MutableScatterMap<ProviderKey<*>, List<*>>(5)
    private val metaStore = MutableScatterMap<MetaKey<*, *>, Any?>(5)
    private var currentProvider: GeometryProvider<*>? = null
    private var metaProviderScope: MetaProviderScope<Nothing>? = null

    /** Executes provider with the [provider] set as current context. */
    internal inline fun <R : Any> withProvider(
        provider: GeometryProvider<R>,
        block: GeometryProvider<R>.() -> Unit,
    ) {
        currentProvider = provider
        try {
            provider.block()
        } finally {
            currentProvider = null
        }
    }

    override fun <R : Any> hasResult(key: ProviderKey<R>): Boolean {
        return resultStore.contains(key.family)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <R : Any> resultOf(key: ProviderKey<R>): List<R> {
        return resultStore[key.family] as? List<R>
            ?: error(
                "Result for $key not found. Ensure the provider is registered and dependencies are correctly declared."
            )
    }

    override fun <R : Any> storeResult(key: ProviderKey<R>, result: List<R>) {
        resultStore[key.family] = result
    }

    @Suppress("UNCHECKED_CAST")
    override fun <R : Any> removeResult(key: ProviderKey<R>): List<R>? {
        return resultStore.remove(key.family) as? List<R>
    }

    override fun <M> hasMeta(metaKey: MetaKey<*, M>): Boolean {
        return metaStore.contains(metaKey)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <M> metaOf(metaKey: MetaKey<*, M>): M? {
        return metaStore[metaKey] as? M
    }

    override fun <P : GeometryProvider<*>, M> storeMeta(metaKey: MetaKey<P, M>, value: M) {
        metaStore[metaKey] = value
    }

    override fun <P : GeometryProvider<*>> P.provideMeta(block: MetaProviderScope<P>.() -> Unit) {
        val sharedMetaScope =
            metaProviderScope
                ?: object : MetaProviderScope<Nothing> {
                        override infix fun <M> MetaKey<Nothing, M>.providedBy(value: M) {
                            storeMeta(this, value)
                        }
                    }
                    .also { metaProviderScope = it }

        @Suppress("UNCHECKED_CAST") (sharedMetaScope as MetaProviderScope<P>).block()
    }

    override fun dispose() {
        resultStore.clear()
        metaStore.clear()
        currentProvider = null
        metaProviderScope = null
    }

    override fun close() = dispose()
}
