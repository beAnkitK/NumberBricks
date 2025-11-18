package io.github.beankitk.numberbricks.blockdigit

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import io.github.beankitk.numberbricks.core.BrickLayout
import io.github.beankitk.numberbricks.core.data.LayoutData
import io.github.beankitk.numberbricks.core.data.asString
import io.github.beankitk.numberbricks.blockdigit.data.BlockData
import io.github.beankitk.numberbricks.blockdigit.data.BlockLayoutData
import io.github.beankitk.numberbricks.blockdigit.data.ProviderData
import io.github.beankitk.numberbricks.blockdigit.data.asString
import io.github.beankitk.numberbricks.blockdigit.data.corners.CornersProvider
import io.github.beankitk.numberbricks.blockdigit.data.offset.OffsetProvider
import io.github.beankitk.numberbricks.blockdigit.data.size.SizeProvider

class BlockLayout(
    val layoutData: BlockLayoutData,
    val offsetProvider: OffsetProvider,
    val sizeProvider: SizeProvider,
    val cornersProvider: CornersProvider,
) : BrickLayout<BlockData> {

    override val rows = layoutData.rows
    override val cols = layoutData.cols
    override val brickCount = layoutData.brickCount
    
    init {
        validateProvider("offsetProvider", offsetProvider)
        validateProvider("sizeProvider", sizeProvider)
        validateProvider("cornersProvider", cornersProvider)
    }
    
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

private fun LayoutData.validateProvider(
    providerName: String,
    providerData: ProviderData
) {
    val layoutData = this as LayoutData
    require(layoutData matches providerData) {
        """
        $providerName data does not match with layout data.
        LayoutData:   ${layoutData.asString()}
        ProviderData: ${providerData.asString()}
        """.trimIndent()
    }
}

private infix fun LayoutData.matches(other: ProviderData): Boolean {
    if (other.isAdaptive) return true
    return (this.rows == other.rows &&
        this.cols == other.cols &&
        this.brickCount == other.brickCount)
}
