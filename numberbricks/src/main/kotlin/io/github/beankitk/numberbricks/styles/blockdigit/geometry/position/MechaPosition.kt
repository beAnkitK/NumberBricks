package io.github.beankitk.numberbricks.blockdigit.geometry.position

import io.github.beankitk.numberbricks.core.geometry.Position
import io.github.beankitk.numberbricks.data.DigitData

/**
 * Provides a [PositionProvider] for block digit geometry, defining positions for the modern
 * mechanical style.
 */
object MechaPosition : BaseBlockPosition(), DigitData<List<Position>> by ClassicPosition {

    override val key: PositionProvider.Key
        get() = MechaPosition.Key

    override val digit1 = listOf(g4, g2, g2, g5, g5, g8, g8, g8, g11, g11, g13, g14, g15)

    override val digit7 = listOf(g1, g2, g3, g4, g6, g9, g9, g9, g12, g12, g15, g15, g15)

    /** Key identifying the [MechaPosition] provider within the [PositionProvider] family. */
    object Key : PositionProvider.Key {
        override fun toString(): String = "MechaPosition"
    }
}
