package io.github.beankitk.numberbricks.blockdigit.data.corners

import io.github.beankitk.numberbricks.data.ShapeRadius

open class DefaultCorners private constructor(
    private val radius: ShapeRadius
): CornersProvider {

    override fun radiusFor(index: Int, digit: Int) = radius
    
    companion object {
        val Zero = DefaultCorners(ShapeRadius.Zero)
        val Full = DefaultCorners(ShapeRadius.all(1f))
        
        fun uniform(cornerRadius: Float) = DefaultCorners(ShapeRadius.all(cornerRadius))
    }
}