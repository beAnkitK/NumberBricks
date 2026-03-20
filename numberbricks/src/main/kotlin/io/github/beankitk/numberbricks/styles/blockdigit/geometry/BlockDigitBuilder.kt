package io.github.beankitk.numberbricks.blockdigit.geometry

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import io.github.beankitk.numberbricks.core.geometry.BaseDigitBuilder
import io.github.beankitk.numberbricks.core.geometry.Position
import io.github.beankitk.numberbricks.core.geometry.ProviderScope
import io.github.beankitk.numberbricks.blockdigit.geometry.corners.CornersProvider
import io.github.beankitk.numberbricks.blockdigit.geometry.offset.OffsetProvider
import io.github.beankitk.numberbricks.blockdigit.geometry.position.PositionProvider
import io.github.beankitk.numberbricks.blockdigit.geometry.size.SizeProvider
import io.github.beankitk.numberbricks.data.RectCorners

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

    protected override fun ProviderScope.assembleBricks(): List<Block> {
        val positions = resultOf<Position>(PositionProvider.key)
        val offsets = resultOf<Offset>(OffsetProvider.key)
        val sizes = resultOf<Size>(SizeProvider.key)
        val rectCorners = resultOf<RectCorners>(CornersProvider.key)

        return List(digitGridSpec.bricks) { index ->
            Block(
                index = index,
                position = positions[index],
                offset = offsets[index],
                size = sizes[index],
                corners = rectCorners[index]
            )
        }
    }

    protected override fun assembleDefaultBricks(): List<Block> {
        return List(digitGridSpec.bricks) { index ->
            Block(
                index = index,
                position = Position(row = 2, col = 1),
                offset = Offset(x = 1f, y = 2f),
                size = Size(width = 1f, height = 1f),
                corners = RectCorners.Sharp
            )
        }
    }
}