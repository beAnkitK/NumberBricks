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
import io.github.beankitk.numberbricks.data.RectCorners
import io.github.beankitk.numberbricks.data.lerp
import kotlin.math.min

@Immutable
data class Block(
    override val index: Int,
    override val position: Position,
    override val offset: Offset,
    override val size: Size,
    val corners: RectCorners
): Brick<Block> {

    private val asRect = Rect(offset, size)

    private val asRoundRect = RoundRect(
        asRect,
        corners.topLeft.radius,
        corners.topRight.radius,
        corners.bottomRight.radius,
        corners.bottomLeft.radius
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
            corners = RectCorners(
                topLeft = corners.topLeft.copy(corners.topLeft.radius * radius),
                topRight = corners.topRight.copy(corners.topRight.radius * radius),
                bottomRight = corners.bottomRight.copy(corners.bottomRight.radius * radius),
                bottomLeft = corners.bottomLeft.copy(corners.bottomLeft.radius * radius)
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
        corners = lerp(start.corners, end.corners, t)
    )
}