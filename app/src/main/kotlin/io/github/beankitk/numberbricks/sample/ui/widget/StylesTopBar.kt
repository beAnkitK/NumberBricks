package io.github.beankitk.numberbricks.sample.ui.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsIgnoringVisibility
import androidx.compose.foundation.layout.union
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.lerp
import androidx.compose.ui.unit.dp
import io.github.beankitk.numberbricks.sample.R
import io.github.beankitk.numberbricks.sample.ui.theme.AntonFont

@Composable
fun StylesTopBar(
    scrollBehavior: TopAppBarScrollBehavior
) {
    val displaySmall = MaterialTheme.typography.displaySmall.copy(fontFamily = AntonFont)
    val headlineLarge = MaterialTheme.typography.headlineLarge.copy(fontFamily = AntonFont)

    val overlappedFraction by remember { derivedStateOf { scrollBehavior.state.overlappedFraction } }
    val isOverlapThresholdCrossed = remember(overlappedFraction) { overlappedFraction > 0.3f }
    val titleStyle = remember(overlappedFraction, displaySmall, headlineLarge) {
        lerp(displaySmall, headlineLarge, overlappedFraction)
    }

    TopAppBar(
        modifier = Modifier
            .background(
                Brush.verticalGradient(
                    listOf(MaterialTheme.colorScheme.surface, Color.Transparent)
                )
            )
            .padding(top = 12.dp),
        title = {
            Column(modifier = Modifier.fillMaxHeight()) {
                Text(
                    text = stringResource(R.string.app_name).uppercase(),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = titleStyle,
                )
                AnimatedVisibility(
                    visible = !isOverlapThresholdCrossed,
                    enter = slideInVertically() + fadeIn(),
                    exit = slideOutVertically() + fadeOut(),
                    label = "subheading-visibility"
                ) {
                    Text(
                        text = stringResource(R.string.subheadline),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent
        ),
        expandedHeight = 75.dp,
        windowInsets = WindowInsets.statusBarsIgnoringVisibility.union(WindowInsets.displayCutout),
        scrollBehavior = scrollBehavior
    )
}