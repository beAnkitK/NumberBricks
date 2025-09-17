package io.github.beankitk.numberbricks.sample.screen

import android.app.Activity
import android.view.WindowManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import io.github.beankitk.numberbricks.NumberBricks
import io.github.beankitk.numberbricks.defaultAnimationSpec
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId

@Composable
fun ClockScreen(
    modifier: Modifier = Modifier
) {
    val activity = (LocalContext.current as? Activity)
    val scope = rememberCoroutineScope()
    val insetsController = remember(activity) { activity?.window?.let { WindowInsetsControllerCompat(it, it.decorView) }}
    
    var now by remember { mutableStateOf(LocalTime.now(ZoneId.systemDefault())) }
    var largeClock by remember { mutableStateOf(false) }
    var isAmbient by remember { mutableStateOf(false) }
    
    val brightnessAnim = remember { Animatable(1f) }
    val clockScale by animateFloatAsState(
        targetValue = if(largeClock) 2f else 1f,
        animationSpec = defaultAnimationSpec(),
        label = "brickSize"
    )
    
    val brickSizeMultiplier = 25f
    val animateDigits = true
    
    val hoursColor = Color(0xFFFFFFFF)
    val minutesColor = Color(0xFFB2B2B2)
    val secondsColor = Color(0xFF595959)
    
    val hh = String.format("%02d", now.hour % 12)
    val mm = String.format("%02d", now.minute)
    val ss = String.format("%02d", now.second)
    val digits = (hh + mm + ss).map { it - '0' } // it - '0' convert to int
    
    val delays = listOf(500, 400, 300, 200, 100, 0)
    
    SideEffect {
        insetsController?.apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
    }
    
    LaunchedEffect(Unit) {
        while (true) {
            val instant = Instant.now()
            val millis = instant.toEpochMilli()
            val delayUntilNextSecond = 1000 - (millis % 1000)
            delay(delayUntilNextSecond)
            now = LocalTime.now(ZoneId.systemDefault())
        }
    }
    
    LaunchedEffect(isAmbient) {
        if (activity == null) return@LaunchedEffect
        val window = activity.window
        
        launch {
            if (isAmbient) {
                brightnessAnim.animateTo(0f, animationSpec = defaultAnimationSpec())
            } else {
                brightnessAnim.animateTo(1f, animationSpec = defaultAnimationSpec())
            }
        }.invokeOnCompletion {
            val attrs = window.attributes
            attrs.screenBrightness = if (isAmbient) 0f else WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
            window.attributes = attrs
        }
        
        if (insetsController != null) {
            insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            if (isAmbient) {
                insetsController.hide(WindowInsetsCompat.Type.systemBars())
            } else {
                insetsController.show(WindowInsetsCompat.Type.systemBars())
            }
        }
        
        if(!isAmbient) { 
            delay(3000)
            isAmbient = true
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .systemBarsPadding()
            .combinedClickable(
                interactionSource = null,
                indication = null,
                onClick = { isAmbient = !isAmbient },
                onDoubleClick = { largeClock = !largeClock}
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier
                .wrapContentSize()
                .scale(clockScale)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(15.dp)) {
                NumberBricks(
                    digit = digits[0],
                    animateDigits = animateDigits,
                    animationSpec = defaultAnimationSpec(delays[0]),
                    digitColor = hoursColor,
                    brickSizeMultiplier = brickSizeMultiplier
                )
                NumberBricks(
                    digit = digits[1],
                    animateDigits = animateDigits,
                    animationSpec = defaultAnimationSpec(delays[1]),
                    digitColor = hoursColor,
                    brickSizeMultiplier = brickSizeMultiplier
                )
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(15.dp)) {
                NumberBricks(
                    digit = digits[2],
                    animateDigits = animateDigits,
                    animationSpec = defaultAnimationSpec(delays[2]),
                    digitColor = minutesColor,
                    brickSizeMultiplier = brickSizeMultiplier
                )
                NumberBricks(
                    digit = digits[3],
                    animateDigits = animateDigits,
                    animationSpec = defaultAnimationSpec(delays[3]),
                    digitColor = minutesColor,
                    brickSizeMultiplier = brickSizeMultiplier
                )
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(15.dp)) {
                NumberBricks(
                    digit = digits[4],
                    animateDigits = animateDigits,
                    animationSpec = defaultAnimationSpec(delays[4]),
                    digitColor = secondsColor,
                    brickSizeMultiplier = brickSizeMultiplier
                )
                NumberBricks(
                    digit = digits[5],
                    animateDigits = animateDigits,
                    animationSpec = defaultAnimationSpec(delays[5]),
                    digitColor = secondsColor,
                    brickSizeMultiplier = brickSizeMultiplier
                )
            }
        }
    }
}