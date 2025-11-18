package io.github.beankitk.numberbricks.blockdigit.data.size

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

class DefaultSize private constructor(
    private val brickSize: Size
): SizeProvider {
    
    override val rows = 0
    override val cols = 0
    override val brickCount = 0
    override val isAdaptive = true
    
    override fun sizeFor(index: Int, digit: Int, position: Offset) = brickSize
    
    companion object {
        val zero = DefaultSize(Size.Zero)
        
        fun uniform(brickSize: Size) = DefaultSize(brickSize)
        
        fun uniform(width: Float, height: Float = width) = DefaultSize(Size(width, height))
    }
}
