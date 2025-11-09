package io.github.beankitk.numberbricks.blockdigit.data.size

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

open class DefaultSize private constructor(
    private val brickSize: Size
): SizeProvider {

    override fun sizeFor(digit: Int, index: Int, position: Offset) = brickSize
    
    companion object {
        val Zero = DefaultSize(Size.Zero)
        val Full = DefaultSize(Size(1f, 1f))
        
        fun uniform(brickSize: Size) = DefaultSize(brickSize)
    }
}
