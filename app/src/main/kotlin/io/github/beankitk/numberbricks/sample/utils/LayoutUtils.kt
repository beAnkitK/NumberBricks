package io.github.beankitk.numberbricks.sample.utils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min

@Composable
fun PaddingValues.startPadding(): Dp {
    val layoutDir = LocalLayoutDirection.current
    return if (layoutDir == LayoutDirection.Ltr) {
        calculateLeftPadding(layoutDir)
    } else {
        calculateRightPadding(layoutDir)
    }
}

@Composable
fun PaddingValues.endPadding(): Dp {
    val layoutDir = LocalLayoutDirection.current
    return if (layoutDir == LayoutDirection.Ltr) {
        calculateRightPadding(layoutDir)
    } else {
        calculateLeftPadding(layoutDir)
    }
}

internal fun getTargetBrickSize(
    isPortrait: Boolean,
    isClockVertical: Boolean,
    digitRowsCount: Int,
    size: DpSize
): Pair<DpSize, DpSize> {
    val n = digitRowsCount
    val x = 25.dp   // brickSize
    val s = 15.dp   // numberBrickGap
    val g = 15.dp   // digitRowGap
    val sw = size.width
    val sh = size.height
    val vertical = isClockVertical == true
    
    fun columnHeight() = x * 5 * n + g * (n - 1)
    fun rowWidth() = (x * 6 + s) * n + g * (n - 1)
    fun largeColWidth(h: Dp) = ((h - g * (n - 1)) / 15) * 6 + s
    fun largeRowHeight(w: Dp) = ((w - g * (n - 1) - s * n) / 18) * 5
    
    val smallSize = when {
        isPortrait && vertical -> {
            DpSize(x * 6 + s, columnHeight())
        }
        isPortrait && !vertical -> {
            val w = min(sw, rowWidth())
            DpSize(w, largeRowHeight(w))
        }
        !isPortrait && vertical -> {
            val h = min(sh, columnHeight())
            DpSize(largeColWidth(h), h)
        }
        else -> DpSize(rowWidth(), x * 5)
    }
    
    val largeSize = if (vertical) {
        DpSize(largeColWidth(sh), sh)
    } else {
        DpSize(sw, largeRowHeight(sw))
    }
    
    return Pair(largeSize, smallSize)
}

fun Alignment.asVertical(): Alignment.Vertical = when (this) {
    Alignment.TopStart, Alignment.TopCenter, Alignment.TopEnd -> Alignment.Top
    Alignment.CenterStart, Alignment.Center, Alignment.CenterEnd -> Alignment.CenterVertically
    Alignment.BottomStart, Alignment.BottomCenter, Alignment.BottomEnd -> Alignment.Bottom
    else -> Alignment.Top
}

fun Alignment.asHorizontal(): Alignment.Horizontal = when (this) {
    Alignment.TopStart, Alignment.CenterStart, Alignment.BottomStart -> Alignment.Start
    Alignment.TopCenter, Alignment.Center, Alignment.BottomCenter -> Alignment.CenterHorizontally
    Alignment.TopEnd, Alignment.CenterEnd, Alignment.BottomEnd -> Alignment.End
    else -> Alignment.Start
}