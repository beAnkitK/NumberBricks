package io.github.beankitk.numberbricks.blockdigit.data.corners

import androidx.compose.ui.geometry.Rect
import io.github.beankitk.numberbricks.data.ShapeRadius

open class DefaultCorners private constructor(
    private val radius: ShapeRadius
): CornersProvider {

    private val cornerArray = Array(13) { radius }
    
    override fun radiusFor(digit: Int, bricks: Array<Rect>) = cornerArray
    
    companion object {
        val Zero = DefaultCorners(ShapeRadius.Zero)
        val Full = DefaultCorners(ShapeRadius.all(1f))
        
        fun uniform(cornerRadius: Float) = DefaultCorners(ShapeRadius.all(cornerRadius))
    }
}