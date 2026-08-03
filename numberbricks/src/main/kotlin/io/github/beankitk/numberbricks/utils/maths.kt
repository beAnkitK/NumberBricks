package io.github.beankitk.numberbricks.utils

import kotlin.math.abs

const val epsilon = 1e-6f

infix fun Float.approxEquals(other: Float): Boolean {
    return abs(this - other) < epsilon
}
