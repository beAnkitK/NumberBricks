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

/**
 * A composable that renders a single digit using 13 rectangular bricks arranged in a 3×5 grid.
 * Each brick is drawn as part of a single [Path] for efficiency. The positions of the
 * bricks can be animated smoothly when the digit changes.
 *
 * @sample io.github.beankitk.numberbricks.sample
 * @param digit the digit to display. Must be in the range 0..9
 * @param modifier modifier applied to the root drawing surface
 * @param digitStyle styling information for the digit bricks. See [DigitStyle] for details
 * @param brickSizeMultiplier multiplier applied to the base brick size (1.dp). The
 *   composable measures itself as `width = 3 * brickSizeMultiplier` and `height = 5 * brickSizeMultiplier`
 * @param animateDigits if true, transitions between different digits are animated. Default to `false`
 * @param animationSpec the animation spec controlling easing and duration . Supports all compose animation spec
 * @param animateOnFirstVisible if true, the first appearance of the composable
 *   animates appearing as growing from center to target digit. Has no effect if [animateDigits] is `false`
 * @throws IllegalArgumentException if [digit] is not between 0 and 9 inclusive
 */
@Composable
fun NumberBricks(
    digit: Int,
    modifier: Modifier = Modifier,
    digitStyle: DigitStyle = DigitStyle.Default,
    brickSizeMultiplier: Float = 5f,
    animateDigits: Boolean = false,
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec(),
    animateOnFirstVisible: Boolean = false
) = NumberBricksImpl(
        digit = digit,
        modifier = modifier,
        digitStyle = digitStyle,
        brickSizeMultiplier = brickSizeMultiplier,
        animateDigits = animateDigits,
        animationSpec = animationSpec,
        animateOnFirstVisible = animateOnFirstVisible
    )

/**
 * A composable that renders a single digit using 13 rectangular bricks arranged in a 3×5 grid.
 * Each brick is drawn as part of a single [Path] for efficiency. The positions of the
 * bricks can be animated smoothly when the digit changes.
 *
 * @sample io.github.beankitk.numberbricks.sample
 * @param digit the digit to display. Must be in the range 0..9
 * @param modifier modifier applied to the root drawing surface
 * @param digitColor folor used to fill/stroke the bricks
 * @param digitAlpha alpha applied to the drawn path (0f..1f)
 * @param digitDrawStyle [DrawStyle] used when drawing the path (Fill, Stroke, etc.)
 * @param digitColorFilter optional [ColorFilter] to apply
 * @param digitBlendMode blend mode used when drawing the digitPath
 * @param brickSizeMultiplier multiplier applied to the base brick size (1.dp). The
 *   composable measures itself as `width = 3 * brickSizeMultiplier` and `height = 5 * brickSizeMultiplier`
 * @param animateDigits if true, transitions between different digits are animated. Default to `false`
 * @param animationSpec the animation spec controlling easing and duration. Supports all compose animation spec
 * @param animateOnFirstVisible if true, the first appearance of the composable
 *   animates appearing as growing from center to target digit. Has no effect if [animateDigits] is `false`
 * @throws IllegalArgumentException if [digit] is not between 0 and 9 inclusive
 */
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

/**
 * A composable that renders a single digit using 13 rectangular bricks arranged in a 3×5 grid.
 * Each brick is drawn as part of a single [Path] for efficiency. The positions of the
 * bricks can be animated smoothly when the digit changes.
 *
 * @sample io.github.beankitk.numberbricks.sample
 * @param digit the digit to display. Must be in the range 0..9
 * @param modifier modifier applied to the root drawing surface
 * @param digitBrush brush by used to fill/stroke the brickspath
 * @param digitAlpha alpha applied to the drawn path (0f..1f)
 * @param digitDrawStyle [DrawStyle] used when drawing the path (Fill, Stroke, etc.)
 * @param digitColorFilter optional [ColorFilter] to apply
 * @param digitBlendMode blend mode used when drawing the digitPath
 * @param brickSizeMultiplier multiplier applied to the base brick size (1.dp). The
 *   composable measures itself as `width = 3 * brickSizeMultiplier` and `height = 5 * brickSizeMultiplier`
 * @param animateDigits if true, transitions between different digits are animated. Default to `false`
 * @param animationSpec the animation spec controlling easing and duration. Supports all compose animation spec
 * @param animateOnFirstVisible if true, the first appearance of the composable
 *   animates appearing as growing from center to target digit. Has no effect if [animateDigits] is `false`
 * @throws IllegalArgumentException if [digit] is not between 0 and 9 inclusive
 */
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
    
    val baseBrickSize = 1.dp
    val brickSizeDp = baseBrickSize * brickSizeMultiplier
    val width = brickSizeDp * 3f
    val height = brickSizeDp * 5f
    
    var previousDigit by rememberSaveable { mutableStateOf<Int?>(null) }
    var wasFirstVisible by rememberSaveable { mutableStateOf(false) }
    var progress = rememberSaveable(saver = animatableSaver) { Animatable(0f) }
    
    val initialOffsets = remember(wasFirstVisible, digit) {
        if (!wasFirstVisible) {
            Array(13) { Offset(1f, 2f) }
        } else {
            Array(13) { Offset.Unspecified }.also { it.fillOffsetsFor(digit) }
        }
    }
    
    val startOffsets = remember { Array(13) { i -> initialOffsets[i] } }
    val endOffsets = remember { Array(13) { i -> initialOffsets[i] } }
    val targetOffsets = remember { Array(13) { Offset.Unspecified } }
    
    LaunchedEffect(digit, animateDigits) {
        if (wasFirstVisible && previousDigit == digit && progress.value == 1f) 
            return@LaunchedEffect
            
        if (previousDigit != digit) {
            targetOffsets.fillOffsetsFor(digit)
            endOffsets.copyInto(startOffsets)
            targetOffsets.copyInto(endOffsets)
            previousDigit = digit
        }
        
        val shouldAnimate = when {
            !animateDigits -> false
            !wasFirstVisible -> {
                wasFirstVisible = true
                animateOnFirstVisible
            }
            else -> true
        }
        
        progress.snapTo(0f)
        if (shouldAnimate) {
            progress.animateTo(1f, animationSpec)
        } else {
            progress.snapTo(1f)
        }
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
                
                val brickWidth = size.width / 3f
                val brickHeight = size.height / 5f
                val brickSize = Size(brickWidth, brickHeight)
                
                onDrawBehind {
                    digitPath.reset()
                    for (i in 0 until 13) {
                        val unitOffset = lerp(startOffsets[i], endOffsets[i], progress.value)
                        val animatedOffset = Offset(
                            unitOffset.x * brickWidth,
                            unitOffset.y * brickHeight
                        )
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