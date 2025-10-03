package io.github.beankitk.numberbricks.sample.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button 
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch 
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.beankitk.numberbricks.NumberBricks
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
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NumberBricks(
            digit = digit,
            modifier = Modifier,
            digitColor = MaterialTheme.colorScheme.primary,
            brickSizeMultiplier = 23f, 
            animateDigits = animateDigit,
            animateOnFirstVisible = animateOnFirstShown
        )
        
        Spacer(modifier = Modifier.height(24.dp))
  
        // Stop / Resume button  
        Button(onClick = { isRunning = !isRunning }) {
            Text(if (isRunning) "Stop" else "Resume")
        } 
  
        Spacer(modifier = Modifier.height(32.dp))
  
        // Preference-style row: Animate Changes
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
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Preference-style row: Decrease Mode
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