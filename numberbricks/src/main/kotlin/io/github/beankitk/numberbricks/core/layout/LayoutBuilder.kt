package io.github.beankitk.numberbricks.core.layout

interface LayoutBuilder<T : BrickItem<T>> {

    fun construct(properties: LayoutProperties)

    fun bindProviders(properties: LayoutProperties)

    fun getBrickItemsFor(digit: Int): List<T>

    fun defaultBrickItems(): List<T>

    fun destruct()
}

abstract class BrickLayoutBuilder<T : BrickItem<T>> : LayoutBuilder<T> {

    private var isConstructed = false
    private val providersRegistry = mutableListOf<LayoutProvider<*>>()
    private var executionOrder: List<LayoutProvider<*>> = emptyList()
    private lateinit var properties: LayoutProperties

    override fun construct(properties: LayoutProperties) {
        require(!isConstructed) { "Builder already constructed" }
        this.properties = properties
        bindProviders(properties)
        executionOrder = computeExecutionOrder()
        providersRegistry.forEach { it.attachWith(properties) }
        isConstructed = true
    }

    final override fun getBrickItemsFor(digit: Int): List<T> {
        checkConstructed()
        val providerStore = DefaultProviderStore(digit, properties.config)
        executionOrder.forEach { provider ->
            computeDataFor(digit, provider, providerStore)
        }
        return buildBricksFor(digit, providerStore)
    }

    //TODO: Add digit parameter to return digit aware deafult bricks
    final override fun defaultBrickItems(): List<T> {
        checkConstructed()
        return getBrickItemsFor(-1)
    }

    override fun destruct() {
        providersRegistry.clear()
        executionOrder = emptyList()
        isConstructed = false
    }

    protected final fun <P> registerProvider(provider: LayoutProvider<P>) {
        require(!isConstructed) { "Cannot register providers after construction" }
        require(providersRegistry.none { it.key == provider.key }) {
            "Provider with ${provider.key} already registered"
        }

        val providerConsent = provider.matchesWith(properties)
        if (providerConsent.hasRejected()) {
            error(providerConsent.getRejectionReason() ?:
                "Provider '${provider.key}' incompatible with layout")
        }

        providersRegistry.add(provider)
    }

    private fun <P> computeDataFor(
        digit: Int,
        provider: LayoutProvider<P>,
        providerStore: DefaultProviderStore
    ) {
        val providerData = provider.getProviderData(digit, providerStore)
        providerStore.store<P>(provider.key, providerData)
    }

    private fun computeExecutionOrder(): List<LayoutProvider<*>> {
        if (providersRegistry.all { it.dependsOn.isEmpty() }) {
            return providersRegistry.toList()
        }

        val providersByKey = providersRegistry.associateBy { it.key }
        val visitedProviders = mutableMapOf<ProviderKey<*>, VisitState>()
        val orderedProvider = mutableListOf<LayoutProvider<*>>()

        fun dfs(key: ProviderKey<*>) {
            when (visitedProviders[key]) {
                VisitState.VISITING -> error("Failed due to cyclic provider dependency detected at $key")
                VisitState.VISITED -> return
                else -> { /* continue */ }
            }

            visitedProviders[key] = VisitState.VISITING
            val provider = providersByKey[key] ?: error("Unknown provider dependency: $key")
            provider.dependsOn.forEach { depKey -> dfs(depKey) }
            visitedProviders[key] = VisitState.VISITED
            orderedProvider.add(provider)
        }

        providersByKey.keys.forEach { key ->
            if (visitedProviders[key] == null) dfs(key)
        }

        return orderedProvider
    }

    private enum class VisitState { VISITING, VISITED }

    private fun checkConstructed() {
        require(isConstructed) { "Builder not constructed. Call construct() first" }
    }

    protected abstract fun buildBricksFor(digit: Int, store: ProviderStore): List<T>
    //protected abstract fun buildDefaultBricks(digit: Int): List<T>
}