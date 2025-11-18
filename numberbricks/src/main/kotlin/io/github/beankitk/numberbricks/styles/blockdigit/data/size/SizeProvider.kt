package io.github.beankitk.numberbricks.blockdigit.data.size

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import io.github.beankitk.numberbricks.blockdigit.data.ProviderData

interface SizeProvider : ProviderData {

    fun sizeFor(index: Int, digit: Int, position: Offset): Size
}