package io.github.beankitk.numberbricks.core.geometry

interface ProviderStore {

    val digit: Int

    val layoutConfig: GridConfig

    fun <T> has(key: ProviderKey<T>): Boolean

    fun <T> get(key: ProviderKey<T>): List<T>
}

class DefaultProviderStore(
    override val digit: Int,
    override val layoutConfig: GridConfig
) : ProviderStore {

    private val providerDataStore = mutableMapOf<ProviderKey<*>, List<*>>()

    override fun <T> has(key: ProviderKey<T>): Boolean =
        providerDataStore.containsKey(key)

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(key: ProviderKey<T>): List<T> {
        return providerDataStore[key] as? List<T>
            ?: error("No data found for key '${key.id}'. Provider may not have executed yet.")
    }

    internal fun <T> store(key: ProviderKey<T>, providerData: List<T>) {
        require(providerData.size == layoutConfig.bricks) {
            "Provider data list must have $layoutConfig.bricks size, but was ${providerData.size} for $key"
        }
        providerDataStore[key] = providerData
    }

    internal fun <T> remove(key: ProviderKey<T>) {
        providerDataStore.remove(key)
    }

    internal fun clear() {
        providerDataStore.clear()
    }
}