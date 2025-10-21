package io.github.beankitk.numberbricks

import androidx.compose.animation.core.AnimationSpec 
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.Dp

/**
 * Displays a single numeric digit (0–9) using 13 rectangular bricks
 * arranged in a 3×5 grid. Each brick forms part of the digit’s shape, and transition between digits are smoothly animated
 * creating a visually appealing animations ideal for clocks, counters, and custom numeric display
 * 
 * @sample io.github.beankitk.numberbricks.sample
 * @param digit the digit to display. Must be in the range 0..9
 * @param modifier modifier applied to the root drawing surface
 * @param brickSize optional uniform size for each bricks of the numberbrick. The
 *   composable measures itself as `width = 3 * brickSize` and `height = 5 * brickSize` maintaining aspect ratio.
 *   If null, the `brickSize` fallback to default size else it scale to fit incoming constraints changed with [Modifier]
 *   or parent constraints if final size is larger than parent constraints.
 * @param digitStyle styling information for the digit bricks. See [DigitStyle] for details
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
    brickSize: Dp? = null,
    digitStyle: DigitStyle = DigitStyle.Default,
    animateDigits: Boolean = false,
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec(),
    animateOnFirstVisible: Boolean = false
) = NumberBricksImpl(
        digit = digit,
        modifier = modifier,
        brickWidth = brickSize,
        brickHeight = brickSize,
        digitStyle = digitStyle,
        animateDigits = animateDigits,
        animationSpec = animationSpec,
        animateOnFirstVisible = animateOnFirstVisible
    )

/**
 * Displays a single numeric digit (0–9) using 13 rectangular bricks
 * arranged in a 3×5 grid. Each brick forms part of the digit’s shape, and transition between digits are smoothly animated
 * creating a visually appealing animations ideal for clocks, counters, and custom numeric display
 * 
 * @sample io.github.beankitk.numberbricks.sample
 * @param digit the digit to display. Must be in the range 0..9
 * @param modifier modifier applied to the root drawing surface
 * @param brickWidth optional width for each bricks of the numberbrick. The composable measures itself as `width = 3 * brickWidth`.
 *   If null, the `brickWidth` fallback to default width else it scale to fit incoming constraints changed with [Modifier]
 *   or parent constraints if final width is larger than parent constraints.
 * @param brickHeight optional height for each bricks of the numberbrick. The composable measures itself as `height = 5 * brickHeight`.
 *   If null, the `brickHeight` fallback to default height else it scale to fit incoming constraints changed with [Modifier]
 *   or parent constraints if final height is larger than parent constraints.
 * @param digitStyle styling information for the digit bricks. See [DigitStyle] for details
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
    brickWidth: Dp? = null,
    brickHeight: Dp? = null,
    digitStyle: DigitStyle = DigitStyle.Default,
    animateDigits: Boolean = false,
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec(),
    animateOnFirstVisible: Boolean = false
) = NumberBricksImpl(
        digit = digit,
        modifier = modifier,
        brickWidth = brickWidth,
        brickHeight = brickHeight,
        digitStyle = digitStyle,
        animateDigits = animateDigits,
        animationSpec = animationSpec,
        animateOnFirstVisible = animateOnFirstVisible
    )

/**
 * Displays a single numeric digit (0–9) using 13 rectangular bricks
 * arranged in a 3×5 grid. Each brick forms part of the digit’s shape, and transition between digits are smoothly animated
 * creating a visually appealing animations ideal for clocks, counters, and custom numeric display
 * 
 * @sample io.github.beankitk.numberbricks.sample
 * @param digit the digit to display. Must be in the range 0..9
 * @param modifier modifier applied to the root drawing surface
 * @param brickSize optional uniform size for each bricks of the numberbrick. The
 *   composable measures itself as `width = 3 * brickSize` and `height = 5 * brickSize` maintaining aspect ratio.
 *   If null, the `brickSize` fallback to default size else it scale to fit incoming constraints changed with [Modifier]
 *   or parent constraints if final size is larger than parent constraints.
 * @param digitColor folor used to fill/stroke the bricks
 * @param digitAlpha alpha applied to the drawn path (0f..1f)
 * @param digitDrawStyle [DrawStyle] used when drawing the path (Fill, Stroke, etc.)
 * @param digitColorFilter optional [ColorFilter] to apply
 * @param digitBlendMode blend mode used when drawing the digitPath
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
    brickSize: Dp? = null,
    digitColor: Color = Color.White,
    digitAlpha: Float = 1f,
    digitDrawStyle: DrawStyle = Fill,
    digitColorFilter: ColorFilter? = null,
    digitBlendMode: BlendMode = BlendMode.SrcOver,
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
        brickWidth = brickSize,
        brickHeight = brickSize,
        digitStyle = digitStyle,
        animateDigits = animateDigits,
        animationSpec = animationSpec,
        animateOnFirstVisible = animateOnFirstVisible
    )
}

/**
 * Displays a single numeric digit (0–9) using 13 rectangular bricks
 * arranged in a 3×5 grid. Each brick forms part of the digit’s shape, and transition between digits are smoothly animated
 * creating a visually appealing animations ideal for clocks, counters, and custom numeric display
 * 
 * @sample io.github.beankitk.numberbricks.sample
 * @param digit the digit to display. Must be in the range 0..9
 * @param modifier modifier applied to the root drawing surface
 * @param brickWidth optional width for each bricks of the numberbrick. The composable measures itself as `width = 3 * brickWidth`.
 *   If null, the `brickWidth` fallback to default width else it scale to fit incoming constraints changed with [Modifier]
 *   or parent constraints if final width is larger than parent constraints.
 * @param brickHeight optional height for each bricks of the numberbrick. The composable measures itself as `height = 5 * brickHeight`.
 *   If null, the `brickHeight` fallback to default height else it scale to fit incoming constraints changed with [Modifier]
 *   or parent constraints if final height is larger than parent constraints.
 * @param digitColor folor used to fill/stroke the bricks
 * @param digitAlpha alpha applied to the drawn path (0f..1f)
 * @param digitDrawStyle [DrawStyle] used when drawing the path (Fill, Stroke, etc.)
 * @param digitColorFilter optional [ColorFilter] to apply
 * @param digitBlendMode blend mode used when drawing the digitPath
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
    brickWidth: Dp? = null,
    brickHeight: Dp? = null,
    digitColor: Color = Color.White,
    digitAlpha: Float = 1f,
    digitDrawStyle: DrawStyle = Fill,
    digitColorFilter: ColorFilter? = null,
    digitBlendMode: BlendMode = BlendMode.SrcOver,
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
        brickWidth = brickWidth,
        brickHeight = brickHeight,
        digitStyle = digitStyle,
        animateDigits = animateDigits,
        animationSpec = animationSpec,
        animateOnFirstVisible = animateOnFirstVisible
    )
}

/**
 * Displays a single numeric digit (0–9) using 13 rectangular bricks
 * arranged in a 3×5 grid. Each brick forms part of the digit’s shape, and transition between digits are smoothly animated
 * creating a visually appealing animations ideal for clocks, counters, and custom numeric display
 * 
 * @sample io.github.beankitk.numberbricks.sample
 * @param digit the digit to display. Must be in the range 0..9
 * @param modifier modifier applied to the root drawing surface
 * @param brickSize optional uniform size for each bricks of the numberbrick. The
 *   composable measures itself as `width = 3 * brickSize` and `height = 5 * brickSize` maintaining aspect ratio.
 *   If null, the `brickSize` fallback to default size else it scale to fit incoming constraints changed with [Modifier]
 *   or parent constraints if final size is larger than parent constraints.
 * @param digitBrush brush by used to fill/stroke the brickspath
 * @param digitAlpha alpha applied to the drawn path (0f..1f)
 * @param digitDrawStyle [DrawStyle] used when drawing the path (Fill, Stroke, etc.)
 * @param digitColorFilter optional [ColorFilter] to apply
 * @param digitBlendMode blend mode used when drawing the digitPath
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
    brickSize: Dp? = null,
    digitBrush: Brush = SolidColor(Color.White),
    digitAlpha: Float = 1f,
    digitDrawStyle: DrawStyle = Fill,
    digitColorFilter: ColorFilter? = null,
    digitBlendMode: BlendMode = BlendMode.SrcOver,
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
        brickWidth = brickSize,
        brickHeight = brickSize,
        digitStyle = digitStyle,
        animateDigits = animateDigits,
        animationSpec = animationSpec,
        animateOnFirstVisible = animateOnFirstVisible
    )
}

/**
 * Displays a single numeric digit (0–9) using 13 rectangular bricks
 * arranged in a 3×5 grid. Each brick forms part of the digit’s shape, and transition between digits are smoothly animated
 * creating a visually appealing animations ideal for clocks, counters, and custom numeric display
 * 
 * @sample io.github.beankitk.numberbricks.sample
 * @param digit the digit to display. Must be in the range 0..9
 * @param modifier modifier applied to the root drawing surface
 * @param brickWidth optional width for each bricks of the numberbrick. The composable measures itself as `width = 3 * brickWidth`.
 *   If null, the `brickWidth` fallback to default width else it scale to fit incoming constraints changed with [Modifier]
 *   or parent constraints if final width is larger than parent constraints.
 * @param brickHeight optional height for each bricks of the numberbrick. The composable measures itself as `height = 5 * brickHeight`.
 *   If null, the `brickHeight` fallback to default height else it scale to fit incoming constraints changed with [Modifier]
 *   or parent constraints if final height is larger than parent constraints.
 * @param digitBrush brush by used to fill/stroke the brickspath
 * @param digitAlpha alpha applied to the drawn path (0f..1f)
 * @param digitDrawStyle [DrawStyle] used when drawing the path (Fill, Stroke, etc.)
 * @param digitColorFilter optional [ColorFilter] to apply
 * @param digitBlendMode blend mode used when drawing the digitPath
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
    brickWidth: Dp? = null,
    brickHeight: Dp? = null,
    digitBrush: Brush = SolidColor(Color.White),
    digitAlpha: Float = 1f,
    digitDrawStyle: DrawStyle = Fill,
    digitColorFilter: ColorFilter? = null,
    digitBlendMode: BlendMode = BlendMode.SrcOver,
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
        brickWidth = brickWidth,
        brickHeight = brickHeight,
        digitStyle = digitStyle,
        animateDigits = animateDigits,
        animationSpec = animationSpec,
        animateOnFirstVisible = animateOnFirstVisible
    )
}