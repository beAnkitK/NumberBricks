package io.github.beankitk.numberbricks.blockdigit.data.offset

import androidx.compose.ui.geometry.Offset
import io.github.beankitk.numberbricks.data.DigitData
import io.github.beankitk.numberbricks.blockdigit.data.ProviderData

interface OffsetProvider : ProviderData {
    
    fun offsetFor(index: Int, digit: Int): Offset
}

abstract class BaseOffsetProvider : OffsetProvider, DigitData<Array<Offset>> {
    
    override fun offsetFor(index: Int, digit: Int) = this[digit][index]
}