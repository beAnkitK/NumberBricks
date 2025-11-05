package io.github.beankitk.numberbricks.data

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.lerp

data class ShapeRadius(
    val topLeft: CornerRadius,
    val topRight: CornerRadius,
    val bottomRight: CornerRadius,
    val bottomLeft: CornerRadius
) {
    @Stable
    inline fun isCircular(): Boolean {
        return topLeft.isCircular() && topRight.isCircular() && bottomRight.isCircular() && bottomLeft.isCircular()
    }
    
    @Stable
    inline fun isZero(): Boolean {
        return topLeft.isZero() && topRight.isZero() && bottomRight.isZero() && bottomLeft.isZero()
    }
    
    companion object {
        @Stable val Zero = all(0f)
        
        @Stable
        fun all(radius: Float) = ShapeRadius(
            CornerRadius(radius, radius),
            CornerRadius(radius, radius),
            CornerRadius(radius, radius),
            CornerRadius(radius, radius)
        )
    }
}

fun ShapeRadius(tl: Float, tr: Float, br: Float, bl: Float) = 
    ShapeRadius(
        topLeft = CornerRadius(tl),
        topRight = CornerRadius(tr),
        bottomRight = CornerRadius(br),
        bottomLeft = CornerRadius(bl)
    )

fun lerp(start: ShapeRadius, end: ShapeRadius, t: Float) =
    ShapeRadius(
        topLeft = lerp(start.topLeft, end.topLeft, t),
        topRight = lerp(start.topRight, end.topRight, t),
        bottomRight = lerp(start.bottomRight, end.bottomRight, t),
        bottomLeft = lerp(start.bottomLeft, end.bottomLeft, t)
    )