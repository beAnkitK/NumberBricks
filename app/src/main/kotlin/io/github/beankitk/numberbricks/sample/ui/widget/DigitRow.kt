package io.github.beankitk.numberbricks.sample.ui.widget

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
import io.github.beankitk.numberbricks.DigitStyle
import io.github.beankitk.numberbricks.NumberBricks
import io.github.beankitk.numberbricks.defaultAnimationSpec

@Composable
fun DigitRow(
    digits: List<Int>,
    modifier: Modifier = Modifier,
    digitStyle: DigitStyle = DigitStyle.Default,
    brickSize: Dp = 5.dp,
    animateDigits: Boolean = false,
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec(),
    animateOnFirstVisible: Boolean = false
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        require(digits.size == 2) { "DigitRow expects exactly two digits" }
        NumberBricks(
            digit = digits[0],
            digitStyle = digitStyle,
            brickSize = brickSize,
            animateDigits = animateDigits,
            animationSpec = animationSpec,
            animateOnFirstVisible = animateOnFirstVisible
        )
        NumberBricks(
            digit = digits[1],
            digitStyle = digitStyle,
            brickSize = brickSize,
            animateDigits = animateDigits,
            animationSpec = animationSpec,
            animateOnFirstVisible = animateOnFirstVisible
        )
    }
}

@Composable
fun DigitRow(
    digits: List<Int>,
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
    
    DigitRow(
        digits = digits,
        modifier = modifier,
        digitStyle = digitStyle,
        brickSize = brickSize,
        animateDigits = animateDigits,
        animationSpec = animationSpec,
        animateOnFirstVisible = animateOnFirstVisible
    )
}
