package io.github.beankitk.numberbricks

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill

@Immutable
internal data class DigitStyle(
    val brush: Brush,
    val alpha: Float = 1f,
    val drawStyle: DrawStyle = Fill,
    val colorFilter: ColorFilter? = null,
    val blendMode: BlendMode = BlendMode.SrcOver
) {
    companion object {
        val Default = DigitStyle(
            brush = SolidColor(Color.White)
        )
    }
}