package io.github.beankitk.numberbricks.blockdigit.geometry.offset

import androidx.compose.ui.geometry.Offset
import io.github.beankitk.numberbricks.blockdigit.geometry.Block
import io.github.beankitk.numberbricks.blockdigit.geometry.position.PositionProvider
import io.github.beankitk.numberbricks.core.geometry.Position
import io.github.beankitk.numberbricks.core.geometry.ProviderStore
import io.github.beankitk.numberbricks.core.geometry.ProviderKey

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