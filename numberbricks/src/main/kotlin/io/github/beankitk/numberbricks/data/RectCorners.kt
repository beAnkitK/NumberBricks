@file:Suppress("NOTHING_TO_INLINE")

package io.github.beankitk.numberbricks.data

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.CornerRadius

/**
 * Defines corner style for a rectangular shape for all corners.
 *
 * Stores individual [CornerStyle] values for each corner of a rectangle, allowing independent
 * control over corner radius and shape. Used for styling bricks and other visual elements with
 * rounded corners.
 *
 * @property topLeft Corner style for the top-left corner
 * @property topRight Corner style for the top-right corner
 * @property bottomRight Corner style for the bottom-right corner
 * @property bottomLeft Corner style for the bottom-left corner
 */
@Stable
data class RectCorners(
    val topLeft: CornerStyle = CornerStyle.None,
    val topRight: CornerStyle = CornerStyle.None,
    val bottomRight: CornerStyle = CornerStyle.None,
    val bottomLeft: CornerStyle = CornerStyle.None,
) {
    /** Returns true if all four corners have zero radius in x, y or both. */
    @Stable
    inline fun isZero(): Boolean {
        return topLeft.isZero() && topRight.isZero() && bottomRight.isZero() && bottomLeft.isZero()
    }

    /**
     * Returns `true` if all corners form a rectangular shape.
     *
     * A rectangle is defined as having all corners with [CornerShape.Square], meaning no rounding
     * or curvature is applied regardless of radius values.
     */
    @Stable
    inline fun isRect(): Boolean {
        return topLeft.isSquare() &&
            topRight.isSquare() &&
            bottomRight.isSquare() &&
            bottomLeft.isSquare()
    }

    /**
     * Returns `true` if all corners can form a rounded rectangle.
     *
     * A rounded rectangle supports corners of type [CornerShape.Square] and [CornerShape.Round],
     * allowing a mix of sharp and rounded edges.
     *
     * Note: With the current [CornerShape] types (`Square` and `Round`), this will always return
     * `true`. When additional corner types are introduced, they will be excluded by this check.
     */
    @Stable
    inline fun isRoundRect(): Boolean {
        return (topLeft.isSquare() || topLeft.isRound()) &&
            (topRight.isSquare() || topRight.isRound()) &&
            (bottomRight.isSquare() || bottomRight.isRound()) &&
            (bottomLeft.isSquare() || bottomLeft.isRound())
    }

    companion object {
        /** A [RectCorners] with all corners set to zero and square shape. */
        @Stable val Sharp = RectCorners(CornerStyle.None)
    }
}

/** Creates a [RectCorners] with given [CornerStyle] applied to all corners. */
@Stable
fun RectCorners(cornerStyle: CornerStyle): RectCorners =
    RectCorners(
        topLeft = cornerStyle,
        topRight = cornerStyle,
        bottomRight = cornerStyle,
        bottomLeft = cornerStyle,
    )

/** Creates a [RectCorners] with given [CornerRadius] and [CornerShape] applied to all corners. */
@Stable
fun RectCorners(radius: CornerRadius, shape: CornerShape): RectCorners =
    RectCorners(CornerStyle(radius, shape))

/**
 * Creates a [RectCorners] with given radiusX, shape and radiusY (default to radiusX for uniform
 * corner) applied to all corners.
 */
@Stable
fun RectCorners(radiusX: Float, shape: CornerShape, radiusY: Float = radiusX): RectCorners =
    RectCorners(CornerStyle(CornerRadius(radiusX, radiusY), shape))

/**
 * Creates a [RectCorners] with individual corner radii specified as floats and uniform shape for
 * all corners.
 *
 * @param tl Top-left corner radius
 * @param tr Top-right corner radius
 * @param br Bottom-right corner radius
 * @param bl Bottom-left corner radius
 * @param shape Uniform shape applied to all corners
 * @return A [RectCorners] with the specified corner radii and uniform shape
 */
@Stable
fun RectCorners(tl: Float, tr: Float, br: Float, bl: Float, shape: CornerShape) =
    RectCorners(
        topLeft = CornerStyle(tl, shape),
        topRight = CornerStyle(tr, shape),
        bottomRight = CornerStyle(br, shape),
        bottomLeft = CornerStyle(bl, shape),
    )

/**
 * Linearly interpolates between two [RectCorners].
 *
 * Interpolates each corner independently based on the fraction [t].
 *
 * @param start The starting RectCorners (at t = 0.0)
 * @param end The ending RectCorners (at t = 1.0)
 * @param t The interpolation fraction (typically 0.0 to 1.0)
 * @return The interpolated RectCorners
 */
@Stable
fun lerp(start: RectCorners, end: RectCorners, t: Float) =
    RectCorners(
        topLeft = lerp(start.topLeft, end.topLeft, t),
        topRight = lerp(start.topRight, end.topRight, t),
        bottomRight = lerp(start.bottomRight, end.bottomRight, t),
        bottomLeft = lerp(start.bottomLeft, end.bottomLeft, t),
    )
