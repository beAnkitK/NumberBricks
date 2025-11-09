package io.github.beankitk.numberbricks.core

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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.beankitk.numberbricks.utils.animatableSaver

import io.github.beankitk.numberbricks.blockdigit.BlockLayout
import io.github.beankitk.numberbricks.blockdigit.data.corners.*
import io.github.beankitk.numberbricks.blockdigit.data.offset.*
import io.github.beankitk.numberbricks.blockdigit.data.size.*
import io.github.beankitk.numberbricks.blockdigit.lerp

@Composable
internal fun NumberBricksImpl(
    digit: Int,
    modifier: Modifier,
    brickWidth: Dp?,
    brickHeight: Dp?,
    digitStyle: DigitStyle,
    animateDigits: Boolean,
    animationSpec: AnimationSpec<Float>,
    animateOnFirstVisible: Boolean
) {
    require(digit in 0..9) {
        "The digit parameter accepts only values from 0 to 9, but got $digit"
    }
    
    val blockLayout = BlockLayout(
        offsetProvider = ClassicOffset(),
        sizeProvider = DefaultSize.Full,
        cornersProvider = DefaultCorners.Zero
    )
    
    val totalWidth = brickWidth?.let { it * blockLayout.cols } ?: NumberbrickWidth
    val totalHeight = brickHeight?.let { it * blockLayout.rows } ?: NumberbrickHeight
    
    var previousDigit by rememberSaveable { mutableStateOf<Int?>(null) }
    var wasFirstVisible by rememberSaveable { mutableStateOf(false) }
    var progress = rememberSaveable(saver = animatableSaver) { Animatable(0f) }
    
    val initialBricks = remember(wasFirstVisible, digit) {
        if (!wasFirstVisible) {
            blockLayout.defaultBrickData(digit)
        } else {
            blockLayout.brickDataFor(digit)
        }
    }
    
    val startBricks = remember { Array(blockLayout.brickCount) { i -> initialBricks[i] } }
    val endBricks = remember { Array(blockLayout.brickCount) { i -> initialBricks[i] } }
    
    LaunchedEffect(digit, animateDigits) {
        if (wasFirstVisible && previousDigit == digit && progress.value == 1f) 
            return@LaunchedEffect
        
        if (previousDigit != digit) {
            val targetBricks = blockLayout.brickDataFor(digit)
            endBricks.copyInto(startBricks)
            targetBricks.copyInto(endBricks)
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
                
                val brickSize = Size(
                    width = size.width / blockLayout.cols,
                    height = size.height / blockLayout.rows
                )
                
                onDrawBehind {
                    digitPath.rewind()
                    for (i in 0 until blockLayout.brickCount) {
                        val animatedBricks = lerp(startBricks[i], endBricks[i], progress.value).scaledBy(size, brickSize)
                        if(animatedBricks.cornerRadius.isZero()) {
                            digitPath.addRect(animatedBricks.toRect())
                        } else {
                            digitPath.addRoundRect(animatedBricks.toRoundRect())
                        }
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
            .semantics {
                contentDescription = "$digit"
            }
    )
}

private val NumberbrickWidth = 15.dp
private val NumberbrickHeight = 25.dp