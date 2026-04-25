package io.github.beankitk.numberbricks.blockdigit.geometry.offset

import androidx.compose.ui.geometry.Offset
import io.github.beankitk.numberbricks.blockdigit.geometry.Block
import io.github.beankitk.numberbricks.blockdigit.geometry.position.PositionProvider
import io.github.beankitk.numberbricks.core.geometry.Position
import io.github.beankitk.numberbricks.core.geometry.ProviderKey
import io.github.beankitk.numberbricks.core.geometry.ProviderScope
import io.github.beankitk.numberbricks.core.geometry.buildProviderData

/**
 * Provides offsets by mapping grid positions directly to block offsets.
 *
 * This [OffsetProvider] converts each block’s [Position] into an [Offset] in grid-relative
 * fractional units, where `1f` represents one grid cell as defined in [Block]. The column maps to
 * the x-axis and the row maps to the y-axis.
 *
 * This results in a direct cell-aligned placement, where each block is positioned at the origin of
 * its grid cell without any additional adjustments.
 *
 * @see Block for details on grid-relative offset representation
 */
object DirectOffset : OffsetProvider.Adaptive() {

    override val dependsOn: Set<ProviderKey<*>> = setOf(PositionProvider.key)

    override fun ProviderScope.provideData(): List<Offset> {
        val positions = resultOf<Position>(PositionProvider.key)
        return buildProviderData { index ->
            val position = positions[index]

            Offset(x = position.col.toFloat(), y = position.row.toFloat())
        }
    }
}
