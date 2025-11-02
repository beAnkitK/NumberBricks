package io.github.beankitk.numberbricks.utils

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.lerp

data class ShapeRadius(
    val topLeft: CornerRadius,
    val topRight: CornerRadius,
    val bottomRight: CornerRadius,
    val bottomLeft: CornerRadius
)

fun lerp(start: ShapeRadius, end: ShapeRadius, t: Float) =
    ShapeRadius(
        topLeft = lerp(start.topLeft, end.topLeft, t),
        topRight = lerp(start.topRight, end.topRight, t),
        bottomRight = lerp(start.bottomRight, end.bottomRight, t),
        bottomLeft = lerp(start.bottomLeft, end.bottomLeft, t)
    )