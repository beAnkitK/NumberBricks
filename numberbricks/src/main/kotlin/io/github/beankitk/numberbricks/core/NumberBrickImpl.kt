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

import io.github.beankitk.numberbricks.core.geometry.DigitSlot
import io.github.beankitk.numberbricks.core.geometry.DefaultNumberComposer
import io.github.beankitk.numberbricks.core.geometry.GeometryProps
import io.github.beankitk.numberbricks.core.geometry.GridSpec
import io.github.beankitk.numberbricks.core.geometry.Position
import io.github.beankitk.numberbricks.blockdigit.geometry.Block
import io.github.beankitk.numberbricks.blockdigit.geometry.BlockDigitBuilder
import io.github.beankitk.numberbricks.blockdigit.geometry.corners.*
import io.github.beankitk.numberbricks.blockdigit.geometry.offset.*
import io.github.beankitk.numberbricks.blockdigit.geometry.position.*
import io.github.beankitk.numberbricks.blockdigit.geometry.size.*
import io.github.beankitk.numberbricks.blockdigit.geometry.lerp

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

    val gridSpec = remember { GridSpec(rows = 5, cols = 3, brickCount = 13) }
    val geometryPropeties = remember { object : GeometryProps {} }

    val gridOffset = remember {
        GridOffset { digit, pos, baseOffset ->
            if ((digit == 3 || digit == 7) && pos == Position(2, 1)) baseOffset.copy(x = 1f) else baseOffset
        }
    }

    val variableSize = remember {
        object: VariableSize(
            eachColWidth = floatArrayOf(1.3f, 0.4f, 1.3f),
            eachRowHeight = floatArrayOf(0.2f, 2.2f, 0.2f, 2.2f, 0.2f)
        ) {
            protected override fun modifyColumnWidths(digit: Int, colWidths: FloatArray) =
                if (digit == 1) floatArrayOf(0.85f, 1.3f, 0.85f) else colWidths

            protected override fun modifyBlockSize(digit: Int, position: Position, baseSize: Size) =
                if ((digit == 3 || digit == 7) && position == Position(2, 1)) baseSize.copy(width = 0.7f) else baseSize
        }
    }

    val numberComposer = remember {
        DefaultNumberComposer<Block>(
            initialNumber = digit,
            digitGridSpec = gridSpec,
            geometryProps = geometryPropeties,
            digitBuilder = BlockDigitBuilder(
                positionProvider = ClassicPosition,
                offsetProvider = gridOffset,
                sizeProvider = variableSize,
                cornersProvider = UniformCorners.Sharp
            )
        ).apply { initiate() }
    }

    val layoutConfig = numberComposer.digitGridSpec
    val digitCount = numberComposer.getDigitCount()
    val totalWidth = brickWidth?.let { it * layoutConfig.cols } ?: NumberbrickWidth
    val totalHeight = brickHeight?.let { it * layoutConfig.rows } ?: NumberbrickHeight

    DisposableEffect(Unit) {
        onDispose { numberComposer.dispose() }
    }

    LaunchedEffect(digit) {
        numberComposer.updateNumber(digit)
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        for (place in (digitCount - 1) downTo 0) {
            key(place) {
                SingleDigitBrick(
                    place = place,
                    numberComposer = numberComposer,
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
    numberComposer: DefaultNumberComposer<Block>,
    totalWidth: Dp,
    totalHeight: Dp,
    digitStyle: DigitStyle,
    animateDigits: Boolean,
    animationSpec: AnimationSpec<Float>,
    animateOnFirstVisible: Boolean
) {
    var wasFirstVisible by rememberSaveable { mutableStateOf(false) }
    val progress = rememberSaveable(saver = animatableSaver) {  Animatable(0f) }

    val currentNumber = numberComposer.currentNumber
    val digitSlot = remember(currentNumber) { numberComposer.getDigitSlotAt(place) }

    if (digitSlot == null) return

    val previousDigit = digitSlot.previousDigit
    val currentDigit = digitSlot.currentDigit

    var startBricks by remember { mutableStateOf<List<Block>>(emptyList()) }
    var endBricks by remember { mutableStateOf<List<Block>>(emptyList()) }

    LaunchedEffect(place, currentDigit, animateDigits) {
		//this check is used for launched trigger due to config change so that does not run on again if there not number updates
        if (wasFirstVisible && previousDigit == currentDigit && progress.value == 1f) {
            return@LaunchedEffect
        }

		// this check is for wedding do not update the bruckitem when same digit is there
        if (previousDigit != currentDigit) {
            startBricks = if(!wasFirstVisible || previousDigit == null) {
                numberComposer.getDefaultBricks()
            } else {
                numberComposer.getBricks(previousDigit) ?: error("No bricks for digit $previousDigit")
            }

            endBricks = numberComposer.getBricks(currentDigit) ?: error("No bricks for digit $currentDigit")
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
                    width = size.width / numberComposer.digitGridSpec.cols,
                    height = size.height / numberComposer.digitGridSpec.rows
                )

                onDrawBehind {
                    digitPath.reset()
                    for (i in 0 until numberComposer.digitGridSpec.brickCount) {
                        val animatedBrick = lerp(
                            startBricks[i],
                            endBricks[i],
                            progress.value
                        ).scaledBy(size, brickSize)

                        when {
                            animatedBrick.corners.isRect() -> digitPath.addRect(animatedBrick.toRect())
                            animatedBrick.corners.isRoundRect() ->  digitPath.addRoundRect(animatedBrick.toRoundRect())
                            else -> {
                                // TODO: For future shapes, implement custom path drawing
                                error("Unsupported corner shape: ${animatedBrick.corners}")
                            }
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