package io.github.beankitk.numberbricks.sample

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.beankitk.numberbricks.sample.ui.theme.AppTheme

class ContentActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        setupActivity()
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme(dynamicColor = false) {
                HomeScreen()
            }
        }
    }
    
    fun setupActivity() {
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
    }
}
