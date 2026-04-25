package io.github.beankitk.numberbricks.utils

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.saveable.Saver

/**
 * An easing customized tween spec used in numberbrick to drive digit transition
 *
 * @param delayMillis delay before animation starts in millisecond
 * @param durationMillis duration for which the animation should run in millisecond
 */
fun <T> defaultAnimationSpec(delayMillis: Int = 0, durationMillis: Int = 300): AnimationSpec<T> =
    tween(
        durationMillis = durationMillis,
        delayMillis = delayMillis,
        easing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f),
    )

internal val animatableSaver: Saver<Animatable<Float, AnimationVector1D>, Float> =
    Saver(save = { it.value }, restore = { value -> Animatable(value) })
