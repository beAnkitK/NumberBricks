package io.github.beankitk.numberbricks.core.data

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

interface BrickData<T : BrickData<T>> {
    
    val index: Int
    
    val size: Size
    
    val position: Offset
    
    fun scaledBy(totalSize:Size, brickSize: Size): T
}