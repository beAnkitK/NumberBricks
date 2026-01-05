package io.github.beankitk.numberbricks.core.layout

interface LayoutScope {
    
    val currentDigit: Int
    
    val layoutInfo: LayoutInfo
    
    fun updateDigit(digit: Int)
    
    fun <T> hasProviderDataFor(key: ProviderKey<T>): Boolean
    
    fun <T> getProviderDataFor(key: ProviderKey<T>): List<T>
    
    fun <T> putProviderDataFor(key: ProviderKey<T>, data: List<T>)
    
    fun clearProviderData()
}

internal class LayoutScopeImpl(
    override val layoutInfo: LayoutInfo
): LayoutScope {
    private val providerStorage = mutableMapOf<ProviderKey<*>, List<*>>()
    
    override var currentDigit = 0
        private set
    
    override fun updateDigit(digit: Int) {
        if (currentDigit != digit) {
            providerStorage.clear()
        }
        currentDigit = digit
    }
    
    override fun <T> hasProviderDataFor(key: ProviderKey<T>): Boolean = 
        providerStorage.containsKey(key)
    
    //TODO refine this code
    @Suppress("UNCHECKED_CAST")
    override fun <T> getProviderDataFor(key: ProviderKey<T>): List<T> {
        return providerStorage[key] as? List<T> ?: error("No data present")
    }
    
    //TODO : check for input data to be of the brickount size
    override fun <T> putProviderDataFor(key: ProviderKey<T>, data: List<T>) {
        providerStorage[key] = data
    }
    
    override fun clearProviderData() {
         providerStorage.clear()
    }
}