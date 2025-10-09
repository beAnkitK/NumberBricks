package io.github.beankitk.numberbricks.sample

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import io.github.beankitk.numberbricks.sample.screen.ClockScreen
import io.github.beankitk.numberbricks.sample.screen.StylesScreen
import io.github.beankitk.numberbricks.sample.viewmodel.ClockScreenVM
import io.github.beankitk.numberbricks.sample.viewmodel.TimeVM
import kotlinx.serialization.Serializable

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val activity = LocalActivity.current
    val navController = rememberNavController()
    
    BackHandler(navController.previousBackStackEntry == null) {
        activity?.finish()
    }
    
    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = StylesPage,
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            composable<StylesPage> {
                val timeVM: TimeVM = viewModel()
                val currentTime by timeVM.currentTimeAsList.collectAsStateWithLifecycle()
                val isClockRunning by timeVM.isClockRunning.collectAsStateWithLifecycle()
                
                StylesScreen(
                    visibilityScope = this,
                    boundsTransition = boundsTransition,
                    currentTime = currentTime,
                    isClockRunning = isClockRunning,
                    toggleClockRunning = timeVM::toggleClockRunning,
                    onClockStyleClick = { navController.navigate(ClockPage(it)) }
                )
            }
            composable<ClockPage> {
                val args = it.toRoute<ClockPage>()
                val clockScreenVM: ClockScreenVM = viewModel()
                val currentTime by clockScreenVM.currentTimeAsList.collectAsStateWithLifecycle()
                val isAmbientMode by clockScreenVM.isAmbientMode.collectAsStateWithLifecycle()
                val isLargeClock by clockScreenVM.isLargeClock.collectAsStateWithLifecycle()
    
                ClockScreen(
                    styleId = args.styleId,
                    visibilityScope = this,
                    boundsTransition = boundsTransition,
                    currentTime = currentTime,
                    isAmbientMode = isAmbientMode,
                    isLargeClock = isLargeClock,
                    toggleAmbientMode = clockScreenVM::toggleAmbientMode,
                    toggleLargeClock = clockScreenVM::toggleLargeClock,
                    scheduleAmbientMode = clockScreenVM::scheduleAmbientMode
                )
            }
        }
    }
}

@Serializable
data object StylesPage

@Serializable
data class ClockPage(val styleId: String)

private val boundsTransition = BoundsTransform { _, _ -> tween(300) }