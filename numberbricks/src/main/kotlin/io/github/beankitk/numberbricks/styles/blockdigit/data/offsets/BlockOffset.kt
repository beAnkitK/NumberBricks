package io.github.beankitk.numberbricks.blockdigit.data.offsets

import androidx.compose.ui.geometry.Offset
import io.github.beankitk.numberbricks.data.DigitData

interface BlockOffset<T> : DigitData<T> {
    
    fun offsetFor(digit: Int, index: Int): Offset
}