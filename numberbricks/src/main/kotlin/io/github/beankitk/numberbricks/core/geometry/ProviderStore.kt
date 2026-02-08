package io.github.beankitk.numberbricks.core.geometry

/**
 * Transient data store for sharing provider outputs during digit construction.
 *
 * Acts as a read-only registry where providers can access data produced by their
 * dependencies. Scoped to a single digit and discarded after brick construction completes.
 * Only the digit builder can write to the store; providers have read-only access.
 */
interface ProviderStore {

    /**
     * The digit this store is scoped to (0-9, or -1 for default).
     */
    val digit: Int

    /**
     * The grid configuration for this digit construction.
     */
    val layoutConfig: GridConfig

    /**
     * Checks if data for a provider is available.
     *
     * @param key The provider's key
     * @return true if the provider has executed and stored data
     */
    fun <T> has(key: ProviderKey<T>): Boolean

    /**
     * Retrieves data produced by a provider.
     *
     * @param key The provider's key
     * @return List of data elements from the provider
     * @throws IllegalStateException if the provider hasn't executed yet
     */
    fun <T> get(key: ProviderKey<T>): List<T>
}

/**
 * Default implementation of [ProviderStore].
 *
 * Manages provider data storage and retrieval for a single digit construction.
 * Created per digit by the builder and discarded after use.
 *
 * @property digit The digit being constructed
 * @property layoutConfig The grid configuration
 */
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

    /**
     * Stores provider data. Use by the [DigitBuilder] only.
     *
     * @param key The provider's key whose data is to be stored
     * @param providerData The data to store
     * @throws IllegalArgumentException if data size doesn't match brick count
     */
    internal fun <T> store(key: ProviderKey<T>, providerData: List<T>) {
        require(providerData.size == layoutConfig.bricks) {
            "Provider data list must have $layoutConfig.bricks size, but was ${providerData.size} for $key"
        }
        providerDataStore[key] = providerData
    }

    /**
     * Removes provider data. Use by the [DigitBuilder] only.
     *
     * @param key The provider's key whose data is to be removed
     */
    internal fun <T> remove(key: ProviderKey<T>) {
        providerDataStore.remove(key)
    }

    /**
     * Clears all stored data. Use by the [DigitBuilder] only.
     */
    internal fun clear() {
        providerDataStore.clear()
    }
}