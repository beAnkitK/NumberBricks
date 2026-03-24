@file:OptIn(ExperimentalProviderMetaApi::class)

package io.github.beankitk.numberbricks.blockdigit.geometry.offset

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import io.github.beankitk.numberbricks.blockdigit.geometry.position.PositionProvider
import io.github.beankitk.numberbricks.blockdigit.geometry.size.VariableSize.Meta
import io.github.beankitk.numberbricks.blockdigit.geometry.size.SizeProvider
import io.github.beankitk.numberbricks.core.geometry.ExperimentalProviderMetaApi
import io.github.beankitk.numberbricks.core.geometry.Position
import io.github.beankitk.numberbricks.core.geometry.ProviderScope
import io.github.beankitk.numberbricks.core.geometry.ProviderKey

class GridOffset(
    private val offsetModifier: GridOffsetModifier
) : OffsetProvider.Adaptive() {

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
        val Fixed: GridOffset = GridOffset { _, _, baseOffset -> baseOffset }
    }
}

fun interface GridOffsetModifier {
    fun modify(digit: Int, position: Position, baseOffset: Offset): Offset
}