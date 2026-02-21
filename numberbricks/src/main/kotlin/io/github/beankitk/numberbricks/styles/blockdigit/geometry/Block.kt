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

@Immutable
data class Block(
    override val index: Int,
    override val position: Position,
    override val offset: Offset,
    override val size: Size,
    val cornerRadius: ShapeRadius
): Brick<Block> {

    private val asRect = Rect(offset, size)

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

    fun toRect() = asRect

    fun toRoundRect() = asRoundRect
}

fun lerp(start: Block, end: Block, t: Float): Block {
    return Block(
        index = end.index,
        position = end.position,
        offset = lerp(start.offset, end.offset, t),
        size = lerp(start.size, end.size, t),
        cornerRadius = lerp(start.cornerRadius, end.cornerRadius, t)
    )
}