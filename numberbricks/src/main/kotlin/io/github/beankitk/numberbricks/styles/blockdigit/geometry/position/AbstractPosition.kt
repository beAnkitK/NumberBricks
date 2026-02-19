package io.github.beankitk.numberbricks.blockdigit.geometry.position

/**
 * Provides positions for abstracted styled digit
 *
 * @see ClassicPosition
 */
class AbstractPosition: ClassicPosition() {

    override val digit0 = listOf(
        g1, g2, g2,
        g4, g6,
        g7, g7, g9,
        g10, g12,
        g14, g14, g15
    )

    override val digit1 = listOf(
        g1, g1, g1,
        g5, g5,
        g8, g8, g8,
        g11, g11,
        g13, g14, g15
    )

    override val digit2 = listOf(
        g1, g2, g2,
        g6, g6,
        g7, g8, g9,
        g10, g10,
        g14, g14, g15
    )

    override val digit3 = listOf(
        g1, g2, g2,
        g6, g6,
        g8, g8, g8,
        g12, g12,
        g13, g14, g14
    )

    override val digit4 = listOf(
        g1, g1, g1,
        g4, g4,
        g7, g9, g9,
        g11, g12,
        g15, g15, g15
    )

    override val digit5 = listOf(
        g2, g2, g3,
        g4, g4,
        g7, g8, g9,
        g12, g12,
        g13, g14, g14
    )

    override val digit6 = listOf(
        g2, g2, g3,
        g4, g4,
        g7, g8, g9,
        g10, g12,
        g14, g14, g15
    )

    override val digit7 = listOf(
        g1, g2, g3,
        g6, g6,
        g8, g8, g8,
        g10, g10,
        g13, g13, g13
    )

    override val digit8 = listOf(
        g1, g2, g3,
        g4, g6,
        g8, g8, g8,
        g10, g12,
        g13, g14, g15
    )

    override val digit9 = listOf(
        g1, g2, g2,
        g4, g6,
        g7, g8, g9,
        g12, g12,
        g15, g15, g15
    )
}