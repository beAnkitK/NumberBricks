package io.github.beankitk.numberbricks

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
internal fun NumberBricksImpl(
    digit: Int,
    modifier: Modifier,
    brickWidth: Dp?,
    brickHeight: Dp?,
    digitStyle: DigitStyle,
    animateDigits: Boolean,
    animationSpec: AnimationSpec<Float>,
    animateOnFirstVisible: Boolean,
) {
    require(digit in 0..9) {
        "The digit parameter accepts only values from 0 to 9, but got $digit"
    }
    
    val totalWidth = brickWidth?.let { it * 3 } ?: NumberbrickWidth
    val totalHeight = brickHeight?.let { it * 5 } ?: NumberbrickHeight
    
    var previousDigit by rememberSaveable { mutableStateOf<Int?>(null) }
    var wasFirstVisible by rememberSaveable { mutableStateOf(false) }
    var progress = rememberSaveable(saver = animatableSaver) { Animatable(0f) }
    
    val initialOffsets = remember(wasFirstVisible, digit) {
        if (!wasFirstVisible) {
            Array(13) { Offset(1f, 2f) }
        } else {
            Array(13) { Offset.Unspecified }.also { it.fillOffsetsFor(digit) }
        }
    }
    
    val startOffsets = remember { Array(13) { i -> initialOffsets[i] } }
    val endOffsets = remember { Array(13) { i -> initialOffsets[i] } }
    val targetOffsets = remember { Array(13) { Offset.Unspecified } }
    
    LaunchedEffect(digit, animateDigits) {
        if (wasFirstVisible && previousDigit == digit && progress.value == 1f) 
            return@LaunchedEffect
        
        if (previousDigit != digit) {
            targetOffsets.fillOffsetsFor(digit)
            endOffsets.copyInto(startOffsets)
            targetOffsets.copyInto(endOffsets)
            previousDigit = digit
        }
        
        val shouldAnimate = when {
            !animateDigits -> false
            !wasFirstVisible -> {
                wasFirstVisible = true
                animateOnFirstVisible
            }
            else -> true
        }
        
        progress.snapTo(0f)
        if (shouldAnimate) {
            progress.animateTo(1f, animationSpec)
        } else {
            progress.snapTo(1f)
        }
    }
    
    Spacer(
        modifier = modifier
            .size(totalWidth, totalHeight)
            .drawWithCache {
                val digitPath = Path()
                
                val brush = digitStyle.brush
                val alpha = digitStyle.alpha
                val drawStyle = digitStyle.drawStyle
                val colorFilter = digitStyle.colorFilter
                val blendMode = digitStyle.blendMode
                
                val brickWidth = size.width / 3f
                val brickHeight = size.height / 5f
                val brickSize = Size(brickWidth, brickHeight)
               
                onDrawBehind {
                    digitPath.reset()
                    for (i in 0 until 13) {
                        val unitOffset = lerp(startOffsets[i], endOffsets[i], progress.value)
                        val animatedOffset = Offset(
                            unitOffset.x * brickWidth,
                            unitOffset.y * brickHeight
                        )
                        digitPath.addRect(Rect(animatedOffset, brickSize))
                    }
                    drawPath(
                        path = digitPath,
                        brush = brush,
                        alpha = alpha,
                        style = drawStyle,
                        colorFilter = colorFilter,
                        blendMode = blendMode
                    )
                }
            }
    )
}

private val NumberbrickWidth = 15.dp
private val NumberbrickHeight = 25.dp