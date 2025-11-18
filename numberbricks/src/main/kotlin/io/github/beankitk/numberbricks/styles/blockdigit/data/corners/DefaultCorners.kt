package io.github.beankitk.numberbricks.blockdigit.data.corners

import androidx.compose.ui.geometry.Rect
import io.github.beankitk.numberbricks.blockdigit.data.createArray
import io.github.beankitk.numberbricks.data.ShapeRadius

open class DefaultCorners private constructor(
    private val radius: ShapeRadius
): CornersProvider {
    
    private val cornerArray = Array(13) { radius }
    
    final override val rows = 0
    final override val cols = 0
    final override val brickCount = 0
    final override val isAdaptive = true
    
    override fun radiusFor(digit: Int, bricks: Array<Rect>) = cornerArray
    
    companion object {
        val zero = DefaultCorners(ShapeRadius.Zero)
        val full = DefaultCorners(ShapeRadius.all(1f))
        
        fun uniform(cornerRadius: Float) = DefaultCorners(ShapeRadius.all(cornerRadius))
    }
}