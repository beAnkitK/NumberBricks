package io.github.beankitk.numberbricks

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun NumberBricks(
    digit: Int,
    modifier: Modifier = Modifier,
    digitColor: Color = Color.White,
    digitAlpha: Float = 1f,
    digitDrawStyle: DrawStyle = Fill,
    digitColorFilter: ColorFilter? = null,
    digitBlendMode: BlendMode = BlendMode.SrcOver,
    brickSizeMultiplier: Float = 5f,
    animateDigits: Boolean = false,
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec(),
    animateOnFirstVisible: Boolean = false,
) {
    val digitStyle = DigitStyle(
        brush = SolidColor(digitColor),
        alpha = digitAlpha,
        drawStyle = digitDrawStyle,
        colorFilter = digitColorFilter,
        blendMode = digitBlendMode
    )

    NumberBricksImpl(
        digit = digit,
        modifier = modifier,
        digitStyle = digitStyle,
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
    digitBrush: Brush = SolidColor(Color.White),
    digitAlpha: Float = 1f,
    digitDrawStyle: DrawStyle = Fill,
    digitColorFilter: ColorFilter? = null,
    digitBlendMode: BlendMode = BlendMode.SrcOver,
    brickSizeMultiplier: Float = 5f,
    animateDigits: Boolean = false,
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec(),
    animateOnFirstVisible: Boolean = false,
) {
    val digitStyle = DigitStyle(
        brush = digitBrush,
        alpha = digitAlpha,
        drawStyle = digitDrawStyle,
        colorFilter = digitColorFilter,
        blendMode = digitBlendMode
    )

    NumberBricksImpl(
        digit = digit,
        modifier = modifier,
        digitStyle = digitStyle,
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
    digitStyle: DigitStyle,
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
    val brickSize = remember(brickSizePx) { Size(width = brickSizePx, height = brickSizePx) }
    
    var previousDigit by rememberSaveable { mutableStateOf<Int?>(null) }
    var wasFirstVisible by rememberSaveable { mutableStateOf(false) }
    var progress = rememberSaveable(saver = animatableSaver) { Animatable(0f) }
    
    val initialOffsets = remember(wasFirstVisible, digit, brickSizePx) {
        if (!wasFirstVisible) {
            Array(13) { Offset(brickSizePx, brickSizePx * 2f) }
        } else {
            Array(13) { Offset.Unspecified }.also { it.computeOffsetsFor(digit, brickSizePx) }
        }
    }
    
    val startOffsets = remember(brickSizePx) { Array(13) { i -> initialOffsets[i] } }
    val endOffsets   = remember(brickSizePx) { Array(13) { i -> initialOffsets[i] } }
    val targetOffsets = remember { Array(13) { Offset.Unspecified } }
    
    LaunchedEffect(digit, animateDigits) {
        val isDigitChange = previousDigit != digit
        if (wasFirstVisible && !isDigitChange && progress.value == 1f) return@LaunchedEffect
    
        targetOffsets.computeOffsetsFor(digit, brickSizePx)
    
        if (!animateDigits) {
            endOffsets.copyInto(startOffsets)
            targetOffsets.copyInto(endOffsets)
            progress.snapTo(0f)
            progress.snapTo(1f)
            previousDigit = digit
            return@LaunchedEffect
        }
        
        val inMiddleOfAnimation = progress.value > 0f && progress.value < 1f
    
        if (inMiddleOfAnimation) {
            for (i in 0 until 13) {
                startOffsets[i] = lerp(startOffsets[i], endOffsets[i], progress.value)
                endOffsets[i] = targetOffsets[i]
            }
        } else {
            endOffsets.copyInto(startOffsets)
            targetOffsets.copyInto(endOffsets)
        }
    
        progress.snapTo(0f)
        if (!wasFirstVisible) {
            if (animateOnFirstVisible) progress.animateTo(1f, animationSpec) else progress.snapTo(1f)
            wasFirstVisible = true
        } else {
            progress.animateTo(1f,animationSpec)
        }
        previousDigit = digit
    }
    
    Spacer(
        modifier = modifier
            .size(width, height)
            .drawWithCache {
                val digitPath = Path()
                val brush = digitStyle.brush
                val alpha = digitStyle.alpha
                val drawStyle = digitStyle.drawStyle
                val colorFilter = digitStyle.colorFilter
                val blendMode = digitStyle.blendMode
                
                onDrawBehind {
                    digitPath.reset()
                    for (i in 0 until 13) {
                        val animatedOffset = lerp(startOffsets[i], endOffsets[i], progress.value)
                        digitPath.addRect(Rect(animatedOffset, brickSize))
                    }
                    drawPath(
                        path = digitPath,
                        brush = brush,
                        alpha = alpha,
                        style = drawStyle,
                        colorFilter = colorFilter,
                        blendMode = blendMode
                    )
                }
            }
    )
}