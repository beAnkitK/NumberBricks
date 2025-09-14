package io.github.beankitk.numberbricks

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun NumberBricks(
    digit: Int,
    modifier: Modifier = Modifier,
    brickColor: Color = Color.White,
    brickSizeMultiplier: Int = 2,
    animateDigits: Boolean = false,
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec,
    delayInMillis: Long = 0L,
    animateOnFirstVisible: Boolean = false,
) {
	require(digit in 0..9) {
		"The digit parameter accept only positive value from 0 to 9, but the value was $digit"
	}
	
	val density = LocalDensity.current
    val baseBrickSize: Dp = 1.dp
	val brickSizeDp = baseBrickSize * brickSizeMultiplier
	val brickSizePx = remember(density, brickSizeDp) { density.run { brickSizeDp.toPx() } }
	val width = brickSizeDp * 3
	val height = brickSizeDp * 5
	
	val initialOffset = Offset(brickSizePx * 1f, brickSizePx * 2f)
	
    val startOffsets = remember { Array(13) { initialOffset } }
    val endOffsets = remember { Array(13) { initialOffset } }
    val targetOffsets = remember { Array(13) { Offset.Unspecified } }

    val progress = remember { Animatable(0f) }
    var isFirstVisible =  rememberSaveable { mutableStateOf(true) }
    
    LaunchedEffect(digit, animateDigits, brickSizePx, delayInMillis) {
        targetOffsets.computeOffsetsFor(digit, brickSizePx)
	    if (animateDigits) {
            val currentProgress = progress.value
	        if (currentProgress > 0f && currentProgress < 1f) {
	            for (i in 0 until 13) {
	                val currentPosition = lerp(startOffsets[i], endOffsets[i], currentProgress)
	                startOffsets[i] = currentPosition
	                endOffsets[i] = targetOffsets[i]
	            }
	        } else {
	            for (i in 0 until 13) {
	                startOffsets[i] = endOffsets[i]
	                endOffsets[i] = targetOffsets[i]
	            }
	        }
	
	        if (delayInMillis > 0) delay(delayInMillis)
	        progress.snapTo(0f)
            
            if (isFirstVisible.value) {
                if (animateOnFirstVisible)
                    progress.animateTo(1f, animationSpec = animationSpec)
                else 
                    progress.snapTo(1f)        
                return@LaunchedEffect    
            }
            
	        progress.animateTo(1f, animationSpec = animationSpec)
            
	    } else {
            for (i in 0 until 13) {
	            startOffsets[i] = endOffsets[i]
	            endOffsets[i] = targetOffsets[i]
	        }
            progress.snapTo(0f)
	        progress.snapTo(1f)
	    }
	}
     
    LaunchedEffect(Unit) {
        if (isFirstVisible.value) isFirstVisible.value = false
    }
    
    Canvas(modifier = modifier.size(width, height)) {
        for (i in 0 until 13) {
            val animatedOffset = lerp(startOffsets[i], endOffsets[i], progress.value)
            val currentOffset = Offset(
                x = animatedOffset.x.roundToInt().toFloat(),
                y = animatedOffset.y.roundToInt().toFloat()
            )
            val currentSize = Size(
                width = brickSizePx.roundToInt().toFloat(),
                height = brickSizePx.roundToInt().toFloat()
            )
            drawRect(
                color = brickColor,
                topLeft = currentOffset,
                size = currentSize
            )
        }
    } 
}

internal val defaultAnimationSpec: AnimationSpec<Float> = tween(
	durationMillis = 300,
	easing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
)