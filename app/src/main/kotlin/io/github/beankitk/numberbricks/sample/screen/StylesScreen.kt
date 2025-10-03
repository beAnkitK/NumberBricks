package io.github.beankitk.numberbricks.sample.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.FabPosition
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.navigation.NavController
import io.github.beankitk.numberbricks.sample.ClockPage
import io.github.beankitk.numberbricks.sample.R
import io.github.beankitk.numberbricks.sample.ui.animation.elasticIn
import io.github.beankitk.numberbricks.sample.ui.data.ClockStyle
import io.github.beankitk.numberbricks.sample.ui.data.ClockStyles
import io.github.beankitk.numberbricks.sample.ui.icon.Icons
import io.github.beankitk.numberbricks.sample.ui.theme.AntonFont
import io.github.beankitk.numberbricks.sample.ui.widget.DigitRow
import io.github.beankitk.numberbricks.sample.utils.getTime
import io.github.beankitk.numberbricks.sample.utils.toDigitList
import kotlinx.coroutines.delay

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.StylesScreen(
    navController: NavController,
    visibilityScope: AnimatedContentScope,
    boundsTransition: BoundsTransform,
) {
    val displaySmall = MaterialTheme.typography.displaySmall.copy(fontFamily = AntonFont)
    val headlineLarge = MaterialTheme.typography.headlineLarge.copy(fontFamily = AntonFont)
    val fabInteractionSource = MutableInteractionSource()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    
    var digit by remember { mutableStateOf(getTime().toDigitList().subList(0,2)) }
    var isRunning by remember { mutableStateOf(false) }
    var increasing by remember { mutableStateOf(true) }

    val titleStyle by remember {
        derivedStateOf { lerp(displaySmall, headlineLarge, scrollBehavior.state.overlappedFraction) }
    }
    
    val isOverlapThresholdCrossed by remember {
       derivedStateOf { if (scrollBehavior.state.overlappedFraction > 0.3f) true else false } 
    }
    
    /**
    LaunchedEffect(isRunning) {
        if (!isRunning) return@LaunchedEffect
        while (true) {
            delay(1000L)
            digit += if (increasing) 1 else -1
            if (digit == 0 || digit == 9) increasing = !increasing
        }
    }
    */

    Scaffold(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .background(Brush.verticalGradient(listOf(MaterialTheme.colorScheme.surface, Color.Transparent)))
                    .padding(top = 12.dp),
                title = {
                    Column(modifier = Modifier.fillMaxHeight()) {
                        Text(
                            text = stringResource(R.string.app_name).uppercase(),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = titleStyle,
                        )
                        AnimatedVisibility(
                            visible = !isOverlapThresholdCrossed,
                            modifier = Modifier.fillMaxSize(),
                            enter = slideInVertically() + fadeIn(),
                            exit = slideOutVertically() + fadeOut(),
                            label = "subheading-visibility"
                        ) {
                            Text(
                                text = stringResource(R.string.subheadline),
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                ),
                expandedHeight = 80.dp,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                modifier = Modifier
                    .indication(fabInteractionSource, elasticIn(0.9f))
                    .padding(end = 12.dp),
                onClick = { isRunning = !isRunning },
                interactionSource = fabInteractionSource,    
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                AnimatedContent(isRunning) { isRunning ->
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isRunning) {
                            Icon(
                                imageVector = Icons.Pause,
                                contentDescription = "Pause"
                            )
                            Text(
                                text = stringResource(R.string.pause),
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Play,
                                contentDescription = "Resume"
                            )
                            Text(
                                text = stringResource(R.string.play),
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        },
        content = { innerPadding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = innerPadding.calculateTopPadding() + 16.dp,
                    bottom = innerPadding.calculateBottomPadding() + 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(
                    items = ClockStyles.categories,
                    key = { category -> category.id }
                ) { category -> 
                    GridSection(
                        sectionTitle = category.displayName,
                        sectionItems = category.styles,
                        sectionRows = 2,
                        digit = digit,
                        onClick = { style ->
                            navController.navigate(ClockPage(style.clockId))
                        },
                        visibilityScope = visibilityScope,
                        boundsTransition = boundsTransition
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.GridSection(
    sectionTitle: String,
    sectionItems: List<ClockStyle>,
    sectionRows: Int = 1,
    sectionItemWidth: Dp = 172.dp,
    sectionItemHeight: Dp = 155.dp,
    digit: List<Int>,
    onClick: (ClockStyle) -> Unit,
    visibilityScope: AnimatedContentScope,
    boundsTransition: BoundsTransform
) {
    Column {
        Text(
            text = sectionTitle,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        
        LazyHorizontalGrid(
            rows = GridCells.Fixed(sectionRows),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .height(sectionItemHeight * sectionRows)
                .clipToBounds()
        ) {
            items(sectionItems) { style ->
                val currentDigitStyle = if (style.useThemeColor)
                    style.digitStyle.copy(brush = SolidColor(MaterialTheme.colorScheme.onSurfaceVariant))
                else style.digitStyle

                Surface(
                    modifier = Modifier
                        .sharedBounds(
                            rememberSharedContentState(style.clockId + "bounds"),
                            visibilityScope,
                            boundsTransform = boundsTransition,
                        )
                        .width(sectionItemWidth),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    onClick = { onClick(style) }
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        DigitRow(
                            modifier = Modifier
                                .sharedElement(
                                    rememberSharedContentState(style.clockId),
                                    visibilityScope
                                ),
                            digits = digit,
                            digitStyle = currentDigitStyle,
                            brickSizeMultiplier = 18f,
                            animateDigits = true,
                            animateOnFirstVisible = false
                        )
                    }
                }
            }
        }
    }
}