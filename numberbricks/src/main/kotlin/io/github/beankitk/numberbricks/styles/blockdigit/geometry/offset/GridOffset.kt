package io.github.beankitk.numberbricks.blockdigit.geometry.offset

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import io.github.beankitk.numberbricks.blockdigit.geometry.position.PositionProvider
import io.github.beankitk.numberbricks.blockdigit.geometry.size.SizeProvider
import io.github.beankitk.numberbricks.core.geometry.Position
import io.github.beankitk.numberbricks.core.geometry.ProviderStore
import io.github.beankitk.numberbricks.core.geometry.ProviderKey

class GridOffset(): OffsetProvider.Adaptive() {

    override val dependsOn: Set<ProviderKey<*>>
        get() = setOf(PositionProvider.key, SizeProvider.key)

    override fun provideData(digit: Int, providerStore: ProviderStore): List<Offset> {
        val positions = providerStore.get<Position>(PositionProvider.key)
        val sizes = providerStore.get<Size>(SizeProvider.key)

        val eachColWidth = FloatArray(providerGridSpec.cols) { -1f }
        val eachRowHeight = FloatArray(providerGridSpec.rows) { -1f }

        for (index in 0 until providerGridSpec.bricks) {
            val position = positions[index]

            if (eachColWidth[position.col] == -1f)
                eachColWidth[position.col] = sizes[index].width

            if (eachRowHeight[position.row] == -1f)
                eachRowHeight[position.row] = sizes[index].height
         }

        fillEmpty(eachColWidth, providerGridSpec.cols)
        fillEmpty(eachRowHeight, providerGridSpec.rows)

        val eachColStartX = toPrefix(eachColWidth)
        val eachRowStartY = toPrefix(eachRowHeight)

        return positions.map { position ->
            Offset(
                x = eachColStartX[position.col],
                y = eachRowStartY[position.row]
            )
        }
    }

    private fun fillEmpty(dimens: FloatArray, totalSize: Int) {
        var occupiedSize = 0f
        var emptyCount = 0

        dimens.forEach { item ->
            if (item != -1f) {
                occupiedSize += item
            } else {
                emptyCount++
            }
        }

        if (emptyCount > 0) {
            val remaining = totalSize.toFloat() - occupiedSize
            val sizePerEmpty = if (remaining > 0f) remaining / emptyCount else 0f

            for (i in dimens.indices) {
                if (dimens[i] == -1f) {
                    dimens[i] = sizePerEmpty
                }
            }
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