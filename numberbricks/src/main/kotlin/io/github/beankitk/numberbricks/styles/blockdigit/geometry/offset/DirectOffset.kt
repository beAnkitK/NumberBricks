package io.github.beankitk.numberbricks.blockdigit.geometry.offset

import androidx.compose.ui.geometry.Offset
import io.github.beankitk.numberbricks.blockdigit.geometry.Block
import io.github.beankitk.numberbricks.blockdigit.geometry.position.PositionProvider
import io.github.beankitk.numberbricks.core.geometry.Position
import io.github.beankitk.numberbricks.core.geometry.ProviderScope
import io.github.beankitk.numberbricks.core.geometry.ProviderKey

object DirectOffset : OffsetProvider.Adaptive() {

    override val dependsOn: Set<ProviderKey<*>>
        get() = setOf(PositionProvider.key)

    override fun ProviderScope.provideData(): List<Offset> {
         val positions = resultOf<Position>(PositionProvider.key)
         return positions.map { pos ->
            Offset(
                x = pos.col.toFloat(),
                y = pos.row.toFloat()
            )
         }
    }
}