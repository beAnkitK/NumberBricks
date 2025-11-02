package io.github.beankitk.numberbricks.core

import androidx.compose.ui.geometry.Size

interface BrickData<T : BrickData<T>> {

    fun interpolateBySize(end: T, progress: Float, size: Size): T
}