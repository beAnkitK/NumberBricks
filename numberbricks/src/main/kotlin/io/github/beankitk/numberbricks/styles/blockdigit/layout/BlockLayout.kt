package io.github.beankitk.numberbricks.blockdigit.layout

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import io.github.beankitk.numberbricks.core.layout.BrickLayoutBuilder
import io.github.beankitk.numberbricks.core.layout.LayoutProperties
import io.github.beankitk.numberbricks.core.layout.ProviderStore
import io.github.beankitk.numberbricks.blockdigit.layout.BlockItem
import io.github.beankitk.numberbricks.blockdigit.layout.corners.CornersProvider
import io.github.beankitk.numberbricks.blockdigit.layout.offset.OffsetProvider
import io.github.beankitk.numberbricks.blockdigit.layout.size.SizeProvider
import io.github.beankitk.numberbricks.data.ShapeRadius

class BlockLayout(
    private val offsetProvider: OffsetProvider,
    private val sizeProvider: SizeProvider,
    private val cornersProvider: CornersProvider,
) : BrickLayoutBuilder<BlockItem>() {

    override fun bindProviders(properties: LayoutProperties) {
        registerProvider(offsetProvider)
        registerProvider(sizeProvider)
        registerProvider(cornersProvider)
    }

    protected override fun buildBricksFor(digit: Int, store: ProviderStore): List<BlockItem> {
        val offsetList = store.get<Offset>(OffsetProvider.key)
        val sizeList = store.get<Size>(SizeProvider.key)
        val cornersRadiusList = store.get<ShapeRadius>(CornersProvider.key)

        return List(store.config.bricks) { index ->
            BlockItem(
                index = index,
                offset = offsetList[index],
                size = sizeList[index],
                cornerRadius = cornersRadiusList[index]
            )
        }
    }
}