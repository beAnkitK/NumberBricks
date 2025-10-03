package io.github.beankitk.numberbricks.sample.ui.animation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Stable
fun elasticIn(
    targetScale: Float = 0.95f,
    useTouchOffsetPivot: Boolean = false,
    animationSpec: AnimationSpec<Float> = spring()
): IndicationNodeFactory {
    require(targetScale < 1f) { "elasticIn targetScale must be less than 1f" }
    return ElasticTouchNodeFactory(targetScale, useTouchOffsetPivot,animationSpec)
}

@Stable
fun elasticOut(
    targetScale: Float = 1.05f,
    useTouchOffsetPivot: Boolean = false,
    animationSpec: AnimationSpec<Float> = spring()
): IndicationNodeFactory {
    require(targetScale > 1f) { "elasticOut targetScale must be greater than 1f" }
    return ElasticTouchNodeFactory(targetScale, useTouchOffsetPivot, animationSpec)
}

private class ElasticTouchNodeFactory(
    private val targetScale: Float,
    private val useTouchOffsetPivot: Boolean,
    private val animationSpec: AnimationSpec<Float>
) : IndicationNodeFactory {

    override fun create(interactionSource: InteractionSource): DelegatableNode =
        ElasticTouchNode(targetScale, useTouchOffsetPivot, animationSpec, interactionSource)

    override fun hashCode(): Int {
        var result = targetScale.hashCode()
        result = 31 * result + useTouchOffsetPivot.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ElasticTouchNodeFactory) return false
        if (targetScale != other.targetScale) return false
        return useTouchOffsetPivot == other.useTouchOffsetPivot
    }
}

private class ElasticTouchNode(
    private val targetScale: Float,
    private val useTouchOffsetPivot: Boolean,
    private val animationSpec: AnimationSpec<Float>,
    private val interactionSource: InteractionSource
) : Modifier.Node(), DrawModifierNode {

    private val scaleAnimatable = Animatable(1f)
    private var currentPressPosition: Offset = Offset.Zero

    private var pressedAnimation: Job? = null
    private var restingAnimation: Job? = null

    private suspend fun animateToPressed(pressPosition: Offset) {
        restingAnimation?.cancel()
        pressedAnimation?.cancel()
        pressedAnimation = coroutineScope.launch {
            currentPressPosition = pressPosition
            scaleAnimatable.animateTo(targetScale, animationSpec)
        }
    }

    private fun animateToResting() {
        restingAnimation = coroutineScope.launch {
            pressedAnimation?.join()
            scaleAnimatable.animateTo(1f, animationSpec)
        }
    }

    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> animateToPressed(interaction.pressPosition)
                    is PressInteraction.Release -> animateToResting()
                    is PressInteraction.Cancel -> animateToResting()
                }
            }
        }
    }

    override fun ContentDrawScope.draw() {
        val pivot = if (useTouchOffsetPivot) currentPressPosition else center
        scale(
            scale = scaleAnimatable.value,
            pivot = pivot
        ) {
            this@draw.drawContent()
        }
    }
    
    override fun onDetach() {
        pressedAnimation?.cancel()
        restingAnimation?.cancel()
    }
}
