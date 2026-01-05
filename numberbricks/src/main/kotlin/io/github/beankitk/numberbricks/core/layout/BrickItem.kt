package io.github.beankitk.numberbricks.core.layout

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

interface BrickItem<T : BrickItem<T>> {
    
    val index: Int
    
    val size: Size
    
    val position: Offset
    
    fun scaledBy(totalSize:Size, brickSize: Size): T
}