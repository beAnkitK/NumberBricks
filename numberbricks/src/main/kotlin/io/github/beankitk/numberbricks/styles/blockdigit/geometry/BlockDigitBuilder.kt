package io.github.beankitk.numberbricks.blockdigit.geometry

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import io.github.beankitk.numberbricks.core.geometry.BaseDigitBuilder
import io.github.beankitk.numberbricks.core.geometry.Position
import io.github.beankitk.numberbricks.core.geometry.ProviderStore
import io.github.beankitk.numberbricks.blockdigit.geometry.corners.CornersProvider
import io.github.beankitk.numberbricks.blockdigit.geometry.offset.OffsetProvider
import io.github.beankitk.numberbricks.blockdigit.geometry.position.PositionProvider
import io.github.beankitk.numberbricks.blockdigit.geometry.size.SizeProvider
import io.github.beankitk.numberbricks.data.ShapeRadius

class BlockDigitBuilder(
    private val positionProvider: PositionProvider,
    private val offsetProvider: OffsetProvider,
    private val sizeProvider: SizeProvider,
    private val cornersProvider: CornersProvider,
) : BaseDigitBuilder<Block>() {

    override fun bindProviders() {
        registerProvider(positionProvider)
        registerProvider(offsetProvider)
        registerProvider(sizeProvider)
        registerProvider(cornersProvider)
    }

    protected override fun buildBricks(digit: Int, store: ProviderStore): List<Block> {
        val positionList = store.get<Position>(PositionProvider.key)
        val offsetList = store.get<Offset>(OffsetProvider.key)
        val sizeList = store.get<Size>(SizeProvider.key)
        val cornersRadiusList = store.get<ShapeRadius>(CornersProvider.key)

        return List(properties.config.bricks) { index ->
            Block(
                index = index,
                position = positionList[index],
                offset = offsetList[index],
                size = sizeList[index],
                cornerRadius = cornersRadiusList[index]
            )
        }
    }
}