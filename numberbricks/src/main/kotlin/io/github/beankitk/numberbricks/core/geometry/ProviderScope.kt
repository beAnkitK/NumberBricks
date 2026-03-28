@file:OptIn(ExperimentalProviderMetaApi::class)

package io.github.beankitk.numberbricks.core.geometry

import androidx.collection.MutableScatterMap

interface ProviderScope {

    val digit: Int

    fun <R> hasResult(providerKey: ProviderKey<R>): Boolean

    fun <R> resultOf(providerKey: ProviderKey<R>): List<R>

    fun <M> hasMeta(meta: Meta<out GeometryProvider<*>, M>): Boolean

    fun <M> metaOf(meta: Meta<out GeometryProvider<*>, M>): M?

    fun <P : GeometryProvider<*>> P.provideMeta(block: MetaScope<P>.() -> Unit)
}

interface MutableProviderScope : ProviderScope {

    fun <R> commitResult(providerKey: ProviderKey<R>, providerResult: List<R>)

    fun <R> removeResult(providerKey: ProviderKey<R>): List<R>?

    fun <P : GeometryProvider<*>, M> attachMeta(meta: Meta<P, M>, value: M)

    fun dispose()
}

@Suppress("NOTHING_TO_INLINE")
inline fun ProviderScope(digit: Int): DefaultProviderScope =
    DefaultProviderScope(digit)

class DefaultProviderScope(
    override val digit: Int
) : MutableProviderScope, AutoCloseable {

    private val resultStore = MutableScatterMap<ProviderKey<*>, List<*>>(5)
    private val metaStore = MutableScatterMap<Meta<*, *>, Any?>(5)
    private var currentProvider: GeometryProvider<*>? = null
    private var cachedMetaScope: MetaScope<Nothing>? = null

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

@ExperimentalProviderMetaApi
interface MetaScope<P : GeometryProvider<*>> {
    infix fun <M> Meta<P, M>.providedBy(value: M)
}