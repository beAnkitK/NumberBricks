package io.github.beankitk.numberbricks.blockdigit.geometry.position

import io.github.beankitk.numberbricks.core.geometry.Position
import io.github.beankitk.numberbricks.data.DigitData

/**
 * Provides a [PositionProvider] for block digit geometry, defining positions
 * for the minimal style.
 */
object MinimalPosition : BaseBlockPosition(),
    DigitData<List<Position>> by ClassicPosition {

    override val digit1 = listOf(
        g3, g3, g3,
        g6, g6,
        g9, g9, g9,
        g12, g12,
        g15, g15, g15
    )

    override val digit3 = listOf(
        g1, g2, g3,
        g6, g6,
        g7, g8, g9,
        g12, g12,
        g13, g14, g15
    )

    override val digit7 = listOf(
        g1, g2, g3,
        g6, g6,
        g9, g9, g9,
        g12, g12,
        g15, g15, g15
    )
}