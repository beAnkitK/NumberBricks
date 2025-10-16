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
import androidx.compose.ui.unit.dp

/**
 * A composable that visually represents a **single numeric digit (0–9)** using
 * **13 rectangular bricks** arranged within a **3×5 grid layout**.
 *
 * Each brick corresponds to a specific segment of the digit’s shape, collectively forming
 * the appearance of a number similar to a digital display. For performance optimization,
 * all bricks are rendered together using a single [Path], reducing draw calls and improving
 * rendering efficiency.
 *
 * When the displayed digit changes, the positions and visibility of the bricks are
 * **smoothly animated**, allowing for a visually appealing transition effect rather than
 * an abrupt change.
 *
 * This composable is useful for custom numeric animations, clocks, counters, or any UI
 * where digits need a stylized, animated display.
 *
 * @sample io.github.beankitk.numberbricks.sample
 * @param digit the digit to display. Must be in the range 0..9
 * @param modifier modifier applied to the root drawing surface
 * @param digitStyle styling information for the digit bricks. See [DigitStyle] for details
 * @param brickSize size of each brick of the numberbrick. The
 *   composable measures itself as `width = 3 * brickSize` and `height = 5 * brickSize`
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
    brickSize: Dp = 5.dp,
    animateDigits: Boolean = false,
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec(),
    animateOnFirstVisible: Boolean = false
) = NumberBricksImpl(
        digit = digit,
        modifier = modifier,
        digitStyle = digitStyle,
        brickSize = brickSize,
        animateDigits = animateDigits,
        animationSpec = animationSpec,
        animateOnFirstVisible = animateOnFirstVisible
    )

/**
 * A composable that visually represents a **single numeric digit (0–9)** using
 * **13 rectangular bricks** arranged within a **3×5 grid layout**.
 *
 * Each brick corresponds to a specific segment of the digit’s shape, collectively forming
 * the appearance of a number similar to a digital display. For performance optimization,
 * all bricks are rendered together using a single [Path], reducing draw calls and improving
 * rendering efficiency.
 *
 * When the displayed digit changes, the positions and visibility of the bricks are
 * **smoothly animated**, allowing for a visually appealing transition effect rather than
 * an abrupt change.
 *
 * This composable is useful for custom numeric animations, clocks, counters, or any UI
 * where digits need a stylized, animated display.
 *
 * @sample io.github.beankitk.numberbricks.sample
 * @param digit the digit to display. Must be in the range 0..9
 * @param modifier modifier applied to the root drawing surface
 * @param digitColor folor used to fill/stroke the bricks
 * @param digitAlpha alpha applied to the drawn path (0f..1f)
 * @param digitDrawStyle [DrawStyle] used when drawing the path (Fill, Stroke, etc.)
 * @param digitColorFilter optional [ColorFilter] to apply
 * @param digitBlendMode blend mode used when drawing the digitPath
 * @param brickSize size of each brick of the numberbrick. The
 *   composable measures itself as `width = 3 * brickSize` and `height = 5 * brickSize`
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
    brickSize: Dp = 5.dp,
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
        brickSize = brickSize,
        animateDigits = animateDigits,
        animationSpec = animationSpec,
        animateOnFirstVisible = animateOnFirstVisible
    )
}

/**
 * A composable that visually represents a **single numeric digit (0–9)** using
 * **13 rectangular bricks** arranged within a **3×5 grid layout**.
 *
 * Each brick corresponds to a specific segment of the digit’s shape, collectively forming
 * the appearance of a number similar to a digital display. For performance optimization,
 * all bricks are rendered together using a single [Path], reducing draw calls and improving
 * rendering efficiency.
 *
 * When the displayed digit changes, the positions and visibility of the bricks are
 * **smoothly animated**, allowing for a visually appealing transition effect rather than
 * an abrupt change.
 *
 * This composable is useful for custom numeric animations, clocks, counters, or any UI
 * where digits need a stylized, animated display.
 *
 * @sample io.github.beankitk.numberbricks.sample
 * @param digit the digit to display. Must be in the range 0..9
 * @param modifier modifier applied to the root drawing surface
 * @param digitBrush brush by used to fill/stroke the brickspath
 * @param digitAlpha alpha applied to the drawn path (0f..1f)
 * @param digitDrawStyle [DrawStyle] used when drawing the path (Fill, Stroke, etc.)
 * @param digitColorFilter optional [ColorFilter] to apply
 * @param digitBlendMode blend mode used when drawing the digitPath
 * @param brickSize size of each brick of the numberbrick. The
 *   composable measures itself as `width = 3 * brickSize` and `height = 5 * brickSize`
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
    brickSize: Dp = 5.dp,
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
        brickSize = brickSize,
        animateDigits = animateDigits,
        animationSpec = animationSpec,
        animateOnFirstVisible = animateOnFirstVisible
    )
}