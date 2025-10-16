package io.github.beankitk.numberbricks.sample.screen

import android.content.res.Configuration
import android.view.WindowManager
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsIgnoringVisibility
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.beankitk.numberbricks.defaultAnimationSpec
import io.github.beankitk.numberbricks.sample.data.ClockStyles
import io.github.beankitk.numberbricks.sample.ui.theme.rememberSystemManager
import io.github.beankitk.numberbricks.sample.ui.widget.Axis
import io.github.beankitk.numberbricks.sample.ui.widget.AxisLayout
import io.github.beankitk.numberbricks.sample.ui.widget.DigitRow
import io.github.beankitk.numberbricks.sample.utils.getTargetBrickSize
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.hypot
import kotlin.random.Random

@Composable
fun SharedTransitionScope.ClockScreen(
    styleId: String,
    currentTime: List<Int>,
    toggleAmbientMode: () -> Unit,
    toggleLargeClock: () -> Unit,
    scheduleAmbientMode: () -> Unit,
    visibilityScope: AnimatedContentScope,
    modifier: Modifier = Modifier,
    isAmbientMode: Boolean = false,
    isLargeClock: Boolean = false,
    isVerticalClock: Boolean? = null,
    animateDigits: Boolean = true,
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec(),
    animateOnFirstVisible: Boolean = true
) {
    val configuration = LocalConfiguration.current
    val isVertical = isVerticalClock ?: configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val height = configuration.screenHeightDp.dp
    val width = configuration.screenWidthDp.dp
    val (targetLargeClockSize, targetSmallClockSize) = getTargetBrickSize(isVertical, width, height)
    val contentInsets = WindowInsets.systemBarsIgnoringVisibility.union(WindowInsets.displayCutout)
    
    val systemManager = rememberSystemManager()
    val parentSize = remember { mutableStateOf(IntSize.Zero) }
    val contentSize = remember { mutableStateOf(IntSize.Zero) }
    
    val clockStyle = remember(styleId) { ClockStyles.styleFor(styleId) }
    
    val animatedDrift = remember { Animatable(Offset.Zero, Offset.VectorConverter, label = "clock-drift-animatable") }
    val animatedBrickSize = remember { Animatable(targetSmallClockSize, Dp.VectorConverter, label = "large-clock-transition") }
    
    DisposableEffect(Unit) {
        systemManager.isSystemBarsLight = false
        systemManager.immersiveView()
        onDispose {
            systemManager.resetWindow()
        }
    }
    
    LaunchedEffect(isAmbientMode) {
        if (!isAmbientMode) {
            scheduleAmbientMode()
        }
        systemManager.isSystemBarsVisible = !isAmbientMode
        systemManager.screenBrightness = if (isAmbientMode) 0f else -1f
    }
    
    LaunchedEffect(isAmbientMode, isLargeClock) {
    
        snapshotFlow { Pair(parentSize.value, contentSize.value) }
            .first { (p, c) ->
                p.width != 0 && p.height != 0 && c.width != 0 && c.height != 0 
            }
            
        val maxAllowedX = maxOf(0f, (parentSize.value.width - contentSize.value.width) / 2f)
        val maxAllowedY = maxOf(0f, (parentSize.value.height - contentSize.value.height) / 2f)
        
        if (maxAllowedX <= 0f && maxAllowedY <= 0f) {
            animatedDrift.animateTo(Offset.Zero, animationSpec = defaultAnimationSpec())
            if (isLargeClock) {
                animatedBrickSize.animateTo(targetLargeClockSize, animationSpec = defaultAnimationSpec())
            } else {
                animatedBrickSize.animateTo(targetSmallClockSize, animationSpec = defaultAnimationSpec())
            }
            return@LaunchedEffect
        }
        
        when {
            isAmbientMode && !isLargeClock -> {
                launch {
                    animatedBrickSize.animateTo(targetSmallClockSize, animationSpec = defaultAnimationSpec())
                }
                launch {
                    while (isAmbientMode && !isLargeClock) {
                        val targetX = (Random.nextFloat() * 2f - 1f) * maxAllowedX
                        val targetY = (Random.nextFloat() * 2f - 1f) * maxAllowedY
                        
                        val dx = targetX - animatedDrift.value.x
                        val dy = targetY - animatedDrift.value.y
                        val distance = hypot(dx.toDouble(), dy.toDouble()).toFloat()
                        val duration = ((distance / AMBIENT_SPEED_PX_PER_SEC) * 1000f).toInt().coerceIn(1500, 8000)
                        animatedDrift.animateTo(Offset(targetX, targetY), tween(duration, 2000, LinearOutSlowInEasing))
                    }
                }    
            }
                        
            isLargeClock -> {
                launch {
                    animatedBrickSize.animateTo(targetLargeClockSize, defaultAnimationSpec())
                }
                launch {
                    animatedDrift.animateTo(Offset.Zero, defaultAnimationSpec())
                }
            }
            
            else -> {
                launch {
                    animatedBrickSize.animateTo(targetSmallClockSize, defaultAnimationSpec())
                }
                launch {
                    animatedDrift.animateTo(Offset.Zero, defaultAnimationSpec())
                }
            }
        }
    }
    
    Box(
        modifier = modifier
            .sharedBounds(
                rememberSharedContentState("${styleId} + bounds"),
                visibilityScope,
                zIndexInOverlay = 1f,
                clipInOverlayDuringTransition = OverlayClip(MaterialTheme.shapes.medium)
            )
            .background(Color.Black)
            .combinedClickable(
                interactionSource = null,
                indication = null,
                onClick = toggleAmbientMode,
                onDoubleClick = toggleLargeClock
            )
            .fillMaxSize()
            .onSizeChanged { parentSize.value = it }
            .windowInsetsPadding(contentInsets)
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
            arrangement = Arrangement.spacedBy(15.dp)
        ) {
            DigitRow(
                digits = currentTime.subList(0, 2),
                digitStyle = clockStyle.hourStyle,
                brickSize = animatedBrickSize.value,
                animateDigits = animateDigits,
                animationSpec = animationSpec,
                animateOnFirstVisible = animateOnFirstVisible
            )

            DigitRow(
                digits = currentTime.subList(2, 4),
                digitStyle = clockStyle.minuteStyle,
                brickSize = animatedBrickSize.value,
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
                    ),
                    digits = currentTime.subList(4, 6),
                    digitStyle = clockStyle.secondStyle,
                    brickSize = animatedBrickSize.value,
                    animateDigits = animateDigits,
                    animationSpec = animationSpec,
                    animateOnFirstVisible = animateOnFirstVisible
                )
            }
        }
    }
}

private const val AMBIENT_SPEED_PX_PER_SEC = 60f