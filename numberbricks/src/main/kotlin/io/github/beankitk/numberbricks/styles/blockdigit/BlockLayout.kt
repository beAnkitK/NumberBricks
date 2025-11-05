package io.github.beankitk.numberbricks.blockdigit

import androidx.compose.ui.geometry.Size
import io.github.beankitk.numberbricks.core.BrickLayout
import io.github.beankitk.numberbricks.blockdigit.data.offsets.BlockOffset
import io.github.beankitk.numberbricks.blockdigit.data.corners.BlockCorners

class BlockLayout(
    val blockOffset: BlockOffset<*>,
    val blockRadius: BlockCorners<*>
) : BrickLayout<BlockData> {

    override val rows = 5
    override val cols = 3
    override val brickCount = 13

    private val blockWidths = FloatArray(cols) { 1f / cols }
    private val blockHeights = FloatArray(rows) { 1f / rows }

    override fun getBrickData(digit: Int, isDefault: Boolean): Array<BlockData> {
        
        val finalDigit = if(isDefault) -1 else digit
        
        return Array(brickCount) { idx ->
            val offset = blockOffset.offsetFor(digit, idx)
            val cornerRadius = blockRadius.radiusFor(digit, idx)

            val size = Size(
                width = blockWidths[offset.x.toInt().coerceIn(0, cols - 1)],
                height = blockHeights[offset.y.toInt().coerceIn(0, rows - 1)]
            )

            BlockData(
                index = idx,
                position = offset,
                brickSize = size,
                cornerRadius = cornerRadius
            )
        }
    }
}