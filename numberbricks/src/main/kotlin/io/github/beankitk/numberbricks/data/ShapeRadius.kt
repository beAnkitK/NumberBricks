package io.github.beankitk.numberbricks.data

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.lerp

/**
 * Defines corner radii for a rectangular shape.
 *
 * Stores individual [CornerRadius] values for each corner of a rectangle, allowing
 * independent control over corner rounding. Used for styling bricks and other visual
 * elements with rounded corners.
 *
 * @property topLeft Corner radius for the top-left corner
 * @property topRight Corner radius for the top-right corner
 * @property bottomRight Corner radius for the bottom-right corner
 * @property bottomLeft Corner radius for the bottom-left corner
 */
@Stable
data class ShapeRadius(
    val topLeft: CornerRadius = CornerRadius.Zero,
    val topRight: CornerRadius = CornerRadius.Zero,
    val bottomRight: CornerRadius = CornerRadius.Zero,
    val bottomLeft: CornerRadius = CornerRadius.Zero,
) {
    /**
     * Checks if all corners are circular (x radius equals y radius).
     *
     * @return true if all four corners have equal x and y radii
     */
    @Stable
    inline fun isCircular(): Boolean {
        return topLeft.isCircular() && topRight.isCircular() &&
               bottomRight.isCircular() && bottomLeft.isCircular()
    }

    /**
     * Checks if all corners have zero radius (sharp corners).
     *
     * @return true if all four corners are [CornerRadius.Zero]
     */
    @Stable
    inline fun isZero(): Boolean {
        return topLeft.isZero() && topRight.isZero() &&
               bottomRight.isZero() && bottomLeft.isZero()
    }

    companion object {
        /**
         * A shape radius with all corners set to zero (no rounding).
         */
        @Stable
        val Zero = all(CornerRadius.Zero)

        /**
         * Creates a shape radius with the same corner radius for all corners.
         *
         * @param radius The corner radius to apply to all four corners
         * @return A [ShapeRadius] with uniform corner radii
         */
        @Stable
        fun all(radius: CornerRadius) = ShapeRadius(
            radius, radius, radius, radius
        )

        /**
         * Creates a shape radius with the same circular radius for all corners.
         *
         * @param radius The radius value to apply uniformly (creates circular corners)
         * @return A [ShapeRadius] with uniform circular corner radii
         */
        @Stable
        fun all(radius: Float) = ShapeRadius(
            CornerRadius(radius, radius),
            CornerRadius(radius, radius),
            CornerRadius(radius, radius),
            CornerRadius(radius, radius)
        )
    }
}

/**
 * Creates a shape radius with individual corner radii specified as floats.
 *
 * Each corner receives a circular radius (x = y) with the specified value.
 *
 * @param tl Top-left corner radius
 * @param tr Top-right corner radius
 * @param br Bottom-right corner radius
 * @param bl Bottom-left corner radius
 * @return A [ShapeRadius] with the specified corner radii
 */
@Stable
fun ShapeRadius(tl: Float, tr: Float, br: Float, bl: Float) = 
    ShapeRadius(
        topLeft = CornerRadius(tl),
        topRight = CornerRadius(tr),
        bottomRight = CornerRadius(br),
        bottomLeft = CornerRadius(bl)
    )

/**
 * Linearly interpolates between two shape radii.
 *
 * Interpolates each corner independently based on the fraction [t].
 *
 * @param start The starting shape radius (at t = 0.0)
 * @param end The ending shape radius (at t = 1.0)
 * @param t The interpolation fraction (typically 0.0 to 1.0)
 * @return The interpolated shape radius
 */
@Stable
fun lerp(start: ShapeRadius, end: ShapeRadius, t: Float) =
    ShapeRadius(
        topLeft = lerp(start.topLeft, end.topLeft, t),
        topRight = lerp(start.topRight, end.topRight, t),
        bottomRight = lerp(start.bottomRight, end.bottomRight, t),
        bottomLeft = lerp(start.bottomLeft, end.bottomLeft, t)
    )