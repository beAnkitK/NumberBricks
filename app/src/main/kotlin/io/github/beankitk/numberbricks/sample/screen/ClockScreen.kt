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
import androidx.compose.runtime.rememberUpdatedState
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
import kotlinx.coroutines.Job
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
    
    val animateDigitsState = rememberUpdatedState(animateDigits)
    val animationSpecState = rememberUpdatedState(animationSpec)
    val animateOnFirstVisibleState = rememberUpdatedState(animateOnFirstVisible)

    val isVertical = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val height = configuration.screenHeightDp.toFloat()
    val width = configuration.screenWidthDp.toFloat()
    
    val targetBrickSize = calculateTargetBrickSize(isLargeClock, isVertical, width, height)
    
    val parentSize = remember { mutableStateOf(IntSize(0, 0)) }
    val contentSize = remember { mutableStateOf(IntSize(0, 0)) }
    var brightnessWatcher: Job? = remember { null }
    val insetsController = remember(activity) { 
        activity?.window?.let { WindowInsetsControllerCompat(it, it.decorView) }?.apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
    
    val animatedDrift = remember { Animatable(Offset.Zero, Offset.VectorConverter) }

    val brickSizeMultiplier by animateFloatAsState(
        targetValue = targetBrickSize,
        animationSpec = defaultAnimationSpec(),
        label = "large-clock-transition"
    )

    val brightness by animateFloatAsState(
        targetValue = if (isAmbientMode) 0f else 1f,
        animationSpec = defaultAnimationSpec(),
        label = "screen-brightness-animation",
        finishedListener = { brightnessWatcher?.cancel() }
    )

    SideEffect {
        insetsController?.apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
    }
    
    LaunchedEffect(isAmbientMode) {
        if (!isAmbientMode)
            viewModel.scheduleAmbientMode()
        
        val window = activity?.window ?: return@LaunchedEffect
        val attrs = window.attributes
        
        if (isAmbientMode) {
            insetsController?.hide(WindowInsetsCompat.Type.systemBars())
        } else {
            insetsController?.show(WindowInsetsCompat.Type.systemBars())
        }
        
        brightnessWatcher?.cancel()
        brightnessWatcher = launch {
            snapshotFlow { brightness }.collect { value ->
                attrs.screenBrightness = value
                window.attributes = attrs
                if (!isAmbientMode) {
                    attrs.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
                    window.attributes = attrs
                }
            }
        }
    }
    
    LaunchedEffect(isAmbientMode, isLargeClock, parentSize.value, contentSize.value) {
        if (parentSize.value.width == 0 || parentSize.value.height == 0 ||
            contentSize.value.width == 0 || contentSize.value.height == 0
        ) {
            delay(100)
        }
        
        if (!isAmbientMode && isLargeClock) {
            animatedDrift.animateTo(Offset.Zero, animationSpec = defaultAnimationSpec())
            return@LaunchedEffect
        }

        val maxAllowedX = maxOf(0f, (parentSize.value.width - contentSize.value.width) / 2f)
        val maxAllowedY = maxOf(0f, (parentSize.value.height - contentSize.value.height) / 2f)

        if (maxAllowedX <= 0f && maxAllowedY <= 0f) {
            animatedDrift.animateTo(Offset.Zero, animationSpec = defaultAnimationSpec())
            return@LaunchedEffect
        }
        
        while (isActive && isAmbientMode && !isLargeClock) {
            val targetX = (Random.nextFloat() * 2f - 1f) * maxAllowedX
            val targetY = (Random.nextFloat() * 2f - 1f) * maxAllowedY

            val dx = targetX - animatedDrift.value.x
            val dy = targetY - animatedDrift.value.y
            val distance = hypot(dx.toDouble(), dy.toDouble()).toFloat()

            val duration = ((distance / AMBIENT_SPEED_PX_PER_SEC) * 1000f).toInt().coerceIn(1500, 8000)
            
            delay(2000)
            animatedDrift.animateTo(
                Offset(targetX, targetY),
                animationSpec = tween(durationMillis = duration, easing = LinearOutSlowInEasing)
            )
        }
        animatedDrift.animateTo(Offset.Zero, animationSpec = defaultAnimationSpec())
    }

    Box(
        modifier = modifier
            .background(Color.Black)
            .combinedClickable(
                interactionSource = null,
                indication = null,
                onClick = { viewModel.toggleAmbient() },
                onDoubleClick = { viewModel.toggleLargeClock() }
            )
            .fillMaxSize()
            .onSizeChanged { parentSize.value = it }
            .systemBarsPadding()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        AxisLayout(
            axis = if (isVertical) Axis.Vertical else Axis.Horizontal,
            modifier = Modifier
                .onGloballyPositioned { coords -> contentSize.value = coords.size }
                .graphicsLayer {
                    translationX = animatedDrift.value.x
                    translationY = animatedDrift.value.y
                }
                .wrapContentSize(),
            alignment = Alignment.Center,
            arrangement = Arrangement.spacedBy(DEFAULT_DIGIT_SPACING.dp)
        ) {
            DigitRow(
                digits = currentTime.subList(0, 2),
                digitAlpha = 1f,
                brickSizeMultiplier = brickSizeMultiplier,
                animateDigits = animateDigitsState.value,
                animationSpec = animationSpecState.value,
                animateOnFirstVisible = animateOnFirstVisibleState.value
            )
            
            DigitRow(
                digits = currentTime.subList(2, 4),
                digitAlpha = 0.7f,
                brickSizeMultiplier = brickSizeMultiplier,
                animateDigits = animateDigitsState.value,
                animationSpec = animationSpecState.value,
                animateOnFirstVisible = animateOnFirstVisibleState.value
            )
            
            DigitRow(
                digits = currentTime.subList(4, 6),
                digitAlpha = 0.35f,
                brickSizeMultiplier = brickSizeMultiplier,
                animateDigits = animateDigitsState.value,
                animationSpec = animationSpecState.value,
                animateOnFirstVisible = animateOnFirstVisibleState.value
            )
        }
    }
}

@Composable
private fun DigitRow(
    digits: List<Int>,
    digitAlpha: Float,
    brickSizeMultiplier: Float,
    animateDigits: Boolean,
    animationSpec: AnimationSpec<Float>,
    animateOnFirstVisible: Boolean
) {
    Row(horizontalArrangement = Arrangement.spacedBy(DEFAULT_DIGIT_SPACING.dp)) {
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

private fun calculateTargetBrickSize(isLargeClock: Boolean, isVertical: Boolean, widthDp: Float, heightDp: Float) =
    when {
        isLargeClock && isVertical -> heightDp / 16f
        isLargeClock && !isVertical -> widthDp / 21f
        !isLargeClock && isVertical -> heightDp / 35f
        else -> widthDp / 35f
    }

private const val DEFAULT_DIGIT_SPACING = 15
private const val AMBIENT_SPEED_PX_PER_SEC = 60f