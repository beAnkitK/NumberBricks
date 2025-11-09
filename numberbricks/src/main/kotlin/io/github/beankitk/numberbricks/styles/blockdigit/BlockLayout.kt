package io.github.beankitk.numberbricks.blockdigit

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
        return Array(brickCount) { index ->
            val offset = offsetProvider.offsetFor(index, digit)
            val size = sizeProvider.sizeFor(index, digit, offset)
            val cornerRadius = cornersProvider.radiusFor(index, digit)
            
            BlockData(
                index = index,
                size = size,
                position = offset,
                cornerRadius = cornerRadius
            )
        }
    }
    
    override fun defaultBrickData(digit: Int) = brickDataFor(-1)
}