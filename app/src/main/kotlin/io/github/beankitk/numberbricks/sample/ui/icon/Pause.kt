package io.github.beankitk.numberbricks.sample.ui.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val _Pause: ImageVector by lazy {
    ImageVector.Builder(
        name = "Pause",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(
            fill = SolidColor(Color.Black)
        ) {
            moveTo(600f, 720f)
            quadToRelative(-16.08f, 0f, -28.04f, -11.96f)
            reflectiveQuadTo(560f, 680f)
            verticalLineToRelative(-400f)
            quadToRelative(0f, -16.08f, 11.96f, -28.04f)
            reflectiveQuadTo(600f, 240f)
            horizontalLineToRelative(60f)
            quadToRelative(16.08f, 0f, 28.04f, 11.96f)
            reflectiveQuadTo(700f, 280f)
            verticalLineToRelative(400f)
            quadToRelative(0f, 16.08f, -11.96f, 28.04f)
            reflectiveQuadTo(660f, 720f)
            horizontalLineToRelative(-60f)
            close()
            moveToRelative(-300f, 0f)
            quadToRelative(-16.08f, 0f, -28.04f, -11.96f)
            reflectiveQuadTo(260f, 680f)
            verticalLineToRelative(-400f)
            quadToRelative(0f, -16.08f, 11.96f, -28.04f)
            reflectiveQuadTo(300f, 240f)
            horizontalLineToRelative(60f)
            quadToRelative(16.08f, 0f, 28.04f, 11.96f)
            reflectiveQuadTo(400f, 280f)
            verticalLineToRelative(400f)
            quadToRelative(0f, 16.08f, -11.96f, 28.04f)
            reflectiveQuadTo(360f, 720f)
            horizontalLineToRelative(-60f)
            close()
        }
    }.build()
}