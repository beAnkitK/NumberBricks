package io.github.beankitk.numberbricks.block.data

import androidx.compose.ui.geometry.CornerRadius
import io.github.beankitk.numberbricks.utils.ShapeRadius

interface BlockCorners<T> : DigitData<T> {
    
    fun radiusFor(digit: Int, index: Int): ShapeRadius
}

open class DefaultBlockCorners: BlockCorners<FloatArray> {

    override val digit0 = FloatArray(52) { 0f }

    override val digit1 = FloatArray(52) { 0f }

    override val digit2 = FloatArray(52) { 0f }

    override val digit3 = FloatArray(52) { 0f }

    override val digit4 = FloatArray(52) { 0f }

    override val digit5 = FloatArray(52) { 0f }

    override val digit6 = FloatArray(52) { 0f }

    override val digit7 = FloatArray(52) { 0f }

    override val digit8 = FloatArray(52) { 0f }

    override val digit9 = FloatArray(52) { 0f }

    override val default = FloatArray(52) { 0f }
    
    override fun radiusFor(digit: Int, index: Int): ShapeRadius {
        val radius = this[digit]
        val topLeft = radius[index * 4]
        val topRight = radius[index * 4 + 1]
        val bottomRight = radius[index * 4 + 2]
        val bottomLeft = radius[index * 4 + 3]
        
        return ShapeRadius(
            topLeft = CornerRadius(topLeft),
            topRight = CornerRadius(topRight),
            bottomLeft = CornerRadius(bottomLeft),
            bottomRight = CornerRadius(bottomRight)
        )
    }
}