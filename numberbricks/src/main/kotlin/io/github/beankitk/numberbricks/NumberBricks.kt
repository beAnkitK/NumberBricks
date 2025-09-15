package io.github.beankitk.numberbricks

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun NumberBricks(
    digit: Int,
    modifier: Modifier = Modifier,
    brickColor: Color = Color.White,
    brickSizeMultiplier: Float = 5f,
    animateDigits: Boolean = false,
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec(),
    animateOnFirstVisible: Boolean = false,
) {
    NumberBricksImpl(
        digit = digit,
        modifier = modifier,
        brickFillStyle = SolidColor(brickColor),
        brickSizeMultiplier = brickSizeMultiplier,
        animateDigits = animateDigits,
        animationSpec = animationSpec,
        animateOnFirstVisible = animateOnFirstVisible
    )
}

@Composable
fun NumberBricks(
    digit: Int,
    modifier: Modifier = Modifier,
    brickBrush: Brush = SolidColor(Color.White),
    brickSizeMultiplier: Float = 5f,
    animateDigits: Boolean = false,
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec(),
    animateOnFirstVisible: Boolean = false,
) {
    NumberBricksImpl(
        digit = digit,
        modifier = modifier,
        brickFillStyle = brickBrush,
        brickSizeMultiplier = brickSizeMultiplier,
        animateDigits = animateDigits,
        animationSpec = animationSpec,
        animateOnFirstVisible = animateOnFirstVisible
    )
}

@Composable
private fun NumberBricksImpl(
    digit: Int,
    modifier: Modifier,
    brickFillStyle: Brush,
    brickSizeMultiplier: Float,
    animateDigits: Boolean,
    animationSpec: AnimationSpec<Float>,
    animateOnFirstVisible: Boolean,
) {
    require(digit in 0..9) {
        "The digit parameter accepts only values from 0 to 9, but got $digit"
    }
    
    val density = LocalDensity.current
    val baseBrickSize: Dp = 1.dp
    val brickSizeDp = baseBrickSize * brickSizeMultiplier
    val brickSizePx = remember(density, brickSizeDp) { with(density) { brickSizeDp.toPx() } }
    val width = brickSizeDp * 3f
    val height = brickSizeDp * 5f

    val brickSize = remember(brickSizePx) {
        Size(
            width = brickSizePx.roundToInt().toFloat(),
            height = brickSizePx.roundToInt().toFloat()
        )
    }
    
    val initialOffset = Offset(brickSizePx, brickSizePx * 2f)
    val startOffsets = remember { Array(13) { initialOffset } }
    val endOffsets = remember { Array(13) { initialOffset } }
    val targetOffsets = remember { Array(13) { Offset.Unspecified } }
    
    val progress = remember { Animatable(0f) }
    val isFirstVisible = rememberSaveable { mutableStateOf(true) }
    
    LaunchedEffect(digit, animateDigits, brickSizePx) {
        targetOffsets.computeOffsetsFor(digit, brickSizePx)

        if (animateDigits) {
            val currentProgress = progress.value
            if (currentProgress in 0f..1f && currentProgress != 0f && currentProgress != 1f) {
                for (i in 0 until 13) {
                    startOffsets[i] = lerp(startOffsets[i], endOffsets[i], currentProgress)
                    endOffsets[i] = targetOffsets[i]
                }
            } else {
                endOffsets.copyInto(startOffsets)
                targetOffsets.copyInto(endOffsets)
            }

            progress.snapTo(0f)
            if (isFirstVisible.value) {
                if (animateOnFirstVisible) progress.animateTo(1f, animationSpec) else progress.snapTo(1f)
                return@LaunchedEffect
            }
            progress.animateTo(1f, animationSpec = animationSpec)
        } else {
            endOffsets.copyInto(startOffsets)
            targetOffsets.copyInto(endOffsets)
            progress.snapTo(0f)
            progress.snapTo(1f)
        }
    }
    
    LaunchedEffect(Unit) {
        if (isFirstVisible.value) isFirstVisible.value = false
    }
    
    val digitPath = remember(digit, brickSize) { Path() }
    
    Spacer(
        modifier = modifier
            .size(width, height)
            .drawBehind {
                digitPath.reset()
                for (i in 0 until 13) {
                    val animatedOffset = lerp(startOffsets[i], endOffsets[i], progress.value)
                    digitPath.addRect(Rect(animatedOffset, brickSize))
                }
                drawPath(
                    path = digitPath,
                    brush = brickFillStyle
                )
            }
    )
}