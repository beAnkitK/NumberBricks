package io.github.beankitk.numberbricks.sample.utils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

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

internal fun getTargetBrickSize(isScreenVertical: Boolean, widthDp: Dp, heightDp: Dp): Pair<Dp, Dp> {
    val largeSize = if (isScreenVertical) heightDp / 16 else widthDp / 21
    val smallSize = if (isScreenVertical) heightDp / 35 else widthDp / 35
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