package io.github.beankitk.numberbricks

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun NumberBricks(
    digit: Int,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    sizeMultiplier: Int = 2,
    animateChanges: Boolean = false,
    isDecMin: Boolean = false,
    isHour: Boolean = false,
    baseCellDp: Dp = 1.dp
) {
    val d = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
    
    val easing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
    val startDelayMs = if (isHour) 500L else if (isDecMin) 200L else 0L
    
    val cellDp = baseCellDp * sizeMultiplier
    val cellDpFloat = cellDp
    val cellPx = with(d) { cellDpFloat.toPx() }
    val widthDp = cellDp * 3
    val heightDp = cellDp * 5

    val cellsX = remember(sizeMultiplier) {
        List(13) { Animatable(cellPx) }
    }
    val cellsY = remember(sizeMultiplier) {
        List(13) { Animatable(cellPx * 2f) }
    }

    val targets = targetsForDigit(digit % 10, cellPx)

    LaunchedEffect(digit, animateChanges, cellPx, startDelayMs) {
        if (animateChanges) {
            if (startDelayMs > 0) delay(startDelayMs)
            val tweenSpec = tween<Float>(durationMillis = 300, easing = easing)
            val jobs = mutableListOf<Job>()
            for (i in 0 until 13) {
                val tx = targets.first[i]
                val ty = targets.second[i]
                jobs += launch {
                    val jobX = launch { cellsX[i].animateTo(tx, animationSpec = tweenSpec) }
                    val jobY = launch { cellsY[i].animateTo(ty, animationSpec = tweenSpec) }
                    jobX.join(); jobY.join()
                }
            }
            jobs.forEach { it.join() }
        } else {
            for (i in 0 until 13) {
                cellsX[i].snapTo(targets.first[i])
                cellsY[i].snapTo(targets.second[i])
            }
        }
    }
    
    Box(
        modifier = modifier
            .size(widthDp, heightDp)
    ) {
        for (i in 0 until 13) {
            val xPx = cellsX[i].value
            val yPx = cellsY[i].value
            
            Box(
                modifier = Modifier
                    .size(cellDp)
                    .then(
                        Modifier.offset {
                            IntOffset(
                                xPx.roundToInt(),
                                yPx.roundToInt()
                            )
                        }
                    )
                    .background(color)
            )
        }
    }
}