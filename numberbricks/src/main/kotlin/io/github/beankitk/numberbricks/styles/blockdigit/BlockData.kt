package io.github.beankitk.numberbricks.blockdigit

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.lerp
import io.github.beankitk.numberbricks.core.BrickData
import io.github.beankitk.numberbricks.data.ShapeRadius
import io.github.beankitk.numberbricks.data.lerp
import kotlin.math.min

class BlockData(
    val index: Int,
    val position: Offset,
    val brickSize: Size,
    val cornerRadius: ShapeRadius
): BrickData<BlockData> {

    override fun interpolateBySize(end: BlockData, progress: Float, size: Size,): BlockData {
        val width = size.width
        val height = size.height
        
        val brickWidth = size.width / 3
        val brickHeight = size.width / 5
        
        val radiusSize = min(brickWidth, brickHeight)
        
        val animatedPos = lerp(position, end.position, progress)
        val animatedSize = lerp(brickSize, end.brickSize, progress)
        val animatedRadius = lerp(cornerRadius, end.cornerRadius, progress)
        val topLeft = animatedRadius.topLeft * radiusSize
        val topRight = animatedRadius.topRight * radiusSize
        val bottomRight = animatedRadius.bottomRight * radiusSize
        val bottomLeft = animatedRadius.bottomLeft * radiusSize
        
        return BlockData(
            index = index,
            position = Offset(animatedPos.x * width, animatedPos.y * height),
            brickSize = Size(animatedSize.width * width, animatedSize.height * height),
            cornerRadius = ShapeRadius(
                topLeft = topLeft,
                topRight = topRight,
                bottomRight = bottomRight,
                bottomLeft = bottomLeft
            )
        )
    }
}