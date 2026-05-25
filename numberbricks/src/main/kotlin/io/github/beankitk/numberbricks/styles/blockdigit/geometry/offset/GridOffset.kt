package io.github.beankitk.numberbricks.blockdigit.geometry.offset

import androidx.compose.ui.geometry.Offset
import io.github.beankitk.numberbricks.blockdigit.geometry.position.PositionProvider
import io.github.beankitk.numberbricks.blockdigit.geometry.size.SizeProvider
import io.github.beankitk.numberbricks.blockdigit.geometry.size.VariableSize
import io.github.beankitk.numberbricks.core.geometry.Position
import io.github.beankitk.numberbricks.core.geometry.ProviderKey
import io.github.beankitk.numberbricks.core.geometry.ProviderScope
import io.github.beankitk.numberbricks.core.geometry.buildProviderData

/**
 * Provides offsets to position blocks when grid cells have non-uniform dimensions.
 *
 * This [GridOffset] works with [VariableSize], reading column-width and row-height meta to
 * resolve the starting offset of each block's grid cell. If size meta is not available, it
 * falls back to direct position-based offsets (col -> x, row -> y), matching [DirectOffset] behavior.
 * The resulting offset is then passed through [transformOffset], when provided, to produce the final
 * offset.
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
 * @param transformOffset Optional transformation applied to the computed offset for each block.
 *   The transformation receives the digit, the block's position, and the base offset calculated
 *   by this provider, and returns the final offset.
 * @see VariableSize
 * @see DirectOffset
 */
class GridOffset(
    private val transformOffset:
        ((digit: Int, position: Position, baseOffset: Offset) -> Offset)? = null
) : OffsetProvider.Adaptive() {

    override val key: OffsetProvider.Key
        get() = GridOffset.Key

    override val dependsOn: Set<ProviderKey<*>> = setOf(PositionProvider.Key, VariableSize.Key)

    override fun ProviderScope.provideData(): List<Offset> {
        val positions = resultOf<Position>(PositionProvider.Key)
        val eachColWidth = metaOf<FloatArray>(VariableSize.ColWidths)
        val eachRowHeight = metaOf<FloatArray>(VariableSize.RowHeights)

        // Fallback to DirectOffset behavior when meta values unavailable
        if (eachColWidth == null || eachRowHeight == null) {
            return positions.map { position ->
                val baseOffset = Offset(x = position.col.toFloat(), y = position.row.toFloat())
                transformOffset?.invoke(digit, position, baseOffset) ?: baseOffset
            }
        }

        // Build offset lookup matrix from dimension meta values
        val eachColStartX = toPrefix(eachColWidth)
        val eachRowStartY = toPrefix(eachRowHeight)

        return buildProviderData { index ->
            val position = positions[index]
            val baseOffset = Offset(x = eachColStartX[position.col], y = eachRowStartY[position.row])

            transformOffset?.invoke(digit, position, baseOffset) ?: baseOffset
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

    /** Key identifying the [GridOffset] provider within the [OffsetProvider] family. */
    object Key : OffsetProvider.Key {
        override fun toString(): String = "DirectOffset"
    }
}
