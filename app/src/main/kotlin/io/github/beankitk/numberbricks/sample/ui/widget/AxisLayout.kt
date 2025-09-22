package io.github.beankitk.numberbricks.sample.ui.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.beankitk.numberbricks.sample.utils.asHorizontal
import io.github.beankitk.numberbricks.sample.utils.asVertical

enum class Axis {
    Horizontal, Vertical
}

@Composable
fun AxisLayout(
    axis: Axis,
    modifier: Modifier = Modifier,
    arrangement: Arrangement.HorizontalOrVertical = Arrangement.Center,
    alignment: Alignment = Alignment.Center,
    content: @Composable () -> Unit
) {
    when (axis) {
        Axis.Horizontal -> {
            Row(
                modifier = modifier,
                horizontalArrangement = arrangement,
                verticalAlignment = alignment.asVertical(),
                content = { content() }
            )
        }
        Axis.Vertical -> {
            Column(
                modifier = modifier,
                verticalArrangement = arrangement,
                horizontalAlignment = alignment.asHorizontal(),
                content = { content() }
            )
        }
    }
}