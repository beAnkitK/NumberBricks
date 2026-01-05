package io.github.beankitk.numberbricks.blockdigit.layout.corners

import io.github.beankitk.numberbricks.core.layout.LayoutScope
import io.github.beankitk.numberbricks.core.layout.ProviderKey
import io.github.beankitk.numberbricks.data.ShapeRadius

open class DefaultCorners private constructor(
    private val cornerRadius: ShapeRadius
): CornersProvider.Adaptive() {

    private var cachedCornerRadius: List<ShapeRadius>? = null

    override val dependsOn = emptySet<ProviderKey<*>>()

    override fun LayoutScope.getOrComputeFor(digit: Int): List<ShapeRadius> {
        return cachedCornerRadius ?: List(brickCount) { cornerRadius }.also {
            cachedCornerRadius = it
        }
    }
    
    companion object {
        val zero = DefaultCorners(ShapeRadius.Zero)
        val full = DefaultCorners(ShapeRadius.all(1f))
        
        fun uniform(cornerRadius: Float) = DefaultCorners(ShapeRadius.all(cornerRadius))
    }
}