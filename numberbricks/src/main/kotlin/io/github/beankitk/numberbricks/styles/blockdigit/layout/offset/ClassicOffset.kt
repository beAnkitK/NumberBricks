package io.github.beankitk.numberbricks.blockdigit.layout.offset

import androidx.compose.ui.geometry.Offset
import io.github.beankitk.numberbricks.core.layout.LayoutConfig
import io.github.beankitk.numberbricks.core.layout.ProviderStore
import io.github.beankitk.numberbricks.core.layout.ProviderKey
import io.github.beankitk.numberbricks.data.DigitData

open class ClassicOffset : OffsetProvider.Fixed(), DigitData<List<Offset>> {

    final override val config = LayoutConfig.of(
        rows = 5, cols = 3, bricks = 13
    )

    private val x0 = 0f * CELL
    private val x1 = 1f * CELL
    private val x2 = 2f * CELL

    private val y0 = 0f * CELL
    private val y1 = 1f * CELL
    private val y2 = 2f * CELL
    private val y3 = 3f * CELL
    private val y4 = 4f * CELL

    protected val g1 = Offset(x0, y0)
    protected val g2 = Offset(x1, y0)
    protected val g3 = Offset(x2, y0)
    protected val g4 = Offset(x0, y1)
    protected val g5 = Offset(x1, y1)
    protected val g6 = Offset(x2, y1)
    protected val g7 = Offset(x0, y2)
    protected val g8 = Offset(x1, y2)
    protected val g9 = Offset(x2, y2)
    protected val g10 = Offset(x0, y3)
    protected val g11 = Offset(x1, y3)
    protected val g12 = Offset(x2, y3)
    protected val g13 = Offset(x0, y4)
    protected val g14 = Offset(x1, y4)
    protected val g15 = Offset(x2, y4)

    override val default = listOf(
        g8, g8, g8,
        g8, g8,
        g8, g8, g8,
        g8, g8,
        g8, g8, g8
    )

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

    override val dependsOn = emptySet<ProviderKey<*>>()
    
    override fun getProviderData(digit: Int, providerStore: ProviderStore): List<Offset> =
        this@ClassicOffset[digit]
}

private const val CELL = 1f