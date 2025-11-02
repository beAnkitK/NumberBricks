package io.github.beankitk.numberbricks.sample.ui.clocklayout

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.beankitk.numberbricks.utils.defaultAnimationSpec
import io.github.beankitk.numberbricks.sample.data.ClockStyle
import io.github.beankitk.numberbricks.sample.ui.widget.DigitRow

@Composable
fun SharedTransitionScope.ColumnClock(
    styleId: String,
    clockStyle: ClockStyle,
    currentTime: List<Int>,
    visibilityScope: AnimatedContentScope,
    modifier: Modifier = Modifier,
    animateDigits: Boolean = true,
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec(),
    animateOnFirstVisible: Boolean = true
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DigitRow(
            digits = currentTime.subList(0, 2),
            modifier = Modifier.weight(1f),
            digitStyle = clockStyle.hourStyle,
            animateDigits = animateDigits,
            animationSpec = animationSpec,
            animateOnFirstVisible = animateOnFirstVisible
        )

        DigitRow(
            digits = currentTime.subList(2, 4),
            modifier = Modifier.weight(1f),
            digitStyle = clockStyle.minuteStyle,
            animateDigits = animateDigits,
            animationSpec = animationSpec,
            animateOnFirstVisible = animateOnFirstVisible
        )

        if (clockStyle.showSeconds) {
            DigitRow(
                modifier = Modifier
                    .sharedElement(
                        rememberSharedContentState(styleId),
                        visibilityScope,
                        zIndexInOverlay = 2f
                    )
                    .weight(1f),
                digits = currentTime.subList(4, 6),
                digitStyle = clockStyle.secondStyle,
                animateDigits = animateDigits,
                animationSpec = animationSpec,
                animateOnFirstVisible = animateOnFirstVisible
            )
        }
    }
}