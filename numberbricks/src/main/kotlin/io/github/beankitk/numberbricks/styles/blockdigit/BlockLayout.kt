package io.github.beankitk.numberbricks.blockdigit

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import io.github.beankitk.numberbricks.core.BrickLayout
import io.github.beankitk.numberbricks.blockdigit.data.corners.CornersProvider
import io.github.beankitk.numberbricks.blockdigit.data.offset.OffsetProvider
import io.github.beankitk.numberbricks.blockdigit.data.size.SizeProvider

class BlockLayout(
    val offsetProvider: OffsetProvider,
    val sizeProvider: SizeProvider,
    val cornersProvider: CornersProvider,
) : BrickLayout<BlockData> {

    override val rows = 5
    override val cols = 3
    override val brickCount = 13

    override fun brickDataFor(digit: Int): Array<BlockData> {
        val blockRects = Array(brickCount) { index ->
            val offset = offsetProvider.offsetFor(index, digit)
            val size = sizeProvider.sizeFor(index, digit, offset)
            Rect(offset, size)
        }
        val cornersArray = cornersProvider.radiusFor(digit, blockRects)
        
        return Array(brickCount) { index ->
            BlockData(
                index = index,
                size = blockRects[index].size,
                position = blockRects[index].topLeft,
                cornerRadius = cornersArray[index]
            )
        }
    }
    
    override fun defaultBrickData(digit: Int) = brickDataFor(-1)
}