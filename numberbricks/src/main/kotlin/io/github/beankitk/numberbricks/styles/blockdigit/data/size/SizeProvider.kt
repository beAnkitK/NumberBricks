package io.github.beankitk.numberbricks.blockdigit.data.size

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

interface SizeProvider {

    fun sizeFor(index: Int, digit: Int, position: Offset): Size
}