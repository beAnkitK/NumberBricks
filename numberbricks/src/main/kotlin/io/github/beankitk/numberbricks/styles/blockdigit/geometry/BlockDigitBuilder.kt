package io.github.beankitk.numberbricks.blockdigit.geometry

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import io.github.beankitk.numberbricks.core.geometry.BaseDigitBuilder
import io.github.beankitk.numberbricks.core.geometry.ProviderStore
import io.github.beankitk.numberbricks.blockdigit.geometry.corners.CornersProvider
import io.github.beankitk.numberbricks.blockdigit.geometry.offset.OffsetProvider
import io.github.beankitk.numberbricks.blockdigit.geometry.size.SizeProvider
import io.github.beankitk.numberbricks.data.ShapeRadius

/**
 * Digit builder for block-style digit representations.
 *
 * Constructs [Block] instances by coordinating three geometry providers:
 * - [OffsetProvider]: Computes brick offset
 * - [SizeProvider]: Computes brick dimensions
 * - [CornersProvider]: Computes brick corner radii
 *
 * The builder assembles data from these providers to create fully-specified
 * rounded rectangle bricks for each digit.
 *
 * @property offsetProvider Provider that computes brick positions
 * @property sizeProvider Provider that computes brick sizes
 * @property cornersProvider Provider that computes corner radii
 */
class BlockDigitBuilder(
    private val offsetProvider: OffsetProvider,
    private val sizeProvider: SizeProvider,
    private val cornersProvider: CornersProvider,
) : BaseDigitBuilder<Block>() {

    override fun bindProviders() {
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
        val offsetList = store.get<Offset>(OffsetProvider.key)
        val sizeList = store.get<Size>(SizeProvider.key)
        val cornersRadiusList = store.get<ShapeRadius>(CornersProvider.key)

        return List(properties.config.bricks) { index ->
            Block(
                index = index,
                offset = offsetList[index],
                size = sizeList[index],
                cornerRadius = cornersRadiusList[index]
            )
        }
    }
}