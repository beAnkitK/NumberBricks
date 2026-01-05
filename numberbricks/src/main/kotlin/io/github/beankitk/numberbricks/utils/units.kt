package io.github.beankitk.numberbricks.utils

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.util.fastRoundToInt

@Stable 
inline fun Offset.toIntOffset() = IntOffset(x.fastRoundToInt(), y.fastRoundToInt())