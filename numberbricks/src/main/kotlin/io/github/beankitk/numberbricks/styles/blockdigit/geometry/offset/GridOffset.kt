package io.github.beankitk.numberbricks.blockdigit.geometry.offset

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import io.github.beankitk.numberbricks.blockdigit.geometry.position.PositionProvider
import io.github.beankitk.numberbricks.blockdigit.geometry.size.VariableSize.Meta
import io.github.beankitk.numberbricks.blockdigit.geometry.size.SizeProvider
import io.github.beankitk.numberbricks.core.geometry.Position
import io.github.beankitk.numberbricks.core.geometry.ProviderScope
import io.github.beankitk.numberbricks.core.geometry.ProviderKey

class GridOffset(): OffsetProvider.Adaptive() {

    override val dependsOn: Set<ProviderKey<*>>
        get() = setOf(PositionProvider.key, SizeProvider.key)

    override fun ProviderScope.provideData(): List<Offset> {
        val positions = resultOf<Position>(PositionProvider.key)
        val eachColWidth = metaOf<FloatArray>(Meta.ColWidths)
        val eachRowHeight = metaOf<FloatArray>(Meta.RowHeights)

        if (eachColWidth == null || eachRowHeight == null) {
            return positions.map { position ->
                Offset(
                    x = position.col.toFloat(),
                    y = position.row.toFloat()
                )
            }
        }

        val eachColStartX = toPrefix(eachColWidth)
        val eachRowStartY = toPrefix(eachRowHeight)

        return positions.map { position ->
            Offset(
                x = eachColStartX[position.col],
                y = eachRowStartY[position.row]
            )
        }
    }

    private fun toPrefix(dimens: FloatArray): FloatArray {
        var comulative = 0f
        return FloatArray(dimens.size) { idx ->
            val element = comulative
            comulative += dimens[idx]
            element
        }
    }
}