package io.github.beankitk.numberbricks.core

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
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

import io.github.beankitk.numberbricks.core.layout.DigitEntry
import io.github.beankitk.numberbricks.core.layout.DefaultLayoutComposer
import io.github.beankitk.numberbricks.core.layout.LayoutProperties
import io.github.beankitk.numberbricks.core.layout.LayoutConfig
import io.github.beankitk.numberbricks.blockdigit.layout.BlockItem
import io.github.beankitk.numberbricks.blockdigit.layout.BlockLayout
import io.github.beankitk.numberbricks.blockdigit.layout.corners.*
import io.github.beankitk.numberbricks.blockdigit.layout.offset.*
import io.github.beankitk.numberbricks.blockdigit.layout.size.*
import io.github.beankitk.numberbricks.blockdigit.layout.lerp

import io.github.beankitk.numberbricks.utils.logd

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
    val layoutProperties = remember {
        object : LayoutProperties {
            override val config = LayoutConfig.of(rows = 5, cols = 3, bricks = 13)
        }
    }

    val layoutComposer = remember {
        DefaultLayoutComposer<BlockItem>(
            initialNumber = digit,
            properties = layoutProperties,
            layoutBuilder = BlockLayout(
                offsetProvider = ClassicOffset(),
                sizeProvider = DefaultSize.uniform(1f),
                cornersProvider = DefaultCorners.uniform(1f)
            )
        ).apply { initiate() }
    }

    val layoutConfig = layoutComposer.layoutConfig
    val digitCount = layoutComposer.getDigitCount()
    val totalWidth = brickWidth?.let { it * layoutConfig.cols } ?: NumberbrickWidth
    val totalHeight = brickHeight?.let { it * layoutConfig.rows } ?: NumberbrickHeight

    DisposableEffect(Unit) {
        onDispose { layoutComposer.dispose() }
    }

    LaunchedEffect(digit) {
        layoutComposer.updateNumber(digit)
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        for (place in (digitCount - 1) downTo 0) {
            key(place) {
                SingleDigitBrick(
                    place = place,
                    composer = layoutComposer,
                    totalWidth = totalWidth,
                    totalHeight = totalHeight,
                    digitStyle = digitStyle,
                    animateDigits = animateDigits,
                    animationSpec = animationSpec,
                    animateOnFirstVisible = animateOnFirstVisible
                )
            }
        }
    }
}

@Composable
private fun SingleDigitBrick(
    place: Int,
    composer: DefaultLayoutComposer<BlockItem>,
    totalWidth: Dp,
    totalHeight: Dp,
    digitStyle: DigitStyle,
    animateDigits: Boolean,
    animationSpec: AnimationSpec<Float>,
    animateOnFirstVisible: Boolean
) {
    var wasFirstVisible by rememberSaveable { mutableStateOf(false) }
    val progress = rememberSaveable(saver = animatableSaver) {  Animatable(0f) }

    val currentNumber = composer.currentNumber
    val digitEntry = remember(currentNumber) { composer.getDigitEntryAt(place) }

    if (digitEntry == null) return

    val previousDigit = digitEntry.previousDigit
    val currentDigit = digitEntry.currentDigit

    var startBricks by remember { mutableStateOf<List<BlockItem>>(emptyList()) }
    var endBricks by remember { mutableStateOf<List<BlockItem>>(emptyList()) }

    LaunchedEffect(place, currentDigit, animateDigits) {
        if (wasFirstVisible && previousDigit == currentDigit && progress.value == 1f) {
            return@LaunchedEffect
        }

        if (previousDigit != currentDigit) {
            startBricks = if(!wasFirstVisible || previousDigit == null) {
                composer.getDefaultBrickItems()
            } else {
                composer.getBrickItems(previousDigit) ?: error("No bricks for digit $previousDigit")
            }
            
            endBricks = composer.getBrickItems(currentDigit) ?: error("No bricks for digit $currentDigit")
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

    if (startBricks.isEmpty() || endBricks.isEmpty()) return

    Spacer(
        modifier = Modifier
            .size(totalWidth, totalHeight)
            .drawWithCache {
                val digitPath = Path()

                val brush = digitStyle.brush
                val alpha = digitStyle.alpha
                val drawStyle = digitStyle.drawStyle
                val colorFilter = digitStyle.colorFilter
                val blendMode = digitStyle.blendMode

                val brickSize = Size(
                    width = size.width / composer.layoutConfig.cols,
                    height = size.height / composer.layoutConfig.rows
                )

                onDrawBehind {
                    digitPath.reset()
                    for (i in 0 until composer.layoutConfig.bricks) {
                        val animatedBrick = lerp(
                            startBricks[i],
                            endBricks[i],
                            progress.value
                        ).scaledBy(size, brickSize)

                        if (animatedBrick.cornerRadius.isZero()) {
                            digitPath.addRect(animatedBrick.toRect())
                        } else {
                            digitPath.addRoundRect(animatedBrick.toRoundRect())
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
                contentDescription = "$currentDigit"
            }
    )
}

private val NumberbrickWidth = 15.dp
private val NumberbrickHeight = 25.dp