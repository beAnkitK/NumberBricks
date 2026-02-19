package io.github.beankitk.numberbricks.blockdigit.geometry.offset

import androidx.compose.ui.geometry.Offset
import io.github.beankitk.numberbricks.blockdigit.geometry.Block
import io.github.beankitk.numberbricks.blockdigit.geometry.position.PositionProvider
import io.github.beankitk.numberbricks.core.geometry.Position
import io.github.beankitk.numberbricks.core.geometry.ProviderStore
import io.github.beankitk.numberbricks.core.geometry.ProviderKey

/**
 * An adaptive offset provider that derives brick offsets directly from their grid positions
 * by directly converting integer grid coordinates to offsets (e.g., Position(row = 5, col = 6)
 * -> Offset(x = 6f, y = 5f)).
 *
 * This is only compatible with [Block] coordinate scale where offsets are defined by unit grid
 * cells. It is the simplest position-to-offset mapping, useful when the bricks needs to be aligned
 * exactly with their grid cell positions without any size or arrangement adjustments. The row
 * values maps to y-axis offset while the col values maps to x-axis offset.
 *
 * @see Block for details on relative scale handling.
 */
class DirectOffset: OffsetProvider.Adaptive() {

    override val dependsOn: Set<ProviderKey<*>>
        get() = setOf(PositionProvider.key)

    override fun getProviderData(digit: Int, providerStore: ProviderStore): List<Offset> {
         val positions = providerStore.get<Position>(PositionProvider.key)
         return positions.map { pos ->
            Offset(
                x = pos.col.toFloat(),
                y = pos.row.toFloat()
            )
         }
    }
}