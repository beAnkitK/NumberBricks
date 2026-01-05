package io.github.beankitk.numberbricks.core.layout

//TODO figtue out way for updating the provider data from layout other than matcheswith
interface LayoutProvider<T> {

    val key: ProviderKey<T>

    val dependsOn: Set<ProviderKey<*>>
    
    val isAdaptive: Boolean
    
    val rowsCount: Int
    
    val colsCount: Int
    
    val brickCount: Int
    
    fun matchesWith(layoutInfo: LayoutInfo): Boolean

    fun LayoutScope.getOrComputeFor(digit: Int): List<T>
}

abstract class AdaptiveProvider<T>: LayoutProvider<T> {

    final override val isAdaptive = true

    override var rowsCount = 0
    override var colsCount = 0
    override var brickCount = 0
    
    override fun matchesWith(layoutInfo: LayoutInfo): Boolean {
        rowsCount = layoutInfo.rowsCount
        colsCount = layoutInfo.colsCount
        brickCount = layoutInfo.brickCount
        return true
    }
}

abstract class FixedProvider<T>: LayoutProvider<T> {

    final override val isAdaptive = false

    override fun matchesWith(layoutInfo: LayoutInfo): Boolean {
        return (rowsCount == layoutInfo.rowsCount &&
               colsCount == layoutInfo.colsCount &&
               brickCount == layoutInfo.brickCount)
    }
}