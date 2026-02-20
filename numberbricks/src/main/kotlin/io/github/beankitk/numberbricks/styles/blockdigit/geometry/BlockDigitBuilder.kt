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

/**
 * Digit builder for block-style digit representations.
 *
 * Constructs [Block] instances by coordinating four geometry providers:
 * - [PositionProvider]: Computes block position in grid
 * - [OffsetProvider]: Computes block offset
 * - [SizeProvider]: Computes block dimensions
 * - [CornersProvider]: Computes block corner radii
 *
 * The builder assembles data from these providers to create fully-specified
 * [Block]s for each digit.
 *
 * @property positionProvider Provider that computes block position
 * @property offsetProvider Provider that computes block offset
 * @property sizeProvider Provider that computes block size
 * @property cornersProvider Provider that computes block corner radii
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

    /**
     * Assembles blocks from provider outputs.
     *
     * Retrieves offset, size, and corner radius data from the provider store
     * and combines them into [Block] instances.
     *
     * @param digit The digit being constructed (0-9, or -1 for default)
     * @param store Provider store containing computed geometry data
     * @return List of blocks forming the digit representation
     */
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