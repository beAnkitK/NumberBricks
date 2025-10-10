package io.github.beankitk.numberbricks.sample.ui.widget

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.indication
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.beankitk.numberbricks.sample.R
import io.github.beankitk.numberbricks.sample.ui.animation.elasticIn
import io.github.beankitk.numberbricks.sample.ui.icon.Icons
import androidx.compose.ui.res.stringResource

@Composable
fun PlayPauseFab(
    isPlay: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    ExtendedFloatingActionButton(
        modifier = Modifier
            .padding(end = 12.dp)
            .indication(interactionSource, elasticIn(0.9f)),
        onClick = onClick,
        interactionSource = interactionSource,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    ) {
        AnimatedContent(isPlay) { isPlay ->
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isPlay) {
                    Icon(imageVector = Icons.Pause, contentDescription = "Pause")
                    Text(text = stringResource(R.string.pause), modifier = Modifier.padding(start = 8.dp))
                } else {
                    Icon(imageVector = Icons.Play, contentDescription = "Resume")
                    Text(text = stringResource(R.string.play), modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    }
}