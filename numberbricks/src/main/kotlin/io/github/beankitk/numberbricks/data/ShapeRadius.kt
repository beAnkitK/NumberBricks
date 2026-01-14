package io.github.beankitk.numberbricks.data

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.lerp

@Stable
data class ShapeRadius(
    val topLeft: CornerRadius = CornerRadius.Zero,
    val topRight: CornerRadius = CornerRadius.Zero,
    val bottomRight: CornerRadius = CornerRadius.Zero,
    val bottomLeft: CornerRadius = CornerRadius.Zero,
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
        @Stable val Zero = all(CornerRadius.Zero)

        @Stable
        fun all(radius: CornerRadius) = ShapeRadius(
            radius, radius, radius, radius
        )

        @Stable
        fun all(radius: Float) = ShapeRadius(
            CornerRadius(radius, radius),
            CornerRadius(radius, radius),
            CornerRadius(radius, radius),
            CornerRadius(radius, radius)
        )
    }
}

@Stable
fun ShapeRadius(tl: Float, tr: Float, br: Float, bl: Float) = 
    ShapeRadius(
        topLeft = CornerRadius(tl),
        topRight = CornerRadius(tr),
        bottomRight = CornerRadius(br),
        bottomLeft = CornerRadius(bl)
    )

@Stable
fun lerp(start: ShapeRadius, end: ShapeRadius, t: Float) =
    ShapeRadius(
        topLeft = lerp(start.topLeft, end.topLeft, t),
        topRight = lerp(start.topRight, end.topRight, t),
        bottomRight = lerp(start.bottomRight, end.bottomRight, t),
        bottomLeft = lerp(start.bottomLeft, end.bottomLeft, t)
    )