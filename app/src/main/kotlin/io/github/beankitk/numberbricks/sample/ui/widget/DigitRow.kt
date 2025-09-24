package io.github.beankitk.numberbricks.sample.ui.widget

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import io.github.beankitk.numberbricks.NumberBricks

@Composable
internal fun DigitRow(
    digits: List<Int>,
    digitAlpha: Float,
    brickSizeMultiplier: Float,
    animateDigits: Boolean,
    animationSpec: AnimationSpec<Float>,
    animateOnFirstVisible: Boolean
) {
    Row(horizontalArrangement = Arrangement.spacedBy(15.dp)) {
        require(digits.size == 2) { "DigitRow expects exactly two digits" }
        NumberBricks(
            digit = digits[0],
            digitColor = Color.White,
            digitAlpha = digitAlpha,
            brickSizeMultiplier = brickSizeMultiplier,
            animateDigits = animateDigits,
            animationSpec = animationSpec,
            animateOnFirstVisible = animateOnFirstVisible
        )
        NumberBricks(
            digit = digits[1],
            digitColor = Color.White,
            digitAlpha = digitAlpha,
            brickSizeMultiplier = brickSizeMultiplier,
            animateDigits = animateDigits,
            animationSpec = animationSpec,
            animateOnFirstVisible = animateOnFirstVisible
        )
    }
}
