package io.github.beankitk.numberbricks.core.layout

interface LayoutBuilder<T : BrickItem<T>> {
    
    val layoutScope: LayoutScope
    
    val layoutInfo: LayoutInfo
    
    fun brickDataFor(digit: Int): List<T>

    fun defaultBrickData(digit: Int): List<T>
    
    fun dispose()
}

abstract class BrickLayoutBuilder<T : BrickItem<T>>(
    layoutInfo: LayoutInfo
): LayoutBuilder<T> {

    private val providers = mutableMapOf<ProviderKey<*>, LayoutProvider<*>>()
    private val _layoutScope = LayoutScopeImpl(layoutInfo)

    override val layoutScope: LayoutScope
        get() = _layoutScope

    override val layoutInfo = layoutScope.layoutInfo

    protected fun <P> registerProvider(provider: LayoutProvider<P>) {
        require(providers.none { it.key == provider.key }) {
            "Provider with ${provider.key.id} already registered"
        }
        require(provider.matchesWith(layoutScope.layoutInfo)) {
            "Provider '${provider.key.id}' is incompatible with layout"
        }
        providers[provider.key] = provider
    }

    override fun brickDataFor(digit: Int): List<T> {
        layoutScope.updateDigit(digit)

        providers.forEach { key, provider ->
            val providerData = with(provider) { layoutScope.getOrComputeFor(digit) }
            //layoutScope.putProviderDataFor(key, providerData)
            
            @Suppress("UNCHECKED_CAST")
            layoutScope.putProviderDataFor(
                key as ProviderKey<Any?>,
                providerData as List<Any?>
            )
        }

        return buildBrickData(digit)
    }

    override fun dispose() {
        layoutScope.clearProviderData()
        providers.clear()
    }

    abstract override fun defaultBrickData(digit: Int): List<T>
    protected abstract fun buildBrickData(digit: Int): List<T>
}