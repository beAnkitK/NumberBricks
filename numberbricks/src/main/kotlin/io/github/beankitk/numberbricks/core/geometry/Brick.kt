package io.github.beankitk.numberbricks.core.geometry

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

interface Brick<T : Brick<T>> {
    
    val index: Int
    
    val offset: Offset
    
    val size: Size
    
    fun scaledBy(totalSize:Size, brickSize: Size): T
}