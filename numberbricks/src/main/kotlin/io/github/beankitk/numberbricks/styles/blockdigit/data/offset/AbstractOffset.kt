package io.github.beankitk.numberbricks.blockdigit.data.offset

class AbstractOffset : ClassicOffset() {

    override val digit0 = arrayOf(
        g1, g2, g2,
        g4, g6,
        g7, g7, g9,
        g10, g12,
        g14, g14, g15
    )

    override val digit1 = arrayOf(
        g1, g1, g1,
        g5, g5,
        g8, g8, g8,
        g11, g11,
        g13, g14, g15
    )

    override val digit2 = arrayOf(
        g1, g2, g2,
        g6, g6,
        g7, g8, g9,
        g10, g10,
        g14, g14, g15
    )

    override val digit3 = arrayOf(
        g1, g2, g2,
        g6, g6,
        g8, g8, g8,
        g12, g12,
        g13, g14, g14
    )

    override val digit4 = arrayOf(
        g1, g1, g1,
        g4, g4,
        g7, g9, g9,
        g11, g12,
        g15, g15, g15
    )

    override val digit5 = arrayOf(
        g2, g2, g3,
        g4, g4,
        g7, g8, g9,
        g12, g12,
        g13, g14, g14
    )

    override val digit6 = arrayOf(
        g2, g2, g3,
        g4, g4,
        g7, g8, g9,
        g10, g12,
        g14, g14, g15
    )

    override val digit7 = arrayOf(
        g1, g2, g3,
        g6, g6,
        g8, g8, g8,
        g10, g10,
        g13, g13, g13
    )

    override val digit8 = arrayOf(
        g1, g2, g3,
        g4, g6,
        g8, g8, g8,
        g10, g12,
        g13, g14, g15
    )

    override val digit9 = arrayOf(
        g1, g2, g2,
        g4, g6,
        g7, g8, g9,
        g12, g12,
        g15, g15, g15
    )
}