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

    protected override fun buildBricks(digit: Int, providerStore: ProviderStore): List<Block> {
        val positions = providerStore.get<Position>(PositionProvider.key)
        val offsets = providerStore.get<Offset>(OffsetProvider.key)
        val sizes = providerStore.get<Size>(SizeProvider.key)
        val cornersRadii = providerStore.get<ShapeRadius>(CornersProvider.key)

        return List(digitGridSpec.bricks) { index ->
            Block(
                index = index,
                position = positions[index],
                offset = offsets[index],
                size = sizes[index],
                cornerRadius = cornersRadii[index]
            )
        }
    }

    protected override fun buildDefaultBricks(): List<Block> {
        return List(digitGridSpec.bricks) { index ->
            Block(
                index = index,
                position = Position(row = 2, col = 1),
                offset = Offset(x = 1f, y = 2f),
                size = Size(width = 1f, height = 1f),
                cornerRadius = ShapeRadius.Zero
            )
        }
    }
}