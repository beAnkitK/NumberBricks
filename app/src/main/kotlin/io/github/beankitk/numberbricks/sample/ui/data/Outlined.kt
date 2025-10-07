package io.github.beankitk.numberbricks.sample.ui.data

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StampedPathEffectStyle
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import io.github.beankitk.numberbricks.DigitStyle
import io.github.beankitk.numberbricks.sample.utils.getCirclePath
import io.github.beankitk.numberbricks.sample.utils.getDoubleDashPath
import io.github.beankitk.numberbricks.sample.utils.getSquarePath
import io.github.beankitk.numberbricks.sample.utils.getWoodenPath
import io.github.beankitk.numberbricks.sample.utils.getZigZagPath

open class OutlinedStyle(
    clockId: Int,
    displayName: String,
    showSeconds: Boolean = true,
    background: Brush = SolidColor(Color.Black),
    useThemeColor: Boolean = true,
    digitBg: Brush = SolidColor(Color.White),
    strokeWidth: Float = 7.2f,
    strokeCap: StrokeCap = StrokeCap.Butt,
    strokeJoin: StrokeJoin = StrokeJoin.Miter,
    pathEffect: PathEffect? = null,
) : ClockStyle(
    categoryId = "outlined",
    clockId = clockId,
    displayName = displayName,
    showSeconds = showSeconds,
    background = background,
    useThemeColor = useThemeColor,
    digitStyle = DigitStyle(
        brush = digitBg,
        drawStyle = Stroke(
            width = strokeWidth,
            cap = strokeCap,
            join = strokeJoin,
            pathEffect = pathEffect
        )
    )
)

private var nextId: Int = 1
private fun nextId(): Int = nextId.also { nextId += 1 }

object Outlined : ClockCategory {
    override val id: String = "outlined"
    override val displayName: String = "Outlined"
    override val styles: List<ClockStyle> = listOf(
        SimpleBlocks, RoundedBlocks,
        DottedCircle, SquareBlocks, MorseLined,
        ZigZagged, DoubleDashed, LinedDashed, WoodenDashed
    )
    
    data object SimpleBlocks: OutlinedStyle(
        clockId = nextId(),
        displayName = "Simple Blocks",
    )
    
    data object RoundedBlocks: OutlinedStyle(
        clockId = nextId(),
        displayName = "Rounded Blocks",
        pathEffect = PathEffect.cornerPathEffect(30f)
    )
    
    data object MorseLined: OutlinedStyle(
        clockId = nextId(),
        displayName = "Morse Lined",
        strokeCap = StrokeCap.Round,
        pathEffect = PathEffect
            .chainPathEffect(
                outer = PathEffect.dashPathEffect(floatArrayOf(strokeWidth * 4f, strokeWidth * 2f, strokeWidth, strokeWidth * 2f), 0f),
                inner = PathEffect.cornerPathEffect(30f)
            )
    )
    
    data object DottedCircle: OutlinedStyle(
        clockId = nextId(),
        displayName = "Dotted Circle",
        pathEffect = PathEffect.stampedPathEffect(
            shape = getCirclePath(strokeWidth / 2),
            advance = strokeWidth * 2f,
            phase = 0f,
            style = StampedPathEffectStyle.Rotate
        )
    )
    
    data object SquareBlocks: OutlinedStyle(
        clockId = nextId(),
        displayName = "Square Block",
        pathEffect = PathEffect.stampedPathEffect(
            shape = getSquarePath(strokeWidth * 2, strokeWidth / 3),
            advance = strokeWidth * 3f,
            phase = 0f,
            style = StampedPathEffectStyle.Rotate
        )
    )
    
    data object ZigZagged: OutlinedStyle(
        clockId = nextId(),
        displayName = "Zig Zagged",
        pathEffect = PathEffect.stampedPathEffect(
            shape = getZigZagPath(strokeWidth * 2, strokeWidth * 4, strokeWidth),
            advance = strokeWidth * 4,
            phase = 0f,
            style = StampedPathEffectStyle.Morph
        )
    )
    
    data object DoubleDashed: OutlinedStyle(
        clockId = nextId(),
        displayName = "Double Dashed",
        pathEffect = PathEffect.stampedPathEffect(
            shape = getDoubleDashPath(strokeWidth, strokeWidth, strokeWidth / 2),
            advance = strokeWidth,
            phase = 0f,
            style = StampedPathEffectStyle.Morph
        )
    )
    
    data object LinedDashed: OutlinedStyle(
        clockId = nextId(),
        displayName = "Lined Dashed",
        pathEffect = PathEffect.stampedPathEffect(
            shape = getDoubleDashPath(strokeWidth * 2, strokeWidth, strokeWidth / 2),
            advance = strokeWidth * 2,
            phase = 0f,
            style = StampedPathEffectStyle.Rotate
        )
    )
    
    data object WoodenDashed: OutlinedStyle(
        clockId = nextId(),
        displayName = "Wooden Dashed",
        pathEffect = PathEffect.stampedPathEffect(
            shape = getWoodenPath(strokeWidth * 3, strokeWidth, strokeWidth),
            advance = strokeWidth * 3,
            phase = 0f,
            style = StampedPathEffectStyle.Rotate
        )
    )
}

private val strokeWidth = 7.2f