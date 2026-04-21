@file:OptIn(ExperimentalProviderMetaApi::class)

package io.github.beankitk.numberbricks.core.geometry

import androidx.collection.MutableScatterMap

/**
 * Provides the digit-scoped environment for provider execution and inter-provider
 * data exchange.
 *
 * A [ProviderScope] is created and owned by [DigitBuilder] for each digit during
 * geometry construction. It represents a short-lived, per-digit environment where
 * all registered [GeometryProvider]s are executed to compute their data.
 *
 * Within this scope, providers can coordinate and communicate with each other by:
 * - Accessing the current [digit] they are computing for
 * - Reading results produced by other providers
 * - Sharing metadata across providers
 *
 * This enables providers to build upon each other's outputs and contribute their
 * own results back to [DigitBuilder], which then assembles the final brick model
 * for the digit associated with this scope.
 *
 * The scope is disposed once the final brick model for the digit is constructed.
 *
 * **Important:** For correct inter-provider communication -
 * 1. [GeometryProvider]s must declare all dependencies for both results and metadata via `dependsOn`.
 * 2. [DigitBuilder] must resolve these dependencies, execute providers accordingly, and store their
 * results in this scope.
 *
 * Otherwise, required data may be unavailable and access will fail with runtime errors.
 */
interface ProviderScope {

    /**
     * Represents the digit for which this scope is created and associated, used by all providers
     * to compute their data, with value constrained within `0..9`, or `-1` when computing default bricks.
     */
    val digit: Int

    /**
     * Returns `true` if a result for the given [providerKey] is available in this scope.
     *
     * This is mainly useful for defensive checks. In a correctly ordered execution, required
     * results should already be available when accessed.
     *
     * @param R The type of data produced by the provider
     */
    fun <R> hasResult(providerKey: ProviderKey<R>): Boolean

    /**
     * Returns the result produced by the provider for the given [providerKey].
     *
     * The returned list contains one element per brick, aligned with the provider's
     * grid constraints.
     *
     * **Important:**
     * - The result is guaranteed to be available if the provider dependency graph
     *   is correctly declared and executed in order by [DigitBuilder].
     * - If the result is accessed before the provider has executed, this will throw
     *   an [IllegalStateException].
     *
     * @param R The type of data produced by the provider
     * @return A list of values aligned with the digit's brick structure
     * @throws IllegalStateException if no result is found for the given key
     */
    fun <R> resultOf(providerKey: ProviderKey<R>): List<R>

    /**
     * Returns `true` if a metadata value for the given [meta] has been explicitly
     * provided in this scope.
     *
     * This only reflects values provided via [provideMeta] during execution and
     * does not consider the fallback value defined by [Meta.default].
     *
     * @param M The type of metadata
     */
    fun <M> hasMeta(meta: Meta<out GeometryProvider<*>, M>): Boolean

    /**
     * Returns the metadata value associated with the given [meta], or `null`
     * if no value has been provided in this scope.
     *
     * This can occur when:
     * - The associated provider is not registered with the [DigitBuilder], or
     * - The provider does not provide a value for this [Meta] during execution
     *
     * Note:
     * - This does not return [Meta.default].
     * - Callers are responsible for applying default fallback if required.
     *
     * @param M The type of metadata
     */
    fun <M> metaOf(meta: Meta<out GeometryProvider<*>, M>): M?

    /**
     * Publishes meta defined by this provider to the current [ProviderScope] so other
     * providers can read it within the same scope.
     *
     * This acts as the provider-side channel for providing computed meta to the scope,
     * enabling other providers to access it during the same execution.
     *
     * The [GeometryProvider] receiver (`P`) defines the provider context and enforces
     * ownership — only [Meta] defined by this provider can be provided. This prevents
     * cross-provider writes and ensures meta remains scoped to its defining provider.
     *
     * The [block] runs in a [MetaScope], which uses this provider context to allow only
     * meta defined by this provider to be attached to the current [ProviderScope] using
     * the `providedBy` infix.
     *
     * Example:
     * ```kotlin
     * this@SomeProvider.provideMeta {
     *     SomeProvider.Meta.padding providedBy 8f
     * }
     * ```
     *
     * @see metaOf
     * @receiver The [GeometryProvider] invoking this function. Restricts writes to meta
     * defined by this provider.
     */
    fun <P : GeometryProvider<*>> P.provideMeta(block: MetaScope<P>.() -> Unit)
}

/**
 * Writable scope used by [DigitBuilder] to store provider results and metadata.
 *
 * Extends [ProviderScope] with mutation APIs for committing provider outputs.
 * Providers supply results and meta to the [DigitBuilder], which then commits them
 * to this scope. These mutation APIs are not intended to be used directly
 * by [GeometryProvider]s.
 */
interface MutableProviderScope : ProviderScope {

