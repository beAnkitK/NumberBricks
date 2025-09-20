package io.github.beankitk.numberbricks

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.geometry.Offset

fun defaultAnimationSpec(
    delayMillis: Int = 0,
    durationMillis: Int = 300
): AnimationSpec<Float> = tween(
    durationMillis = durationMillis,
    delayMillis = delayMillis,
    easing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
)

val animatableSaver: Saver<Animatable<Float, AnimationVector1D>, Float> =
    Saver(
        save = { it.value },
        restore = { value -> Animatable(value) }
    )

internal fun Array<Offset>.computeOffsetsFor(digit: Int, cellPx: Float) {
    val layout = DIGIT_LAYOUTS.getOrElse(digit) { DIGIT_LAYOUTS[10] }
    for (i in indices) {
        val xIndex = layout[i * 2].toInt()
        val yIndex = layout[i * 2 + 1].toInt()
        this[i] = Offset(xIndex * cellPx, yIndex * cellPx)
    }
}

private val DIGIT_LAYOUTS: Array<ByteArray> = arrayOf(
    // digit 0
    byteArrayOf(
        0,0, 1,0, 2,0,
        0,1, 2,1,
        0,2, 0,2, 2,2,
        0,3, 2,3,
        0,4, 1,4, 2,4
    ),
    // digit 1
    byteArrayOf(
        0,0, 1,0, 1,0,
        1,1, 1,1,
        1,2, 1,2, 1,2,
        1,3, 1,3,
        0,4, 1,4, 2,4
    ),
    // digit 2
    byteArrayOf(
        0,0, 1,0, 2,0,
        2,1, 2,1,
        0,2, 1,2, 2,2,
        0,3, 0,3,
        0,4, 1,4, 2,4
    ),
    // digit 3
    byteArrayOf(
        0,0, 1,0, 2,0,
        2,1, 2,1,
        0,2, 1,2, 2,2,
        2,3, 2,3,
        0,4, 1,4, 2,4
    ),
    // digit 4
    byteArrayOf(
        0,0, 2,0, 2,0,
        0,1, 2,1,
        0,2, 1,2, 2,2,
        2,3, 2,3,
        2,4, 2,4, 2,4
    ),
    // digit 5
    byteArrayOf(
        0,0, 1,0, 2,0,
        0,1, 2,0,
        0,2, 1,2, 2,2,
        2,3, 2,3,
        0,4, 1,4, 2,4
    ),
    // digit 6
    byteArrayOf(
        0,0, 1,0, 2,0,
        0,1, 2,0,
        0,2, 1,2, 2,2,
        0,3, 2,3,
        0,4, 1,4, 2,4
    ),
    // digit 7
    byteArrayOf(
        0,0, 1,0, 2,0,
        2,1, 2,1,
        1,2, 1,2, 2,2,
        2,3, 2,3,
        2,4, 2,4, 2,4
    ),
    // digit 8
    byteArrayOf(
        0,0, 1,0, 2,0,
        0,1, 2,1,
        0,2, 1,2, 2,2,
        0,3, 2,3,
        0,4, 1,4, 2,4
    ),
    // digit 9
    byteArrayOf(
        0,0, 1,0, 2,0,
        0,1, 2,1,
        0,2, 1,2, 2,2,
        0,4, 2,3,
        0,4, 1,4, 2,4
    ),
    //default at center 1,2 
    byteArrayOf(
        1,2, 1,2, 1,2,
        1,2, 1,2, 
        1,2, 1,2, 1,2,
        1,2, 1,2,
        1,2, 1,2, 1,2,
    )
    
)

/**
old 1
byteArrayOf(
    2,0, 2,0, 2,0,
    2,1, 2,1,
    2,2, 2,2, 2,2,
    2,3, 2,3,
    2,4, 2,4, 2,4
),
old 7
byteArrayOf(
    0,0, 1,0, 2,0,
    2,1, 2,1,
    2,2, 2,2, 2,2,
    2,3, 2,3,
    2,4, 2,4, 2,4
),
*/