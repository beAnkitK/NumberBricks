@file:Suppress("NOTHING_TO_INLINE")

package io.github.beankitk.numberbricks.data

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.lerp

/**
 * Describes the style of corner by its radius and shape.
 *
 * A corner style encapsulates both the size ([radius]) and geometric type ([shape]) of a corner,
 * allowing independent control over "how large" and "what shape" a corner should be rendered in a
 * unified way.
 *
 * @property radius The horizontal and vertical corner radii as [CornerRadius]
 * @property shape The type of geometric shape used for the corner
 * @see CornerShape
 * @see RectCorners
 */
@Stable
data class CornerStyle(val radius: CornerRadius, val shape: CornerShape) {
    /**
     * Checks if the corner has uniform radius in both x and y axes.
     *
     * Returns true when `radiusX == radiusY`, meaning the corner scales identically in both axes.
     * This is independent of [shape] type: both Square and Round corners can be uniform or
     * non-uniform.
     */
    @Stable
    inline fun isUniform(): Boolean {
        return radius.isCircular()
    }

    /** Returns true if the corner has zero radius in x, y or both. */
    @Stable
    inline fun isZero(): Boolean {
        return radius.isZero()
    }

    /** Returns `true` if the shape is [CornerShape.Square]. */
    @Stable
    inline fun isSquare(): Boolean {
        return shape == CornerShape.Square
    }

    /** Returns `true` if the shape is [CornerShape.Round]. */
    @Stable
    inline fun isRound(): Boolean {
        return shape == CornerShape.Round
    }

    companion object {
        /** A corner style with zero radius and square shape. */
        @Stable val None = CornerStyle(CornerRadius.Zero, CornerShape.Square)
    }
}

/**
 * Creates a [CornerStyle] from specified radius and shape.
 *
 * @param radius Horizontal radius, or uniform radius when [radiusY] is not specified
 * @param shape Corner shape for this style
 * @param radiusY Vertical radius (defaults to [radius])
 */
@Stable
fun CornerStyle(radius: Float, shape: CornerShape, radiusY: Float = radius) =
    CornerStyle(CornerRadius(radius, radiusY), shape)

/**
 * Linearly interpolates between two [CornerStyle].
 *
 * Radius is interpolated linearly, while shape transitions to the [end] shape.
 *
 * @param start Starting corner style
 * @param end Target corner style
 * @param t Interpolation fraction in the range `[0f, 1f]`
 */
// TODO: Add corner shape interpolation
@Stable
fun lerp(start: CornerStyle, end: CornerStyle, t: Float) =
    CornerStyle(
        radius = lerp(start.radius, end.radius, t),
        shape = if (t < 0.5f) start.shape else end.shape,
    )
