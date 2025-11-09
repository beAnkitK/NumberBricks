package io.github.beankitk.numberbricks.blockdigit.data.offset

import androidx.compose.ui.geometry.Offset
import io.github.beankitk.numberbricks.data.DigitData

interface OffsetProvider {
    
    fun offsetFor(index: Int, digit: Int): Offset
}

abstract class BaseOffsetProvider : OffsetProvider, DigitData<Array<Offset>> {
    
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
    
    override val default = Array(13) { g8 }
    
    override fun offsetFor(index: Int, digit: Int) = this[digit][index]
}