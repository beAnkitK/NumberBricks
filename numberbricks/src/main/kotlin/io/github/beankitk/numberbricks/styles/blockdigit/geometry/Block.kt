package io.github.beankitk.numberbricks.blockdigit.geometry

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.lerp
import io.github.beankitk.numberbricks.core.geometry.Brick
import io.github.beankitk.numberbricks.core.geometry.Position
import io.github.beankitk.numberbricks.data.ShapeRadius
import io.github.beankitk.numberbricks.data.lerp
import kotlin.math.min

 /**
 * A rectangular brick with rounded corners for block-style digit display.
 *
 * Represents a single visual element in a block digit layout. It extends [Brick] with corner radius
 * to support rounded rectangle drawing. Used in block digit styles, here, all geometric properties
 * ([offset], [size], [cornerRadius]) are specified in a normalized coordinate system relative to 1f
 * where 1f represents one grid cell. All properties are stored as grid-relative values and scaled to
 * pixels during rendering.
 *
 * **Coordinate System:**
 * Grid: 5 rows × 3 cols
 * Canvas: 200dp height × 120dp width
 * Unit brick size: width = 120dp/3 = 40dp, height = 200dp/5 = 40dp
 *
 * Example brick with normalized values:
 * ```
 * // Normalized values
 * offset = Offset(2.5f, 3f)
 * size = Size(1.5f, 1f)
 * cornerRadius = 0.8f
 *
 * // After scaling (rendered values)
 * actualOffset = (100dp, 120dp)      // 2.5f × 40dp, 3f × 40dp
 * actualSize = (60dp, 40dp)         // 1.5f × 40dp, 1f × 40dp
 * actualRadius = 32dp               // 0.8f × min(60dp, 40dp)
 * ```
 *
 * **Property Ranges:**
 * - **index**: [0, brickCount)
 * - **position**: row ∈ [0, rows), cols ∈ [0, cols)
 * - **offset**: x ∈ [0f, cols], y ∈ [0f, rows]
 * - **size**: width ∈ [0f, cols], height ∈ [0f, rows]
 * - **cornerRadius**: [0f, 1f]
 *   - 0f = sharp corners
 *   - 1f = maximum rounding (clamped to min(width, height))
 *   - Values > 1f are treated as 1f
 *
 * **Note:**
 * 1. Only [offset], [size], and [cornerRadius] are subject to scaling. The [index] and
 *   [position] values represent absolute block values and are therefore not scaled.
 * 2. Negative values are not allowed for any property.
 *
 * @property index The brick's index in the ordered brick list
 * @property position The brick's position in the two dimensional digit layout/grid
 * @property offset The brick's top-left position in normalized grid coordinates
 * @property size The brick's dimensions in normalized grid units
 * @property cornerRadius The corner radii as percentages of the brick's actual size
 */
@Immutable
data class Block(
    override val index: Int,
    override val position: Position,
    override val offset: Offset,
    override val size: Size,
    val cornerRadius: ShapeRadius
): Brick<Block> {

    /** This block represented as a simple rectangle. */
    private val asRect = Rect(offset, size)

    /** This block represented as a rounded rectangle with corner radii applied. */
    private val asRoundRect = RoundRect(
        asRect,
        cornerRadius.topLeft,
        cornerRadius.topRight,
        cornerRadius.bottomRight,
        cornerRadius.bottomLeft
    )

    override fun scaledBy(totalSize: Size, brickSize: Size): Block {
        val width = brickSize.width
        val height = brickSize.height
        val radius = brickSize.minDimension

        return copy(
            offset = Offset(
                x = offset.x * width,
                y = offset.y * height
            ),
            size = Size(
                width = size.width * width,
                height = size.height * height
            ),
            cornerRadius = ShapeRadius(
                topLeft = cornerRadius.topLeft * radius,
                topRight = cornerRadius.topRight * radius,
                bottomRight = cornerRadius.bottomRight * radius,
                bottomLeft = cornerRadius.bottomLeft * radius
            )
        )
    }

    /**
     * Converts this block to a simple rectangle.
     *
     * @return A [Rect] with this block's offset and size
     */
    fun toRect() = asRect

    /**
     * Converts this block to a rounded rectangle.
     *
     * @return A [RoundRect] with this block's offset, size, and corner radii
     */
    fun toRoundRect() = asRoundRect
}

/**
 * Linearly interpolates between two blocks. The index of the resulting block
 *  is taken from the end block.
 *
 * @param start The starting block (at t = 0.0)
 * @param end The ending block (at t = 1.0)
 * @param t The interpolation fraction (typically 0.0 to 1.0)
 * @return The interpolated block
 */
fun lerp(start: Block, end: Block, t: Float): Block {
    return Block(
        index = end.index,
        position = end.position,
        offset = lerp(start.offset, end.offset, t),
        size = lerp(start.size, end.size, t),
        cornerRadius = lerp(start.cornerRadius, end.cornerRadius, t)
    )
}