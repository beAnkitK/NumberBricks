package io.github.beankitk.numberbricks.blockdigit.geometry.position

/**
 * Provides a [PositionProvider] for block digit geometry, defining positions for the minimal style.
 */
class MinimalPosition : ClassicPosition() {

    override val key: PositionProvider.Key
        get() = MinimalPosition.Key

    override val digit1 = listOf(g3, g3, g3, g6, g6, g9, g9, g9, g12, g12, g15, g15, g15)

    override val digit3 = listOf(g1, g2, g3, g6, g6, g7, g8, g9, g12, g12, g13, g14, g15)

    override val digit7 = listOf(g1, g2, g3, g6, g6, g9, g9, g9, g12, g12, g15, g15, g15)

    /** Key identifying the [MinimalPosition] provider within the [PositionProvider] family. */
    object Key : PositionProvider.Key {
        override fun toString(): String = "MinimalPosition"
    }
}
