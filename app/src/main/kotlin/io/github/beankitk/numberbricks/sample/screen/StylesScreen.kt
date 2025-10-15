package io.github.beankitk.numberbricks.sample.screen

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsIgnoringVisibility
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.beankitk.numberbricks.sample.data.ClockStyle
import io.github.beankitk.numberbricks.sample.data.ClockStyles
import io.github.beankitk.numberbricks.sample.ui.widget.DigitRow
import io.github.beankitk.numberbricks.sample.ui.widget.PlayPauseFab
import io.github.beankitk.numberbricks.sample.ui.widget.StylesTopBar
import io.github.beankitk.numberbricks.sample.utils.startPadding
import io.github.beankitk.numberbricks.sample.utils.endPadding

@Composable
fun SharedTransitionScope.StylesScreen(
    currentTime: List<Int>,
    isClockRunning: Boolean,
    toggleClockRunning: () -> Unit,
    onClockStyleClick: (String) -> Unit,
    visibilityScope: AnimatedContentScope,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val contentInsets = WindowInsets.systemBarsIgnoringVisibility.union(WindowInsets.displayCutout)
    val categoryListState = rememberLazyListState()
   
    val animateAppBarExit by remember {
        derivedStateOf {
            categoryListState.firstVisibleItemIndex > 0 || categoryListState.firstVisibleItemScrollOffset >= 200
        }
    }
    
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = contentInsets,
        topBar = {
            with(visibilityScope) {
                StylesTopBar(
                    scrollBehavior = scrollBehavior,
                    modifier = Modifier
                        .renderInSharedTransitionScopeOverlay(
                            renderInOverlay = { isTransitionActive && animateAppBarExit },
                            zIndexInOverlay = 3f
                        )
                        .then(
                            if(isTransitionActive && animateAppBarExit)
                                Modifier.animateEnterExit(
                                    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                                    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                                    label = "top_appbar_enter_exit_anim"
                                )
                            else Modifier
                        )
                )
            }    
        },
        floatingActionButton = {
            with(visibilityScope) {
                PlayPauseFab(
                    isPlay = isClockRunning,
                    onClick = toggleClockRunning,
                    modifier = Modifier
                        .renderInSharedTransitionScopeOverlay(zIndexInOverlay = 3f)
                        .animateEnterExit(
                            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                            label = "fab_enter_exit_anim"
                        ),
                )
            }
        },
        content = { innerPadding ->
            LazyColumn(
                state = categoryListState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding() + 16.dp,
                    bottom = innerPadding.calculateBottomPadding() + 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(items = ClockStyles.categories, key = { it.id }) { category -> 
                    val secondDigits = currentTime.subList(4,6)
                    val rows = if(category.styles.size > 4) 2 else 1
                    GridSection(
                        sectionTitle = category.displayName,
                        sectionItems = category.styles,
                        sectionRows = rows,
                        digit = secondDigits,
                        onClick = { 
                            if (!isClockRunning) toggleClockRunning()
                            onClockStyleClick(it.styleId)
                        },
                        padding = innerPadding,
                        visibilityScope = visibilityScope
                    )
                }
            }
        }
    )
}

@Composable
fun SharedTransitionScope.GridSection(
    sectionTitle: String,
    sectionItems: List<ClockStyle>,
    sectionRows: Int = 1,
    sectionItemWidth: Dp = 172.dp,
    sectionItemHeight: Dp = 155.dp,
    digit: List<Int>,
    onClick: (ClockStyle) -> Unit,
    padding: PaddingValues,
    visibilityScope: AnimatedContentScope
) {
    Column {
        Text(
            text = sectionTitle,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(
                start = padding.startPadding() + 24.dp,
                bottom = 8.dp
            )
        )

        LazyHorizontalGrid(
            rows = GridCells.Fixed(sectionRows),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(
                start = padding.startPadding() + 16.dp,
                end = padding.endPadding() + 16.dp
            ),
            modifier = Modifier
                .height(sectionItemHeight * sectionRows)
                .clipToBounds()
        ) {
            items(items = sectionItems, key = { it.styleId }) { style ->
                StyleItem(
                    style = style,
                    width = sectionItemWidth,
                    digit = digit,
                    onClick = onClick,
                    visibilityScope = visibilityScope
                )
            }
        }
    }
}

@Composable
private fun SharedTransitionScope.StyleItem(
    style: ClockStyle,
    width: Dp,
    digit: List<Int>,
    onClick: (ClockStyle) -> Unit,
    visibilityScope: AnimatedContentScope
) {
    val currentDigitStyle =
        if (style.useThemeColor) {
            style.hourStyle.copy(brush = SolidColor(MaterialTheme.colorScheme.onSurfaceVariant))
        } else {
            style.hourStyle
        }

    Surface(
        modifier = Modifier
            .sharedBounds(
                rememberSharedContentState("${style.styleId} + bounds"),
                visibilityScope,
                zIndexInOverlay = 1f,
                clipInOverlayDuringTransition = OverlayClip(MaterialTheme.shapes.medium)
            )
            .width(width),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
        onClick = { onClick(style) }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            DigitRow(
                modifier = Modifier.sharedElement(
                    rememberSharedContentState(style.styleId),
                    visibilityScope,
                    zIndexInOverlay = 2f
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