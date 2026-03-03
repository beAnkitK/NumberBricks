package io.github.beankitk.numberbricks.core.geometry

import androidx.collection.MutableScatterMap

interface ProviderScope {

    val digit: Int

    fun <R> resultOf(providerKey: ProviderKey<R>): List<R>

    fun <R> containsResultOf(providerKey: ProviderKey<R>): Boolean
}

interface MutableProviderScope : ProviderScope {

    fun <R> commitResult(providerKey: ProviderKey<R>, providerResult: List<R>)

    fun <R> discardResultOf(providerKey: ProviderKey<R>): List<R>

    fun dispose()
}

class DefaultProviderScope(
    override val digit: Int
) : MutableProviderScope, AutoCloseable {

    private val resultStore = MutableScatterMap<ProviderKey<*>, List<*>>(5)
    private var currentProvider: GeometryProvider<*>? = null

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

    override fun <R> containsResultOf(providerKey: ProviderKey<R>): Boolean =
        resultStore.contains(providerKey)

    @Suppress("UNCHECKED_CAST")
    override fun <R> resultOf(providerKey: ProviderKey<R>): List<R> {
        return resultStore[providerKey] as? List<R>
            ?: error("No result found for provider with $providerKey. Provider may not have executed yet.")
    }

    override fun <R> commitResult(providerKey: ProviderKey<R>, providerResult: List<R>) {
        resultStore[providerKey] = providerResult
    }

    @Suppress("UNCHECKED_CAST")
    override fun <R> discardResultOf(providerKey: ProviderKey<R>): List<R> =
        resultStore.remove(providerKey) as List<R>

    override fun dispose() {
        resultStore.clear()
        currentProvider = null
    }

    override fun close() = dispose()
}

@Suppress("NOTHING_TO_INLINE", "FunctionName")
inline fun ProviderScope(digit: Int): DefaultProviderScope =
    DefaultProviderScope(digit)