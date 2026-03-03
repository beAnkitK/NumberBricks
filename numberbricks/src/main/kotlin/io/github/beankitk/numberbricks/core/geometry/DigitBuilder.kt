package io.github.beankitk.numberbricks.core.geometry

interface DigitBuilder<T : Brick<T>> {

    fun construct(
        digitGridSpec: GridSpec,
        geometryProps: GeometryProps
    )

    fun bindProviders()

    fun buildBricks(digit: Int): List<T>

    //TODO: Add digit parameter to return digit aware default bricks
    fun buildDefaultBricks(): List<T>

    fun destruct()
}

abstract class BaseDigitBuilder<T : Brick<T>> : DigitBuilder<T> {

    private var isConstructed = false
    private val providersRegistry = mutableListOf<GeometryProvider<*>>()
    private var executionOrder: List<GeometryProvider<*>> = emptyList()

    protected lateinit var digitGridSpec: GridSpec
        private set

    protected lateinit var geometryProps: GeometryProps
        private set

    override fun construct(
        digitGridSpec: GridSpec,
        geometryProps: GeometryProps
    ) {
        require(!isConstructed) { "Builder already constructed" }
        this.digitGridSpec = digitGridSpec
        this.geometryProps = geometryProps
        bindProviders()
        executionOrder = computeExecutionOrder()
        providersRegistry.forEach { it.attachWith(digitGridSpec, geometryProps) }
        isConstructed = true
    }

    final override fun buildBricks(digit: Int): List<T> {
        checkConstructed()
        require(digit in 0..9 || digit == -1) {
            "Builder accepts digit values from 0 to 9 to construct bricks and -1 for default bricks, but got $digit"
        }

        return ProviderScope(digit).use { providerScope ->
            executionOrder.forEach { provider -> executeProvider(provider, providerScope) }
            providerScope.assembleBricks()
        }
    }

    final override fun buildDefaultBricks(): List<T> {
        checkConstructed()
        return assembleDefaultBricks()
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

        val providerConsent = provider.matchesWith(digitGridSpec)
        if (providerConsent.hasRejected()) {
            error(providerConsent.getRejectionReason() ?:
                "Provider '${provider.key}' incompatible with layout")
        }

        providersRegistry.add(provider)
    }

    protected abstract fun ProviderScope.assembleBricks(): List<T>

    protected abstract fun assembleDefaultBricks(): List<T>

    private fun <P> executeProvider(
        provider: GeometryProvider<P>,
        providerScope: DefaultProviderScope
    ) {
        providerScope.withProvider(provider) {
            val providerResult = providerScope.provideData()
            check(providerResult.size == digitGridSpec.bricks) {
                "Provider result must have ${digitGridSpec.bricks} size, but was ${providerResult.size} for ${provider.key}"
            }
            providerScope.commitResult<P>(key, providerResult)
        }
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