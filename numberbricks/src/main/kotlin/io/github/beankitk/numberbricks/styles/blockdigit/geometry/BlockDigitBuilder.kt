package io.github.beankitk.numberbricks.blockdigit.geometry

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import io.github.beankitk.numberbricks.core.geometry.BaseDigitBuilder
import io.github.beankitk.numberbricks.core.geometry.GeometryProvider
import io.github.beankitk.numberbricks.core.geometry.Position
import io.github.beankitk.numberbricks.core.geometry.ProviderScope
import io.github.beankitk.numberbricks.blockdigit.geometry.corners.CornersProvider
import io.github.beankitk.numberbricks.blockdigit.geometry.offset.OffsetProvider
import io.github.beankitk.numberbricks.blockdigit.geometry.position.PositionProvider
import io.github.beankitk.numberbricks.blockdigit.geometry.size.SizeProvider
import io.github.beankitk.numberbricks.data.RectCorners

/**
 * Builds the brick model for block-based digit geometry.
 *
 * A concrete [BaseDigitBuilder] implementation for block-style digits. It performs
 * geometry composition by coordinating four core [GeometryProvider]s that provide
 * the required data to construct each [Block] for a given digit:
 *
 * 1. [PositionProvider] -> grid position
 * 2. [OffsetProvider] -> offset
 * 3. [SizeProvider] -> dimensions
 * 4. [CornersProvider] -> corner styling
 *
 * The collected geometry from these providers is assembled into the final
 * brick model representing the digit.
 *
 * @property positionProvider Provides grid position for each block
 * @property offsetProvider Provides offset values for each block
 * @property sizeProvider Provides size values for each block
 * @property cornersProvider Provides corner styling for each block
 */
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

        return List(digitGridSpec.brickCount) { index ->
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
        return List(digitGridSpec.brickCount) { index ->
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