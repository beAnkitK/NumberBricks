package io.github.beankitk.numberbricks.block

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.lerp
import io.github.beankitk.numberbricks.core.BrickData
import io.github.beankitk.numberbricks.utils.ShapeRadius
import io.github.beankitk.numberbricks.utils.lerp

class BlockData(
    val index: Int,
    val position: Offset,
    val brickSize: Size,
    val cornerRadius: ShapeRadius
): BrickData<BlockData> {

    override fun interpolateBySize(end: BlockData, progress: Float, size: Size,): BlockData {
        val width = size.width
        val height = size.height
        
        val animatedPos = lerp(position, end.position, progress)
        val animatedSize = lerp(brickSize, end.brickSize, progress)
        val animatedRadius = lerp(cornerRadius, end.cornerRadius, progress)
        
        return BlockData(
            index = index,
            position = Offset(animatedPos.x * width, animatedPos.y * height),
            brickSize = Size(animatedSize.width * width, animatedSize.height * height),
            cornerRadius = ShapeRadius(
                topLeft = CornerRadius(
                    animatedRadius.topLeft.x * width,
                    animatedRadius.topLeft.y * height
                ),
                topRight = CornerRadius(
                    animatedRadius.topRight.x * width,
                    animatedRadius.topRight.y * height
                ),
                bottomRight = CornerRadius(
                    animatedRadius.bottomRight.x * width,
                    animatedRadius.bottomRight.y * height
                ),
                bottomLeft = CornerRadius(
                    animatedRadius.bottomLeft.x * width,
                    animatedRadius.bottomLeft.y * height
                )
            )
        )
    }
}