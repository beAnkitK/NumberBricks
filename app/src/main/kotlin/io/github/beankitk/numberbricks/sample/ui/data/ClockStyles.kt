package io.github.beankitk.numberbricks.sample.ui.data

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import io.github.beankitk.numberbricks.DigitStyle

object ClockStyles {

    val categories: List<ClockCategory> = listOf(
        Outlined
    )

    fun allStyles(): List<ClockStyle> = categories.flatMap { it.styles }

    fun styleFor(clockId: String): ClockStyle = allStyles().firstOrNull { it.clockId == clockId } ?: DefaultClock

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
    val clockId: String,
    val displayName: String,
    val showSeconds: Boolean = true,
    val background: Brush = SolidColor(Color.Black),
    val useThemeColor: Boolean = false,
    val digitStyle: DigitStyle = DigitStyle.Default
)

data object DefaultClock : ClockStyle (
    categoryId = "default",
    clockId = "default",
    displayName = "Default",
    showSeconds = true,
    background = SolidColor(Color.Black),
    useThemeColor = false,
    digitStyle = DigitStyle.Default
)