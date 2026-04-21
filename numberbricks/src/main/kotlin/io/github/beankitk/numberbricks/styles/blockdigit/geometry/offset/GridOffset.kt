@file:OptIn(ExperimentalProviderMetaApi::class)

package io.github.beankitk.numberbricks.blockdigit.geometry.offset

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import io.github.beankitk.numberbricks.blockdigit.geometry.position.PositionProvider
import io.github.beankitk.numberbricks.blockdigit.geometry.size.VariableSize
import io.github.beankitk.numberbricks.blockdigit.geometry.size.VariableSize.Meta
import io.github.beankitk.numberbricks.blockdigit.geometry.size.SizeProvider
import io.github.beankitk.numberbricks.core.geometry.ExperimentalProviderMetaApi
import io.github.beankitk.numberbricks.core.geometry.Position
import io.github.beankitk.numberbricks.core.geometry.ProviderScope
import io.github.beankitk.numberbricks.core.geometry.ProviderKey
import io.github.beankitk.numberbricks.core.geometry.buildProviderData

/**
 * Provides offsets to position block when grid cells have non-uniform dimensions.
 *
 * This [OffsetProvider] works with [VariableSize], reading column width and
 * row height metadata to resolve the starting offset of each block’s grid cell.
 *
 * If size metadata is not available, it falls back to direct position-based
 * offsets (col -> x, row -> y), behaving the same as [DirectOffset].
 *
 * Recommended for use with [VariableSize].
 *
 * Example with VariableSize:
 * ```text
 * Variable column widths: [1.5f, 1f, 0.5f]
 * Prefix sums: [0f, 1.5f, 2.5f]
 * Block at position (row=0, col=1) -> offset (x=1.5f, y=...)
 * ```
 *
 * A [GridOffsetModifier] can be provided to adjust the computed offset per
 * block. Use [Fixed] for no modification.
 *
 * @param offsetModifier Modifier applied to computed offsets
 * @see DirectOffset
 * @see GridOffsetModifier
 * @see Fixed
 */
class GridOffset(
    private val offsetModifier: GridOffsetModifier
) : OffsetProvider.Adaptive() {

    override val dependsOn: Set<ProviderKey<*>>
        get() = setOf(PositionProvider.key, SizeProvider.key)

    override fun ProviderScope.provideData(): List<Offset> {
        val positions = resultOf<Position>(PositionProvider.key)
        val eachColWidth = metaOf<FloatArray>(Meta.ColWidths)
        val eachRowHeight = metaOf<FloatArray>(Meta.RowHeights)

        // Fallback to DirectOffset behavior when metadata unavailable
        if (eachColWidth == null || eachRowHeight == null) {
            return positions.map { position ->
                Offset(
                    x = position.col.toFloat(),
                    y = position.row.toFloat()
                )
            }
        }

        // Build offset lookup matrix from dimension metadata
        val eachColStartX = toPrefix(eachColWidth)
        val eachRowStartY = toPrefix(eachRowHeight)

        return buildProviderData { index ->
            val position = positions[index]
            val offset = Offset(
                x = eachColStartX[position.col],
                y = eachRowStartY[position.row]
            )

            offsetModifier.modify(digit, position, offset)
        }
    }

    private fun toPrefix(dimens: FloatArray): FloatArray {
        var cumulative = 0f
        return FloatArray(dimens.size) { idx ->
            val element = cumulative
            cumulative += dimens[idx]
            element
        }
    }

    companion object {
        /** A [GridOffset] that applies no modification to computed offsets. */
        val Fixed: GridOffset = GridOffset { _, _, baseOffset -> baseOffset }
    }
}

/** Allows modifying grid-computed offsets for each block. */
fun interface GridOffsetModifier {
    /**
     * Modifies the base offset for a block.
     *
     * @param digit The digit being composed (0–9, or -1 for default)
     * @param position The block's grid position
     * @param baseOffset The computed offset before modification
     * @return The final offset for the block
     */
    fun modify(digit: Int, position: Position, baseOffset: Offset): Offset
}