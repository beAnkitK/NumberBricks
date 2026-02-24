package io.github.beankitk.numberbricks.core.geometry

interface DigitBuilder<T : Brick<T>> {

    fun construct(properties: GeometryProps)

    fun bindProviders()

    fun getBricksFor(digit: Int): List<T>

    //TODO: Add digit parameter to return digit aware default bricks
    fun defaultBricks(): List<T>

    fun destruct()
}

abstract class BaseDigitBuilder<T : Brick<T>> : DigitBuilder<T> {

    private var isConstructed = false
    private val providersRegistry = mutableListOf<GeometryProvider<*>>()
    private var executionOrder: List<GeometryProvider<*>> = emptyList()

    protected lateinit var properties: GeometryProps
        private set

    override fun construct(properties: GeometryProps) {
        require(!isConstructed) { "Builder already constructed" }
        this.properties = properties
        bindProviders()
        executionOrder = computeExecutionOrder()
        providersRegistry.forEach { it.attachWith(properties) }
        isConstructed = true
    }

    final override fun getBricksFor(digit: Int): List<T> {
        checkConstructed()

        require(digit in 0..9 || digit == -1) {
            "Builder accepts digit values from 0 to 9 to construct bricks and -1 for default bricks, but got $digit"
        }

        val providerStore = DefaultProviderStore(digit, properties.config)
        executionOrder.forEach { provider ->
            executeProvider(digit, provider, providerStore)
        }
        return buildBricks(digit, providerStore)
    }

    final override fun defaultBricks(): List<T> {
        checkConstructed()
        return buildDefaultBricks()
    }

    override fun destruct() {
        providersRegistry.clear()
        executionOrder = emptyList()
        isConstructed = false
    }

    protected final fun <P> registerProvider(provider: GeometryProvider<P>) {
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

    protected abstract fun buildBricks(digit: Int, providerStore: ProviderStore): List<T>

    protected abstract fun buildDefaultBricks(): List<T>

    private fun <P> executeProvider(
        digit: Int,
        provider: GeometryProvider<P>,
        providerStore: DefaultProviderStore
    ) {
        val providerData = provider.getProviderData(digit, providerStore)
        providerStore.store<P>(provider.key, providerData)
    }

    private fun computeExecutionOrder(): List<GeometryProvider<*>> {
        if (providersRegistry.all { it.dependsOn.isEmpty() }) {
            return providersRegistry.toList()
        }

        val providersByKey = providersRegistry.associateBy { it.key }
        val visitedProviders = mutableMapOf<ProviderKey<*>, VisitState>()
        val orderedProvider = mutableListOf<GeometryProvider<*>>()

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
}