package io.github.beankitk.numberbricks.sample.ui.data

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import io.github.beankitk.numberbricks.DigitStyle

object ClockStyles {

    val categories: List<ClockCategory> = listOf(
        Minimal, Outlined
    )

    fun allStyles(): List<ClockStyle> = categories.flatMap { it.styles }

    fun styleFor(id: String): ClockStyle = allStyles().firstOrNull { it.styleId == id } ?: DefaultClock

    fun categoryFor(categoryId: String): List<ClockStyle> =
        categories.firstOrNull { it.id == categoryId }?.styles ?: emptyList()
}

interface ClockCategory {
    val id: String
    val displayName: String
    val styles: List<ClockStyle>
}

open class ClockStyle(
    val categoryId: String,
    val clockId: Int,
    val displayName: String,
    val hourStyle: DigitStyle,
    val minuteStyle: DigitStyle,
    val secondStyle: DigitStyle,
    val showSeconds: Boolean,
    val background: Brush,
    val useThemeColor: Boolean
) {
    val styleId: String = "$categoryId-$clockId"
    
    constructor(
        categoryId: String,
        clockId: Int,
        displayName: String,
        digitStyle: DigitStyle,
        showSeconds: Boolean,
        background: Brush,
        useThemeColor: Boolean
    ) : this(
        categoryId = categoryId,
        clockId = clockId,
        displayName = displayName,
        hourStyle = digitStyle,
        minuteStyle = digitStyle,
        secondStyle = digitStyle,
        showSeconds = showSeconds,
        background = background,
        useThemeColor = useThemeColor
    )
}

data object DefaultClock : ClockStyle (
    categoryId = "default",
    clockId = 0,
    displayName = "Default",
    showSeconds = true,
    background = SolidColor(Color.Black),
    useThemeColor = false,
    digitStyle = DigitStyle.Default
)