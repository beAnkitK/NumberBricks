package io.github.beankitk.numberbricks.blockdigit.data.offset

import androidx.compose.ui.geometry.Offset

open class DefaultOffset private constructor(
    private val offset: Offset
): OffsetProvider {
    
    override fun offsetFor(index: Int, digit: Int) = offset
    
    companion object {
        val Zero = DefaultOffset(Offset.Zero)
        val Center = DefaultOffset(Offset(1f, 2f))
        val End = DefaultOffset(Offset(2f, 4f))
        
        fun getFor(offset: Offset) = DefaultOffset(offset)
    }
}