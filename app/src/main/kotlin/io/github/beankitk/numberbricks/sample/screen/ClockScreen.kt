package io.github.beankitk.numberbricks.sample.screen

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsIgnoringVisibility
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.github.beankitk.numberbricks.defaultAnimationSpec
import io.github.beankitk.numberbricks.sample.data.ClockStyles
import io.github.beankitk.numberbricks.sample.ui.clocklayout.ColumnClock
import io.github.beankitk.numberbricks.sample.ui.clocklayout.RowClock
import io.github.beankitk.numberbricks.sample.ui.theme.rememberSystemManager
import io.github.beankitk.numberbricks.sample.utils.VectorConverter
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
    isClockVertical: Boolean? = null,
    animateDigits: Boolean = true,
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec(),
    animateOnFirstVisible: Boolean = true
) {
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val isClockInVertical = isClockVertical ?: isPortrait
    val screenSize = DpSize(
        width = configuration.screenWidthDp.dp,
        height = configuration.screenHeightDp.dp
    )
    
    val contentInsets = WindowInsets.systemBarsIgnoringVisibility.union(WindowInsets.displayCutout)
    
    val clockStyle = remember(styleId) { ClockStyles.styleFor(styleId) }
    val systemManager = rememberSystemManager()
    var parentSize by remember { mutableStateOf(IntSize.Zero) }
    var contentSize by remember { mutableStateOf(IntSize.Zero) }
    var lastAmbientOffset by remember { mutableStateOf(Offset.Zero) }
    
    val digitRowsCount = if (clockStyle.showSeconds) 3 else 2
    val (targetLargeClockSize, targetSmallClockSize) = getTargetBrickSize(isPortrait, isClockInVertical, digitRowsCount, screenSize)
    
    val animatedDrift = remember { Animatable(Offset.Zero, Offset.VectorConverter, label = "clock-drift-animatable") }
    val animatedBrickSize = remember { Animatable(targetSmallClockSize, DpSize.VectorConverter, label = "large-clock-transition") }
    
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
    
        snapshotFlow { Pair(parentSize, contentSize) }
            .first { (p, c) ->
                p.width != 0 && p.height != 0 && c.width != 0 && c.height != 0 
            }
            
        val maxAllowedX = maxOf(0f, (parentSize.width - contentSize.width) / 2f)
        val maxAllowedY = maxOf(0f, (parentSize.height - contentSize.height) / 2f)
        
        if (maxAllowedX <= 0f && maxAllowedY <= 0f) {
            lastAmbientOffset = Offset.Zero
            animatedDrift.animateTo(Offset.Zero, defaultAnimationSpec())
            if (isLargeClock) {
                animatedBrickSize.animateTo(targetLargeClockSize, defaultAnimationSpec())
            } else {
                animatedBrickSize.animateTo(targetSmallClockSize, defaultAnimationSpec())
            }
            return@LaunchedEffect
        }
        
        when {
            isAmbientMode && !isLargeClock -> {
                launch { animatedBrickSize.animateTo(targetSmallClockSize, defaultAnimationSpec()) }
                launch {
                    if (lastAmbientOffset != Offset.Zero && animatedDrift.value == Offset.Zero) {
                        animatedDrift.animateTo(lastAmbientOffset, animationSpec = defaultAnimationSpec())
                    }
                    
                    while (isAmbientMode && !isLargeClock) {
                        val targetX = (Random.nextFloat() * 2f - 1f) * maxAllowedX
                        val targetY = (Random.nextFloat() * 2f - 1f) * maxAllowedY
                        val target = Offset(targetX, targetY)
                        
                        val dx = targetX - animatedDrift.value.x
                        val dy = targetY - animatedDrift.value.y
                        val distance = hypot(dx.toDouble(), dy.toDouble()).toFloat()
                        val duration = ((distance / AMBIENT_SPEED_PX_PER_SEC) * 1000f).toInt().coerceIn(1500, 8000)
                        animatedDrift.animateTo(target, tween(duration, 2000, LinearOutSlowInEasing))
                        lastAmbientOffset = animatedDrift.value
                    }
                }    
            }
            
            isAmbientMode && isLargeClock -> {
                lastAmbientOffset = animatedDrift.value
                launch { animatedBrickSize.animateTo(targetLargeClockSize, defaultAnimationSpec()) }
                launch { animatedDrift.animateTo(Offset.Zero, defaultAnimationSpec()) }
            }
            
            else -> {
                lastAmbientOffset = Offset.Zero
                launch {
                    if (isLargeClock) animatedBrickSize.animateTo(targetLargeClockSize, defaultAnimationSpec())
                    else animatedBrickSize.animateTo(targetSmallClockSize, defaultAnimationSpec())
                }
                launch { animatedDrift.animateTo(Offset.Zero, defaultAnimationSpec()) }
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
            .windowInsetsPadding(contentInsets)
            .padding(16.dp)
            .onSizeChanged { parentSize = it },
        contentAlignment = Alignment.Center
    ) {
        val clockModifier = Modifier
            .onGloballyPositioned { coords -> contentSize = coords.size }
            .graphicsLayer {
                translationX = animatedDrift.value.x
                translationY = animatedDrift.value.y
            }
            .size(
                width = animatedBrickSize.value.width,
                height = animatedBrickSize.value.height
            )

        if (isClockInVertical) {
            ColumnClock(
                styleId = styleId,
                clockStyle = clockStyle,
                currentTime = currentTime,
                visibilityScope = visibilityScope,
                modifier = clockModifier,
                animateDigits = animateDigits,
                animationSpec = animationSpec,
                animateOnFirstVisible = animateOnFirstVisible
            )
        } else {
            RowClock(
                styleId = styleId,
                clockStyle = clockStyle,
                currentTime = currentTime,
                visibilityScope = visibilityScope,
                modifier = clockModifier,
                animateDigits = animateDigits,
                animationSpec = animationSpec,
                animateOnFirstVisible = animateOnFirstVisible
            )
        }
    }
}

private const val AMBIENT_SPEED_PX_PER_SEC = 60f