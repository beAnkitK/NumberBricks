package io.github.beankitk.numberbricks.sample.screen

import android.app.Activity
import android.view.WindowManager
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.beankitk.numberbricks.NumberBricks
import io.github.beankitk.numberbricks.defaultAnimationSpec
import io.github.beankitk.numberbricks.sample.viewmodel.ClockScreenVM
import kotlinx.coroutines.launch

@Composable
fun ClockScreen(
    modifier: Modifier = Modifier,
    animateDigits: Boolean = true,
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec(),
    animateOnFirstVisible: Boolean = false
) {
    val activity = LocalActivity.current
    val insetsController = remember(activity) { activity?.window?.let { WindowInsetsControllerCompat(it, it.decorView) } }
    val viewModel: ClockScreenVM = viewModel()
    
    val currentTime by viewModel.currentTimeAsList.collectAsStateWithLifecycle()
    val isAmbientMode by viewModel.isAmbientMode.collectAsStateWithLifecycle()
    val isLargeClock by viewModel.isLargeClock.collectAsStateWithLifecycle()
    
    val h1 = currentTime[0]
    val h2 = currentTime[1]
    val m1 = currentTime[2]
    val m2 = currentTime[3]
    val s1 = currentTime[4]
    val s2 = currentTime[5]

    val brickSizeMultiplier by animateFloatAsState(
        targetValue = if (isLargeClock) 50f else 25f,
        animationSpec = defaultAnimationSpec(),
        label = "large-clock-transition"
    )
    
    val brightness by animateFloatAsState(
        targetValue = if (isAmbientMode) 0f else 1f,
        animationSpec = defaultAnimationSpec(),
        label = "screen-brightness-animation"
    )
    
    SideEffect {
        insetsController?.apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
    }
    
    LaunchedEffect(isAmbientMode) {
        if (activity == null) return@LaunchedEffect
        val window = activity.window
        val attrs = window.attributes
        
        val brightnessAnimatorJob = launch {
            snapshotFlow { brightness }
                .collect { value ->
                    attrs.screenBrightness = value
                    window.attributes = attrs
                    
                    if (!isAmbientMode) {
                        attrs.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
                        window.attributes = attrs
                    }
                }
        }
        
        brightnessAnimatorJob.invokeOnCompletion { brightnessAnimatorJob.cancel() }
        
        if (insetsController != null) {
            insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            if (isAmbientMode) {
                insetsController.hide(WindowInsetsCompat.Type.systemBars())
            } else {
                insetsController.show(WindowInsetsCompat.Type.systemBars())
            }
        }
        
        viewModel.scheduleAmbientMode()
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .systemBarsPadding()
            .combinedClickable(
                interactionSource = null,
                indication = null,
                onClick = { viewModel.toggleAmbient() },
                onDoubleClick = { viewModel.toggleLargeClock() }
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier
                .wrapContentSize()
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(15.dp)) {
                NumberBricks(
                    digit = h1,
                    digitColor = Color.White,
                    brickSizeMultiplier = brickSizeMultiplier,
                    animateDigits = animateDigits,
                    animationSpec = animationSpec,
                    animateOnFirstVisible = animateOnFirstVisible
                )
                NumberBricks(
                    digit = h2,
                    digitColor = Color.White,
                    brickSizeMultiplier = brickSizeMultiplier,
                    animateDigits = animateDigits,
                    animationSpec = animationSpec,
                    animateOnFirstVisible = animateOnFirstVisible
                )
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(15.dp)) {
                NumberBricks(
                    digit = m1,
                    digitColor = Color.White,
                    digitAlpha = 0.7f,
                    brickSizeMultiplier = brickSizeMultiplier,
                    animateDigits = animateDigits,
                    animationSpec = animationSpec,
                    animateOnFirstVisible = animateOnFirstVisible
                )
                NumberBricks(
                    digit = m2,
                    digitColor = Color.White,
                    digitAlpha = 0.7f,
                    brickSizeMultiplier = brickSizeMultiplier,
                    animateDigits = animateDigits,
                    animationSpec = animationSpec,
                    animateOnFirstVisible = animateOnFirstVisible
                )
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(15.dp)) {
                NumberBricks(
                    digit = s1,
                    digitColor = Color.White,
                    digitAlpha = 0.35f,
                    brickSizeMultiplier = brickSizeMultiplier,
                    animateDigits = animateDigits,
                    animationSpec = animationSpec,
                    animateOnFirstVisible = animateOnFirstVisible
                )
                NumberBricks(
                    digit = s2,
                    digitColor = Color.White,
                    digitAlpha = 0.35f,
                    brickSizeMultiplier = brickSizeMultiplier,
                    animateDigits = animateDigits,
                    animationSpec = animationSpec,
                    animateOnFirstVisible = animateOnFirstVisible
                )
            }
        }
    }
}