package io.github.beankitk.numberbricks.sample.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

val supportsDynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

@Composable
fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme =
        when {
            dynamicColor && supportsDynamicColor && useDarkTheme -> {
                dynamicDarkColorScheme(context)
            }
            dynamicColor && supportsDynamicColor && !useDarkTheme -> {
                dynamicLightColorScheme(context)
            }
            useDarkTheme -> darkColorScheme()
            else -> lightColorScheme()
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = shapes,
        content = content
    )
}