package io.github.beankitk.numberbricks

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.delay
import kotlin.math.ceil
import kotlin.math.roundToInt

@Composable
fun NumberBricks(
    digit: Int,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    sizeMultiplier: Int = 2,
    startDelayMs: Long = 0L,
    animateChanges: Boolean = false,
    animationDurationMs: Int = 300
) {
    val baseCellDp: Dp = 1.dp
    val density = LocalDensity.current

    val cellSizeDp = baseCellDp * sizeMultiplier
    val widthDp = cellSizeDp * 3
    val heightDp = cellSizeDp * 5
    
    val cellSizePx = remember(density, cellSizeDp) { density.run { cellSizeDp.toPx() } }
    val widthPx = cellSizePx * 3f
    val heightPx = cellSizePx * 5f
    
    val targetsPx = remember(digit, cellSizePx) {
        computeTargetsForDigitPx((digit % 10 + 10) % 10, cellSizePx)
    }

    val bricks = remember(sizeMultiplier, cellSizePx) {
        List(13) { index ->
            Animatable(targetsPx.getOrElse(index) { Offset(cellSizePx, cellSizePx * 2f) }, Offset.VectorConverter)
        }
    }

    // Pre-rendered bitmap cache for static digits (when not animating)
    val cachedBitmap: ImageBitmap? = remember(digit, sizeMultiplier, color, cellSizePx) {
        // create bitmap only when animateChanges is false (we still create it regardless,
        // so it's ready to use; caller can decide to use or ignore it)
        createBitmapForDigit(
            widthPx = ceil(widthPx).toInt(),
            heightPx = ceil(heightPx).toInt(),
            targets = computeTargetsForDigitPx((digit % 10 + 10) % 10, cellSizePx),
            cellSize = cellSizePx,
            color = color,
            density = density
        )
    }
    
    LaunchedEffect(digit, animateChanges, cellSizePx, startDelayMs, animationDurationMs) {
        val scope = this
        if (animateChanges) {
            if (startDelayMs > 0) delay(startDelayMs)
            val tweenSpec = tween<Offset>(durationMillis = animationDurationMs, easing = androidx.compose.animation.core.CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f))
            val jobs = bricks.mapIndexed { index, anim ->
                scope.launch {
                    val target = targetsPx.getOrElse(index) { Offset(cellSizePx, cellSizePx * 2f) }
                    anim.animateTo(target, animationSpec = tweenSpec)
                }
            }
            jobs.joinAll()
        } else {
            for (i in bricks.indices) {
                val target = targetsPx.getOrElse(i) { Offset(cellSizePx, cellSizePx * 2f) }
                bricks[i].snapTo(target)
            }
        }
    }

    Box(modifier = modifier.size(widthDp, heightDp)) {
        Canvas(modifier = Modifier.matchParentSize()) {
            // If not animating and cached bitmap exists — draw the bitmap (fast)
            if (!animateChanges && cachedBitmap != null) {
                drawImageCached(cachedBitmap)
            } else {
                // animate or live-draw: read each Animatable value (in px) and draw rect
                for (i in 0 until bricks.size) {
                    val off = bricks[i].value // Offset in px (Animatable updates will invalidate composition/draw)
                    drawRect(
                        color = color,
                        topLeft = Offset(off.x, off.y),
                        size = Size(cellSizePx, cellSizePx)
                    )
                }
            }
        }
    }
}