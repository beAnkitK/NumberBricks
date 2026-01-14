package io.github.beankitk.numberbricks.sample.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button 
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch 
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.beankitk.numberbricks.NumberBricks
import kotlin.random.Random
import kotlinx.coroutines.delay

@Composable
fun EditorScreen(
    modifier: Modifier = Modifier
) {
    var animateDigit by remember { mutableStateOf(true) }
    var animateOnFirstShown by remember { mutableStateOf(true) }
    var isDecrease by remember { mutableStateOf(false) }
    var isRunning by remember { mutableStateOf(true) }
    var digit by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(isRunning, isDecrease) {
        while (isRunning) {
            delay(1000)
            if (isDecrease) {
                if (digit > 0) digit--
            } else {
                digit++
            }
        }
    }

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NumberBricks(
            digit = digit,
            brickWidth = 23.dp,
            brickHeight = 23.dp,
            digitColor = MaterialTheme.colorScheme.primary,
            animateDigits = animateDigit,
            animateOnFirstVisible = animateOnFirstShown
        )

        Text(
            text = "Current Digit = $digit",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = {
                digit = Random.nextInt(0, 9999)
            }) { Text("Ramdom Number") }

            Button(onClick = {
                isRunning = !isRunning
            }) {
                Text(if (isRunning) "Stop" else "Resume")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Animate Changes",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge
            )
            Switch(
                checked = animateDigit,
                onCheckedChange = { animateDigit = it }
            )  
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Decrease Mode",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge
            )
            Switch(
                checked = isDecrease,
                onCheckedChange = { isDecrease = it }
            )
        }
    }
}