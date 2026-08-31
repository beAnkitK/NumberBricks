package io.github.beankitk.numberbricks.sample.ui.widget

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import io.github.beankitk.numberbricks.NumberBricks
import io.github.beankitk.numberbricks.core.DigitStyle
import io.github.beankitk.numberbricks.utils.defaultAnimationSpec

@Composable
fun DigitRow(
    digits: List<Int>,
    modifier: Modifier = Modifier,
    brickWidth: Dp? = null,
    brickHeight: Dp? = null,
    digitStyle: DigitStyle = DigitStyle.Default,
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
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            brickWidth = brickWidth,
            brickHeight = brickHeight,
            digitStyle = digitStyle,
            animateDigits = animateDigits,
            animationSpec = animationSpec,
            animateOnFirstVisible = animateOnFirstVisible
        )
        NumberBricks(
            digit = digits[1],
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            digitStyle = digitStyle,
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
    
    DigitRow(
        digits = digits,
        modifier = modifier,
        brickWidth = brickWidth,
        brickHeight = brickHeight,
        digitStyle = digitStyle,
        animateDigits = animateDigits,
        animationSpec = animationSpec,
        animateOnFirstVisible = animateOnFirstVisible
    )
}