    /**
     * Stores the result produced by a provider to this scope.
     *
     * The [providerResult] must have elements aligned with the total number of
     * bricks defined by the [DigitBuilder]'s grid spec.
     *
     * @param R The type of data produced by the provider
     */
    fun <R> commitResult(providerKey: ProviderKey<R>, providerResult: List<R>)

    /**
     * Removes and returns the result associated with the given [providerKey], if present
     * in this scope.
     *
     * @param R The type of data produced by the provider
     */
    fun <R> removeResult(providerKey: ProviderKey<R>): List<R>?

    /**
     * Attaches a metadata for the given [meta] to this scope.
     *
     * Used internally by [MetaScope] via `providedBy` infix for storing metadata. This
     * overrides any previously stored value for the same [meta] within this scope.
     *
     * @param P The provider that owns the metadata being attached
     * @param M The type of the metadata
     */
    fun <P : GeometryProvider<*>, M> attachMeta(meta: Meta<P, M>, value: M)

    /**
     * Clears all stored results and metadata, resetting this scope.
     */
    fun dispose()
}

/**
 * Factory function that creates a default [ProviderScope] implementation
 * for the given [digit].
 *
 * @returns a new [DefaultProviderScope] instance, which acts as the mutable scope
 */
@Suppress("NOTHING_TO_INLINE")
inline fun ProviderScope(digit: Int): DefaultProviderScope =
    DefaultProviderScope(digit)

/**
 * Default implementation of [MutableProviderScope].
 *
 * Manages provider results and metadata for a single digit during geometry
 * construction. This scope is created by the [DigitBuilder] per digit and disposed
 * once computation completes.
 */
class DefaultProviderScope(
    override val digit: Int
) : MutableProviderScope, AutoCloseable {

    private val resultStore = MutableScatterMap<ProviderKey<*>, List<*>>(5)
    private val metaStore = MutableScatterMap<Meta<*, *>, Any?>(5)
    private var currentProvider: GeometryProvider<*>? = null
    private var cachedMetaScope: MetaScope<Nothing>? = null

    /** Executes provider with the [provider] set as current context. */
    internal inline fun <P> withProvider(
        provider: GeometryProvider<P>,
        block: GeometryProvider<P>.() -> Unit
    ) {
        currentProvider = provider
        try {
            provider.block()
        } finally {
            currentProvider = null
        }
    }

    override fun <R> hasResult(providerKey: ProviderKey<R>): Boolean {
        return resultStore.contains(providerKey)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <R> resultOf(providerKey: ProviderKey<R>): List<R> {
        return resultStore[providerKey] as? List<R>
            ?: error("Result for $providerKey not found. Ensure the provider is registered and dependencies are correctly declared.")
    }

    override fun <R> commitResult(providerKey: ProviderKey<R>, providerResult: List<R>) {
        resultStore[providerKey] = providerResult
    }

    @Suppress("UNCHECKED_CAST")
    override fun <R> removeResult(providerKey: ProviderKey<R>): List<R>? {
        return resultStore.remove(providerKey) as? List<R>
    }

    override fun <M> hasMeta(meta: Meta<out GeometryProvider<*>, M>): Boolean {
        return metaStore.contains(meta)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <M> metaOf(meta: Meta<out GeometryProvider<*>, M>): M? {
         return metaStore[meta] as? M
    }

    override fun <P : GeometryProvider<*>, M> attachMeta(meta: Meta<P, M>, value: M) {
        metaStore[meta] = value
    }

    override fun <P : GeometryProvider<*>> P.provideMeta(block: MetaScope<P>.() -> Unit) {
        val sharedMetaScope = cachedMetaScope ?: object : MetaScope<Nothing> {
            override infix fun <M> Meta<Nothing, M>.providedBy(value: M) {
                attachMeta(this, value)
            }
        }.also { cachedMetaScope = it }

        @Suppress("UNCHECKED_CAST")
        (sharedMetaScope as MetaScope<P>).block()
    }

    override fun dispose() {
        resultStore.clear()
        metaStore.clear()
        currentProvider = null
        cachedMetaScope = null
    }

    override fun close() = dispose()
}

/**
 * Defines a provider-bound scope for writing meta owned by a [GeometryProvider] to
 * [ProviderScope]
 *
 * This scope is used within [ProviderScope.provideMeta] to attach meta to the current
 * [ProviderScope]. It exposes the `providedBy` infix function to associate a [Meta]
 * with its value and store it in the scope.
 *
 * The type parameter [P] binds this scope to a specific provider. Only meta
 * defined by this provider can be provided within this scope, enforcing ownership
 * and preventing cross-provider writes.
 */
@ExperimentalProviderMetaApi
interface MetaScope<P : GeometryProvider<*>> {

    /**
     * Associates this [Meta] with the given [value] and stores it in the
     * current [ProviderScope].
     *
     * @param M The type of metadata
     */
    infix fun <M> Meta<P, M>.providedBy(value: M)
}