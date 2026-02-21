package io.github.beankitk.numberbricks.core.geometry

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

// Represents the fundamental geometry unit of digit
interface Brick<T : Brick<T>> {

    val index: Int

    val position: Position

    val offset: Offset

    val size: Size

    fun scaledBy(totalSize:Size, brickSize: Size): T
}