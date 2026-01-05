package io.github.beankitk.numberbricks.blockdigit.layout

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.lerp
import io.github.beankitk.numberbricks.core.layout.BrickItem
import io.github.beankitk.numberbricks.data.ShapeRadius
import io.github.beankitk.numberbricks.data.lerp
import kotlin.math.min

//TODO Rename as BlockItem
@Immutable
data class BlockData(
    override val index: Int,
    override val position: Offset,
    override val size: Size,
    val cornerRadius: ShapeRadius
): BrickItem<BlockData> {

    private val asRect = Rect(position, size)

    private val asRoundRect = RoundRect(
        asRect,
        cornerRadius.topLeft,
        cornerRadius.topRight,
        cornerRadius.bottomRight,
        cornerRadius.bottomLeft
    )

    override fun scaledBy(totalSize: Size, brickSize: Size): BlockData {
        val width = brickSize.width
        val height = brickSize.height
        val radius = brickSize.minDimension
        
        return copy(
            position = Offset(
                x = position.x * width,
                y = position.y * height
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

fun lerp(start: BlockData, end: BlockData, t: Float): BlockData {
    return BlockData(
        index = end.index,
        size = lerp(start.size, end.size, t),
        position = lerp(start.position, end.position, t),
        cornerRadius = lerp(start.cornerRadius, end.cornerRadius, t)
    )
}