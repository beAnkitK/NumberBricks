package io.github.beankitk.numberbricks.blockdigit.geometry.position

import io.github.beankitk.numberbricks.core.geometry.Position
import io.github.beankitk.numberbricks.core.geometry.GridSpec
import io.github.beankitk.numberbricks.core.geometry.buildProviderData

private const val c0 = 0
private const val c1 = 1
private const val c2 = 2

private const val r0 = 0
private const val r1 = 1
private const val r2 = 2
private const val r3 = 3
private const val r4 = 4

private val gridSpec = GridSpec(rows = 5, cols = 3, bricks = 13)

abstract class BaseBlockPosition : CustomPositionProvider(gridSpec) {

    protected val g1 = Position(r0, c0)
    protected val g2 = Position(r0, c1)
    protected val g3 = Position(r0, c2)
    protected val g4 = Position(r1, c0)
    protected val g5 = Position(r1, c1)
    protected val g6 = Position(r1, c2)
    protected val g7 = Position(r2, c0)
    protected val g8 = Position(r2, c1)
    protected val g9 = Position(r2, c2)
    protected val g10 = Position(r3, c0)
    protected val g11 = Position(r3, c1)
    protected val g12 = Position(r3, c2)
    protected val g13 = Position(r4, c0)
    protected val g14 = Position(r4, c1)
    protected val g15 = Position(r4, c2)
}

object ClassicPosition : BaseBlockPosition() {

    override val digit0 = listOf(
        g1, g2, g3,
        g4, g6,
        g7, g7, g9,
        g10, g12,
        g13, g14, g15
    )

    override val digit1 = listOf(
        g1, g2, g2,
        g5, g5,
        g8, g8, g8,
        g11, g11,
        g13, g14, g15
    )

    override val digit2 = listOf(
        g1, g2, g3,
        g6, g6,
        g7, g8, g9,
        g10, g10,
        g13, g14, g15
    )

    override val digit3 = listOf(
        g1, g2, g3,
        g6, g6,
        g8, g8, g9,
        g12, g12,
        g13, g14, g15
    )

    override val digit4 = listOf(
        g1, g3, g3,
        g4, g6,
        g7, g8, g9,
        g12, g12,
        g15, g15, g15
    )

    override val digit5 = listOf(
        g1, g2, g3,
        g4, g3,
        g7, g8, g9,
        g12, g12,
        g13, g14, g15
    )

    override val digit6 = listOf(
        g1, g2, g3,
        g4, g3,
        g7, g8, g9,
        g10, g12,
        g13, g14, g15
    )

    override val digit7 = listOf(
        g1, g2, g3,
        g6, g6,
        g8, g8, g9,
        g12, g12,
        g15, g15, g15
    )

    override val digit8 = listOf(
        g1, g2, g3,
        g4, g6,
        g7, g8, g9,
        g10, g12,
        g13, g14, g15
    )

    override val digit9 = listOf(
        g1, g2, g3,
        g4, g6,
        g7, g8, g9,
        g13, g12,
        g13, g14, g15
    )

    override val default = buildProviderData { g8 }
}