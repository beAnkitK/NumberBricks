package io.github.beankitk.numberbricks.core

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill

/**
 * Styling container used to control how the digit path is drawn.
 *
 * @property brush A [Brush] used to paint the bricks path (solidcolor, gradient brushes etc).
 * @property alpha Alpha applied to the final draw (0f..1f).
 * @property drawStyle The [DrawStyle] used to render the path (Fill, Stroke, ...).
 * @property colorFilter Optional [ColorFilter] applied at draw-time.
 * @property blendMode Blend mode used when compositing the digitPath.
 */
@Immutable
data class DigitStyle(
    val brush: Brush,
    val alpha: Float = 1f,
    val drawStyle: DrawStyle = Fill,
    val colorFilter: ColorFilter? = null,
    val blendMode: BlendMode = BlendMode.SrcOver,
) {
    companion object {
        val Default = DigitStyle(brush = SolidColor(Color.White))
    }
}
