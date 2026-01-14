package io.github.beankitk.numberbricks.core.layout

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

interface BrickItem<T : BrickItem<T>> {
    
    val index: Int
    
    val offset: Offset
    
    val size: Size
    
    fun scaledBy(totalSize:Size, brickSize: Size): T
}