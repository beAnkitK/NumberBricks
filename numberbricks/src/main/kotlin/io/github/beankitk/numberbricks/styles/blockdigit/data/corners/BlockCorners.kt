package io.github.beankitk.numberbricks.blockdigit.data.corners

import androidx.compose.ui.geometry.CornerRadius
import io.github.beankitk.numberbricks.data.DigitData
import io.github.beankitk.numberbricks.data.ShapeRadius

interface BlockCorners<T> : DigitData<T> {
    
    fun radiusFor(digit: Int, index: Int): ShapeRadius
}