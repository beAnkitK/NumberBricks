package io.github.beankitk.numberbricks.blockdigit.data.offset

import androidx.compose.ui.geometry.Offset

class DefaultOffset private constructor(
    private val offset: Offset
): OffsetProvider {
    
    override val rows = 0
    override val cols = 0
    override val brickCount = 0
    override val isAdaptive = true
    
    override fun offsetFor(index: Int, digit: Int) = offset
    
    companion object {
        val zero = DefaultOffset(Offset.Zero)
        
        fun getFor(offset: Offset) = DefaultOffset(offset)
    }
}