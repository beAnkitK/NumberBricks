package io.github.beankitk.numberbricks.blockdigit.layout.offset

class MinimalOffset : ClassicOffset() {

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