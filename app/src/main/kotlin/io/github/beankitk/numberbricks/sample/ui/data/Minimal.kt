package io.github.beankitk.numberbricks.sample.ui.data

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import io.github.beankitk.numberbricks.DigitStyle

open class MinimalStyle(
    clockId: Int,
    displayName: String,
    hourStyle: DigitStyle,
    minuteStyle: DigitStyle,
    secondStyle: DigitStyle,
    showSeconds: Boolean = true,
    background: Brush = SolidColor(Color.Black),
    useThemeColor: Boolean = true
) : ClockStyle(
    categoryId = "minimal",
    clockId = clockId,
    displayName = displayName,
    hourStyle = hourStyle,
    minuteStyle = minuteStyle,
    secondStyle = secondStyle,
    showSeconds = showSeconds,
    background = background,
    useThemeColor = useThemeColor
)

private var nextId: Int = 1
private fun nextId(): Int = nextId.also { nextId += 1 }

object Minimal : ClockCategory {
    override val id: String = "mininal"
    override val displayName: String = "Minimal"
    override val styles: List<ClockStyle> = listOf(
        BlockIt
    )
    
    data object BlockIt: MinimalStyle(
        clockId = nextId(),
        displayName = "Block It",
        hourStyle = DigitStyle.Default,
        minuteStyle = DigitStyle.Default.copy(alpha = 0.7f),
        secondStyle = DigitStyle.Default.copy(alpha = 0.35f),
        useThemeColor = false
    )
}