package io.github.beankitk.numberbricks

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.geometry.Offset

/**
* An easing customized tween spec used in numberbrick to drive digit transition
* 
* @param delayMillis delay before aniamtion starts in millisecond
* @param durationMillis duration for which the animation should run in millisecond
*/
fun <T> defaultAnimationSpec(
    delayMillis: Int = 0,
    durationMillis: Int = 300
): AnimationSpec<T> = tween(
    durationMillis = durationMillis,
    delayMillis = delayMillis,
    easing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
)

internal val animatableSaver: Saver<Animatable<Float, AnimationVector1D>, Float> =
    Saver(
        save = { it.value },
        restore = { value -> Animatable(value) }
    )

internal fun Array<Offset>.fillOffsetsFor(digit: Int) {
    val layout = DIGIT_LAYOUTS.getOrElse(digit) { DIGIT_LAYOUTS[10] }
    for (i in indices) {
        val xIndex = layout[i * 2]
        val yIndex = layout[i * 2 + 1]
        this[i] = Offset(xIndex, yIndex)
    }
}

private val DIGIT_LAYOUTS: Array<FloatArray> = arrayOf(
    // digit 0
    floatArrayOf(
        0f,0f, 1f,0f, 2f,0f,
        0f,1f, 2f,1f,
        0f,2f, 0f,2f, 2f,2f,
        0f,3f, 2f,3f,
        0f,4f, 1f,4f, 2f,4f
    ),
    // digit 1
    floatArrayOf(
        0f,0f, 1f,0f, 1f,0f,
        1f,1f, 1f,1f,
        1f,2f, 1f,2f, 1f,2f,
        1f,3f, 1f,3f,
        0f,4f, 1f,4f, 2f,4f
    ),
    // digit 2
    floatArrayOf(
        0f,0f, 1f,0f, 2f,0f,
        2f,1f, 2f,1f,
        0f,2f, 1f,2f, 2f,2f,
        0f,3f, 0f,3f,
        0f,4f, 1f,4f, 2f,4f
    ),
    // digit 3
    floatArrayOf(
        0f,0f, 1f,0f, 2f,0f,
        2f,1f, 2f,1f,
        0f,2f, 1f,2f, 2f,2f,
        2f,3f, 2f,3f,
        0f,4f, 1f,4f, 2f,4f
    ),
    // digit 4
    floatArrayOf(
        0f,0f, 2f,0f, 2f,0f,
        0f,1f, 2f,1f,
        0f,2f, 1f,2f, 2f,2f,
        2f,3f, 2f,3f,
        2f,4f, 2f,4f, 2f,4f
    ),
    // digit 5
    floatArrayOf(
        0f,0f, 1f,0f, 2f,0f,
        0f,1f, 2f,0f,
        0f,2f, 1f,2f, 2f,2f,
        2f,3f, 2f,3f,
        0f,4f, 1f,4f, 2f,4f
    ),
    // digit 6
    floatArrayOf(
        0f,0f, 1f,0f, 2f,0f,
        0f,1f, 2f,0f,
        0f,2f, 1f,2f, 2f,2f,
        0f,3f, 2f,3f,
        0f,4f, 1f,4f, 2f,4f
    ),
    // digit 7
    floatArrayOf(
        0f,0f, 1f,0f, 2f,0f,
        2f,1f, 2f,1f,
        1f,2f, 1f,2f, 2f,2f,
        2f,3f, 2f,3f,
        2f,4f, 2f,4f, 2f,4f
    ),
    // digit 8
    floatArrayOf(
        0f,0f, 1f,0f, 2f,0f,
        0f,1f, 2f,1f,
        0f,2f, 1f,2f, 2f,2f,
        0f,3f, 2f,3f,
        0f,4f, 1f,4f, 2f,4f
    ),
    // digit 9
    floatArrayOf(
        0f,0f, 1f,0f, 2f,0f,
        0f,1f, 2f,1f,
        0f,2f, 1f,2f, 2f,2f,
        0f,4f, 2f,3f,
        0f,4f, 1f,4f, 2f,4f
    ),
    // default at center 1,2 
    floatArrayOf(
        1f,2f, 1f,2f, 1f,2f,
        1f,2f, 1f,2f, 
        1f,2f, 1f,2f, 1f,2f,
        1f,2f, 1f,2f,
        1f,2f, 1f,2f, 1f,2f
    )
    /**
    old 1
    floatArrayOf(
        2f,0f, 2f,0f, 2f,0f,
        2f,1f, 2f,1f,
        2f,2f, 2f,2f, 2f,2f,
        2f,3f, 2f,3f,
        2f,4f, 2f,4f, 2f,4f
    ),
    old 7
    floatArrayOf(
        0f,0f, 1f,0f, 2f,0f,
        2f,1f, 2f,1f,
        2f,2f, 2f,2f, 2f,2f,
        2f,3f, 2f,3f,
        2f,4f, 2f,4f, 2f,4f
    ),
    */
)