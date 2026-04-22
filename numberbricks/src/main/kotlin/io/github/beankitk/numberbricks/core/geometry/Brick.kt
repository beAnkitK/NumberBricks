package io.github.beankitk.numberbricks.core.geometry

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

/**
 * Defines the fundamental unit of digit geometry, encapsulating the data required to compose and
 * render a digit.
 *
 * The geometry of a digit is defined using a grid-based layout where each cell maps to a [Brick].
 * The complete geometry of a single digit is formed by the ordered collection of these bricks,
 * called the brick model of digit.
 *
 * This interface models geometry only and performs no drawing. It encapsulates the data required to
 * render a digit while separating:
 * - Structural layout: canonical grid position ([position])
 * - Rendered state: actual placement and dimensions ([offset], [size])
 *
 * Each brick has a stable identity defined by its [index], corresponding to its position in the
 * ordered collection of bricks for a digit. This identity must remain constant across animations
 * and state transitions.
 *
 * A brick defines:
 * 1. Its canonical grid position (row and column)
 * 2. Its rendered offset relative to the digit origin (top-left)
 * 3. Its rendered size
 *
 * Implementations may extend this contract with additional metadata required for styling,
 * rendering, or animation systems.
 *
 * @param T The concrete brick type
 */
interface Brick<T : Brick<T>> {

    /**
     * Identifies this brick within a digit by its position in the ordered collection of bricks from
     * [DigitBuilder]. Remains stable in the range `0 until total bricks` across animations.
     */
    val index: Int

    /**
     * Defines the canonical (row, column) position in the digit grid. Always represents the final
     * layout position, independent of rendering or animation.
     */
    val position: Position

    /**
     * Defines the top-left coordinate in rendered space relative to the digit origin. Used for
     * actual drawing and may change during animations.
     */
    val offset: Offset

    /**
     * Defines the rendered width and height of the brick. This may differ from grid cell size and
     * can change during animations.
     */
    val size: Size

    /**
     * Returns a scaled copy of this brick for the provided layout dimensions.
     *
     * This is a temporary API used during animation transitions and will be removed once
     * transformation logic is externalized from [Brick].
     *
     * @param totalSize The total size of the digit grid
     * @param brickSize The size of each cell defined by the digit grid
     * @return A new instance with updated rendering parameters
     * @see Brick
     */
    fun scaledBy(totalSize: Size, brickSize: Size): T
}
