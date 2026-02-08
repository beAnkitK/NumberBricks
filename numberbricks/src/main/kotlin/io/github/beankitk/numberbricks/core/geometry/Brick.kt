package io.github.beankitk.numberbricks.core.geometry

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

/**
 * Represents a single visual brick element in the number display system.
 *
 * A brick is the fundamental building block used to construct digit representations.
 * Each brick is uniquely identified by its index in the brick list.
 * It represents a frame in the grid having its own offset and a size.
 *
 * @param T The concrete brick implementation type.
 */
interface Brick<T : Brick<T>> {

    /**
     * This represents the key for this brick identified by its position in the ordered list
     * of bricks returned by the [DigitBuilder]. This value remains in the range
     * [0, brick-count). It must stay constant if the bricks are moving during animations.
     */
    val index: Int

    /**
     * The coordinates of this brick's top-left corner in [Offset] from the digit's top-left corner(i.e. origin).
     * This defines where the brick is drawm on canvas and may differ during animations.
     */
    val offset: Offset

    /**
     * The dimensions (width and height) of this brick in [Size]. This defines the size of [Brick]
     * rendered on screen and may differ during animations. This can also be used to control the size of digit.
     */
    val size: Size

    /**
     * Creates a scaled copy of this brick based on the provided dimensions.
     *
     * This is a temporary API used during animation transitions. It will be removed
     * once the animator implementation is complete. The brick should remain a pure
     * data holder without transformation logic in the final design.
     *
     * @param totalSize The total size of the digit canvas
     * @param brickSize The size of each brick defined by the digit canvas
     * @return A new brick instance with scaled offset and size.
     *
     * @see Brick
     */
    fun scaledBy(totalSize:Size, brickSize: Size): T
}