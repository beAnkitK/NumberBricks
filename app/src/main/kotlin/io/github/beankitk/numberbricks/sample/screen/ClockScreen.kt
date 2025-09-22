package io.github.beankitk.numberbricks.sample.screen

import android.app.Activity
import android.content.res.Configuration
import android.view.WindowManager
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.beankitk.numberbricks.NumberBricks
import io.github.beankitk.numberbricks.defaultAnimationSpec
import io.github.beankitk.numberbricks.sample.viewmodel.ClockScreenVM
import io.github.beankitk.numberbricks.sample.ui.widget.Axis
import io.github.beankitk.numberbricks.sample.ui.widget.AxisLayout
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.hypot
import kotlin.random.Random

@Composable
fun ClockScreen(
    modifier: Modifier = Modifier,
    animateDigits: Boolean = true,
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec(),
    animateOnFirstVisible: Boolean = true
) {
    val activity = LocalActivity.current
    val configuration = LocalConfiguration.current
    val viewModel: ClockScreenVM = viewModel()
    
    val currentTime by viewModel.currentTimeAsList.collectAsStateWithLifecycle()
    val isAmbientMode by viewModel.isAmbientMode.collectAsStateWithLifecycle()
    val isLargeClock by viewModel.isLargeClock.collectAsStateWithLifecycle()
    val isVertical = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    
    val height = configuration.screenHeightDp.toFloat()
    val width = configuration.screenWidthDp.toFloat()
    val h1 = currentTime[0]
    val h2 = currentTime[1]
    val m1 = currentTime[2]
    val m2 = currentTime[3]
    val s1 = currentTime[4]
    val s2 = currentTime[5]

    val insetsController = remember(activity) { activity?.window?.let { WindowInsetsControllerCompat(it, it.decorView) } }
    var parentSize = remember { mutableStateOf(IntSize(0, 0)) }
    var contentSize = remember { mutableStateOf(IntSize(0, 0)) }
    
    val targetBrickSize = when {
        isLargeClock && isVertical -> height / 16f
        isLargeClock && !isVertical -> width / 21f
        !isLargeClock && isVertical -> height / 35f
        else -> width / 35f
    }
    
    val clockLayoutAnimatable = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    
    val brickSizeMultiplier by animateFloatAsState(
        targetValue = targetBrickSize,
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
    
    LaunchedEffect(isAmbientMode, isLargeClock) {
        if (parentSize.value.width == 0 || parentSize.value.height == 0 ||
            contentSize.value.width == 0 || contentSize.value.height == 0) {
            delay(200)
        }
        
        if (!isAmbientMode && isLargeClock) {
            clockLayoutAnimatable.animateTo(Offset.Zero, animationSpec = defaultAnimationSpec())
            return@LaunchedEffect
        }
    
        val maxAllowedX = maxOf(0f, (parentSize.value.width - contentSize.value.width) / 2f)
        val maxAllowedY = maxOf(0f, (parentSize.value.height - contentSize.value.height) / 2f)
        
        if (maxAllowedX <= 0f && maxAllowedY <= 0f) {
            clockLayoutAnimatable.animateTo(Offset.Zero, animationSpec = defaultAnimationSpec())
            return@LaunchedEffect
        }
        
        while (isActive && isAmbientMode && !isLargeClock) {
            val targetX = (Random.nextFloat() * 2f - 1f) * maxAllowedX
            val targetY = (Random.nextFloat() * 2f - 1f) * maxAllowedY
            
            val dx = targetX - clockLayoutAnimatable.value.x
            val dy = targetY - clockLayoutAnimatable.value.y
            val distance = hypot(dx.toDouble(), dy.toDouble()).toFloat()
            val speedPxPerSec = 60f
            val duration = ((distance / speedPxPerSec) * 1000).toInt().coerceIn(1500, 8000)
            
            clockLayoutAnimatable.animateTo(Offset(targetX, targetY), animationSpec = tween(durationMillis = duration, easing = LinearOutSlowInEasing))
        }
        clockLayoutAnimatable.animateTo(Offset.Zero, animationSpec = defaultAnimationSpec())
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { parentSize.value = it }
            .background(Color.Black)
            .systemBarsPadding()
            .combinedClickable(
                interactionSource = null,
                indication = null,
                onClick = { viewModel.toggleAmbient() },
                onDoubleClick = { viewModel.toggleLargeClock() }
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        AxisLayout(
            axis = if (isVertical) Axis.Vertical else Axis.Horizontal,
            modifier = Modifier
                .onGloballyPositioned { coords ->
                    contentSize.value = coords.size
                }
                .graphicsLayer {
                    translationX = clockLayoutAnimatable.value.x
                    translationY = clockLayoutAnimatable.value.y
                }
                .wrapContentSize(),
            alignment = Alignment.Center,
            arrangement = Arrangement.spacedBy(15.dp)
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