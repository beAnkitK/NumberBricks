package io.github.beankitk.numberbricks.blockdigit.layout

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import io.github.beankitk.numberbricks.core.layout.BrickLayoutBuilder
import io.github.beankitk.numberbricks.core.layout.LayoutInfo
import io.github.beankitk.numberbricks.blockdigit.layout.BlockData
import io.github.beankitk.numberbricks.blockdigit.layout.corners.CornersProvider
import io.github.beankitk.numberbricks.blockdigit.layout.offset.OffsetProvider
import io.github.beankitk.numberbricks.blockdigit.layout.size.SizeProvider
import io.github.beankitk.numberbricks.data.ShapeRadius

class BlockLayout(
    layoutInfo: LayoutInfo,
    offsetProvider: OffsetProvider,
    sizeProvider: SizeProvider,
    cornersProvider: CornersProvider,
) : BrickLayoutBuilder<BlockData>(layoutInfo) {

    init {
        registerProvider(offsetProvider)
        registerProvider(sizeProvider)
        registerProvider(cornersProvider)
    }
    
    val listSize = layoutInfo.brickCount
    
    override fun buildBrickData(digit: Int): List<BlockData> {
        val offsetList = layoutScope.getProviderDataFor<Offset>(OffsetProvider.key)
        val sizeList = layoutScope.getProviderDataFor<Size>(SizeProvider.key)
        val cornersRadiusList = layoutScope.getProviderDataFor<ShapeRadius>(CornersProvider.key)
        
        return List(listSize) { index ->
            BlockData(
                index = index,
                position = offsetList[index],
                size = sizeList[index],
                cornerRadius = cornersRadiusList[index]
            )
        }
    }
    
    override fun defaultBrickData(digit: Int) = brickDataFor(-1)
}