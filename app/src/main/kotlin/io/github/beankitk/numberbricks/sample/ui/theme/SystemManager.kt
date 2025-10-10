package io.github.beankitk.numberbricks.sample.ui.theme

import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import io.github.beankitk.numberbricks.sample.ui.theme.isSystemInDarkTheme

@Composable
public fun rememberSystemManager(
    view: View? = null,
    window: Window? = null
): SystemManager {
    val v = view ?: LocalView.current
    val w = window ?: (v.context as? android.app.Activity)?.window
    return remember(v, w) { SystemManager(v, w) }
}

class SystemManager(
    private val view: View,
    private val window: Window?
) {
    private val insetsController = window?.let { WindowInsetsControllerCompat(it, it.decorView) }
    private var _screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
    
    var screenBrightness: Float  
        get() = _screenBrightness  
        set(value) {
            _screenBrightness = value  
            window?.attributes = window?.attributes?.also {  
                it.screenBrightness = value
            }  
        }  
        
    var systemBarsBehavior: Int  
        get() = insetsController?.systemBarsBehavior ?: WindowInsetsControllerCompat.BEHAVIOR_DEFAULT  
        set(value) {  
            insetsController?.systemBarsBehavior = value  
        }  
    
    var isSystemBarsVisible: Boolean  
        get() {  
            return ViewCompat.getRootWindowInsets(view)?.isVisible(WindowInsetsCompat.Type.systemBars()) == true  
        }  
        set(value) {  
            if (value) {  
                insetsController?.show(WindowInsetsCompat.Type.systemBars())  
            } else {  
                insetsController?.hide(WindowInsetsCompat.Type.systemBars())  
            }  
        }  
        
    var isSystemBarsLight: Boolean  
        get() {  
            return insetsController?.isAppearanceLightStatusBars == true   
                || insetsController?.isAppearanceLightNavigationBars == true  
        }  
        set(value) {  
            insetsController?.isAppearanceLightStatusBars = value  
            insetsController?.isAppearanceLightNavigationBars = value  
        }  
    
    fun immersiveView() {  
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE  
    }  
    
    fun resetBrightness() {  
        screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE  
    }  
    
    fun resetWindow() {  
        screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE  
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT  
        isSystemBarsVisible = true  
        isSystemBarsLight = !isSystemInDarkTheme()  
    }
}
