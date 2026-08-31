package io.github.beankitk.numberbricks.blockdigit.geometry

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.lerp
import io.github.beankitk.numberbricks.core.geometry.Brick
import io.github.beankitk.numberbricks.core.geometry.DigitBuilder
import io.github.beankitk.numberbricks.core.geometry.GeometryProvider
import io.github.beankitk.numberbricks.core.geometry.Position
import io.github.beankitk.numberbricks.data.RectCorners
import io.github.beankitk.numberbricks.data.lerp

/**
 * Defines the fundamental unit of brick model for block-based digit geometry
 *
 * Block represents the core building unit used to construct block-style digit geometry. It
 * implements the core [Brick] abstraction, extended with support for per-corner styling through
 * corner radius and shape.
 *
 * During geometry composition, all geometric properties — `offset`, `size`, and `corners` — must be
 * defined as grid-relative fractional values, where `1f` represents the size of a single grid cell
 * (i.e., `brickSize`). These fractional values are later scaled to actual dimensions based on the
 * resolved grid size. Scaling is currently handled by the renderer and is planned to be moved to
 * [DigitBuilder].
 *
 * [GeometryProvider] implementations contributing geometry for [Block] must provide all values in
 * this fractional form, within the valid range defined for each property:
 *
 * **Property Ranges:**
 *
 * 1. **index**: [0, brickCount)
 * 2. **position**: row ∈ [0, rows), col ∈ [0, cols)
 * 3. **offset**: x ∈ [0f, cols], y ∈ [0f, rows]
 * 4. **size**: width ∈ [0f, cols], height ∈ [0f, rows]
 * 5. **corners**: [0f, 1f] (applies to corner radius only)
 *     - 0f -> no corner radius
 *     - 1f -> maximum radius (clamped to min(width, height))
 *     - values > 1f are clamped to 1f
 *
 * **Notes:**
 * 1. Only `offset`, `size`, and corner radius (within `corners`) are subject to scaling. `index`
 *    and `position` define structural identity and remain unchanged.
 * 2. Negative values are not allowed for any property.
 *
 * **Example**
 *
 * ```kotlin
 * // Grid: 5 rows × 3 cols
 * // Canvas: 200dp height × 120dp width
 * // Unit GridCell size: width = 120dp / 3 = 40dp, height = 200dp / 5 = 40dp
 *
 * // Values defined during composition (fractional)
 * val offset = Offset(2.5f, 3f)
 * val size = Size(1.5f, 1f)
 * val cornerRadius = 0.8f
 *
 * // After scaling (rendered values)
 * actualOffset = DpOffset(100dp, 120dp)   // 2.5f × 40dp, 3f × 40dp
 * actualSize = DpSize(60dp, 40dp)         // 1.5f × 40dp, 1f × 40dp
 * actualCornerRadius = 32dp               // 0.8f × min(60dp, 40dp)
 * ```
 *
 * @property index The block's index in the brick model for a single digit
 * @property position The block's position in the digit grid
 * @property offset The block's top-left offset from digit origin
 * @property size The block's height and width relative to grid cell
 * @property corners The per-corner styling (radius and shape) of the block
 */
@Immutable
data class Block(
    override val index: Int,
    override val position: Position,
    override val offset: Offset,
    override val size: Size,
    val corners: RectCorners,
) : Brick<Block> {

    /** Cached rect representation of this block. */
    private val asRect = Rect(offset, size)

    /** Cached round rect representation of this block. */
    private val asRoundRect =
        RoundRect(
            asRect,
            corners.topLeft.radius,
            corners.topRight.radius,
            corners.bottomRight.radius,
            corners.bottomLeft.radius,
        )

    override fun scaledBy(totalSize: Size, brickSize: Size): Block {
        val width = brickSize.width
        val height = brickSize.height
        val radius = brickSize.minDimension

        return copy(
            offset = Offset(x = offset.x * width, y = offset.y * height),
            size = Size(width = size.width * width, height = size.height * height),
            corners =
                RectCorners(
                    topLeft = corners.topLeft.copy(corners.topLeft.radius * radius),
                    topRight = corners.topRight.copy(corners.topRight.radius * radius),
                    bottomRight = corners.bottomRight.copy(corners.bottomRight.radius * radius),
                    bottomLeft = corners.bottomLeft.copy(corners.bottomLeft.radius * radius),
                ),
        )
    }

    /**
     * Returns the rectangular representation of this block.
     *
     * @return A [Rect] computed from the current `offset` and `size`
     */
    fun toRect() = asRect

    /**
     * Returns the rounded rectangle representation of this block.
     *
     * @return A [RoundRect] computed from `offset`, `size`, and per-corner radii
     */
    fun toRoundRect() = asRoundRect
}

/**
 * Linearly interpolates between two blocks.
 *
 * The resulting block takes `index` and `position` from [end], while `offset`, `size`, and
 * `corners` are interpolated based on [t].
 *
 * @param start The start block (at t = 0.0)
 * @param end The end block (at t = 1.0)
 * @param t The interpolation fraction
 * @return The interpolated block
 */
fun lerp(start: Block, end: Block, t: Float): Block {
    return Block(
        index = end.index,
        position = end.position,
        offset = lerp(start.offset, end.offset, t),
        size = lerp(start.size, end.size, t),
        corners = lerp(start.corners, end.corners, t),
    )
}
