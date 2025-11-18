package io.github.beankitk.numberbricks.blockdigit.data.offset

import androidx.compose.ui.geometry.Offset
import io.github.beankitk.numberbricks.blockdigit.data.createArray

open class ClassicOffset : BaseOffsetProvider() {
    
    final override val rows = 5
    final override val cols = 3
    final override val brickCount = 13
    final override val isAdaptive = false
    
    private val cell = 1f
    private val x0 = cell * 0f
    private val x1 = cell * 1f
    private val x2 = cell * 2f
    
    private val y0 = cell * 0f
    private val y1 = cell * 1f
    private val y2 = cell * 2f
    private val y3 = cell * 3f
    private val y4 = cell * 4f
    
    val g1 = Offset(x0, y0)
    val g2 = Offset(x1, y0)
    val g3 = Offset(x2, y0)
    val g4 = Offset(x0, y1)
    val g5 = Offset(x1, y1)
    val g6 = Offset(x2, y1)
    val g7 = Offset(x0, y2)
    val g8 = Offset(x1, y2)
    val g9 = Offset(x2, y2)
    val g10 = Offset(x0, y3)
    val g11 = Offset(x1, y3)
    val g12 = Offset(x2, y3)
    val g13 = Offset(x0, y4)
    val g14 = Offset(x1, y4)
    val g15 = Offset(x2, y4)
    
    override val default = createArray { g8 }
    
    override val digit0 = createArray(
        g1, g2, g3,
        g4, g6,
        g7, g7, g9,
        g10, g12,
        g13, g14, g15
    )

    override val digit1 = createArray(
        g1, g2, g2,
        g5, g5,
        g8, g8, g8,
        g11, g11,
        g13, g14, g15
    )

    override val digit2 = createArray(
        g1, g2, g3,
        g6, g6,
        g7, g8, g9,
        g10, g10,
        g13, g14, g15
    )

    override val digit3 = createArray(
        g1, g2, g3,
        g6, g6,
        g8, g8, g9,
        g12, g12,
        g13, g14, g15
    )

    override val digit4 = createArray(
        g1, g3, g3,
        g4, g6,
        g7, g8, g9,
        g12, g12,
        g15, g15, g15
    )

    override val digit5 = createArray(
        g1, g2, g3,
        g4, g3,
        g7, g8, g9,
        g12, g12,
        g13, g14, g15
    )
    
    override val digit6 = createArray(
        g1, g2, g3,
        g4, g3,
        g7, g8, g9,
        g10, g12,
        g13, g14, g15
    )

    override val digit7 = createArray(
        g1, g2, g3,
        g6, g6,
        g8, g8, g9,
        g12, g12,
        g15, g15, g15
    )
    
    override val digit8 = createArray(
        g1, g2, g3,
        g4, g6,
        g7, g8, g9,
        g10, g12,
        g13, g14, g15
    )
    
    override val digit9 = createArray(
        g1, g2, g3,
        g4, g6,
        g7, g8, g9,
        g13, g12,
        g13, g14, g15
    )
}