package com.example.nomadcompass.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nomadcompass.ui.theme.LocalThemeController
import com.example.nomadcompass.ui.theme.PillActiveBackground
import com.example.nomadcompass.ui.theme.PillActiveText
import com.example.nomadcompass.ui.theme.PillInactiveBackground
import com.example.nomadcompass.ui.theme.PillInactiveBorder
import com.example.nomadcompass.ui.theme.PillInactiveText

/**
 * Premium tactile tab bar with an animated fluid sliding background pill indicator.
 * Employs asymmetric leading/trailing liquid spring physics so the indicator dynamically
 * stretches in the direction of motion before snapping snugly over the selected tab.
 */
@Composable
fun <T> ClaySlidingTabRow(
    tabs: List<T>,
    selectedTab: T,
    onTabSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 42.dp,
    cornerRadius: Dp = 21.dp,
    trackPadding: PaddingValues = PaddingValues(3.dp),
    fontSize: TextUnit = 12.5.sp,
    labelProvider: (T) -> String = { it.toString() },
    iconProvider: (@Composable (tab: T, isSelected: Boolean) -> Unit)? = null
) {
    if (tabs.isEmpty()) return

    val isDark = LocalThemeController.current.isDarkMode
    val trackShape = RoundedCornerShape(cornerRadius)
    val indicatorShape = RoundedCornerShape((cornerRadius.value - 2).coerceAtLeast(12f).dp)

    val tabCount = tabs.size
    val selectedIndex = tabs.indexOf(selectedTab).coerceAtLeast(0)

    var previousIndex by remember { mutableIntStateOf(selectedIndex) }
    val isMovingRight = selectedIndex >= previousIndex

    LaunchedEffect(selectedIndex) {
        previousIndex = selectedIndex
    }

    val targetLeftFraction = selectedIndex.toFloat() / tabCount
    val targetRightFraction = (selectedIndex + 1).toFloat() / tabCount

    // Asymmetric fluid liquid spring animation: leading edge moves faster than trailing edge
    val leftFraction by animateFloatAsState(
        targetValue = targetLeftFraction,
        animationSpec = spring(
            dampingRatio = 0.74f,
            stiffness = if (!isMovingRight) Spring.StiffnessMedium else Spring.StiffnessLow
        ),
        label = "sliding_tab_left"
    )

    val rightFraction by animateFloatAsState(
        targetValue = targetRightFraction,
        animationSpec = spring(
            dampingRatio = 0.74f,
            stiffness = if (isMovingRight) Spring.StiffnessMedium else Spring.StiffnessLow
        ),
        label = "sliding_tab_right"
    )

    // Outer Track
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(trackShape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        PillInactiveBackground.copy(alpha = if (isDark) 0.28f else 0.16f),
                        PillInactiveBackground.copy(alpha = if (isDark) 0.16f else 0.08f)
                    )
                ),
                shape = trackShape
            )
            .border(
                width = 1.dp,
                color = PillInactiveBorder.copy(alpha = if (isDark) 0.35f else 0.22f),
                shape = trackShape
            )
            .padding(trackPadding),
        contentAlignment = Alignment.CenterStart
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterStart
        ) {
            val totalWidth = maxWidth
            val leftOffset = totalWidth * leftFraction
            val indicatorWidth = (totalWidth * (rightFraction - leftFraction)).coerceAtLeast(0.dp)

            // Animated Sliding Background Pill Indicator
            Box(
                modifier = Modifier
                    .offset(x = leftOffset)
                    .width(indicatorWidth)
                    .fillMaxHeight()
                    .clayShadow(
                        cornerRadius = (cornerRadius.value - 2).coerceAtLeast(12f).dp,
                        ambientShadowColor = Color.Black.copy(alpha = if (isDark) 0.35f else 0.12f),
                        spotShadowColor = Color.Black.copy(alpha = if (isDark) 0.45f else 0.18f),
                        blurRadius = 8.dp
                    )
                    .clip(indicatorShape)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                PillActiveBackground,
                                PillActiveBackground.copy(alpha = if (isDark) 0.92f else 0.98f)
                            )
                        ),
                        shape = indicatorShape
                    )
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = if (isDark) 0.45f else 0.60f),
                        shape = indicatorShape
                    )
                    .drawBehind {
                        // Specular top light sheen for glass/porcelain depth
                        drawRect(
                            color = Color.White.copy(alpha = if (isDark) 0.50f else 0.80f),
                            size = size.copy(height = 1.2.dp.toPx())
                        )
                    }
            )

            // Interactive Tab Items Layer
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEachIndexed { index, tab ->
                    val isSelected = index == selectedIndex
                    val interactionSource = remember { MutableInteractionSource() }

                    val textColor by animateColorAsState(
                        targetValue = if (isSelected) PillActiveText else PillInactiveText,
                        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
                        label = "tab_item_text_color"
                    )

                    val textScale by animateFloatAsState(
                        targetValue = if (isSelected) 1.03f else 1.0f,
                        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
                        label = "tab_item_scale"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(indicatorShape)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = { onTabSelected(tab) }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .graphicsLayer {
                                    scaleX = textScale
                                    scaleY = textScale
                                }
                        ) {
                            if (iconProvider != null) {
                                iconProvider(tab, isSelected)
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = labelProvider(tab),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontSize = fontSize,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = textColor,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Dynamic variable-width / scrollable pill row featuring a smooth sliding animated background indicator.
 * Dynamically measures individual tab positions and bounds and glides the indicator behind the active tab.
 */
@Composable
fun <T> ScrollableClaySlidingTabRow(
    tabs: List<T>,
    selectedTab: T,
    onTabSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp),
    height: Dp = 40.dp,
    spacing: Dp = 10.dp,
    fontSize: TextUnit = 13.5.sp,
    labelProvider: (T) -> String = { it.toString() },
) {
    if (tabs.isEmpty()) return

    val isDark = LocalThemeController.current.isDarkMode
    val scrollState = rememberScrollState()
    val density = LocalDensity.current

    val selectedIndex = tabs.indexOf(selectedTab).coerceAtLeast(0)

    val tabPositions = remember { mutableStateMapOf<Int, Pair<Dp, Dp>>() }
    var containerCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }

    // Auto-scroll to selected item when selection changes
    LaunchedEffect(selectedIndex, tabPositions[selectedIndex]) {
        val targetPos = tabPositions[selectedIndex]
        if (targetPos != null) {
            val targetPx = with(density) { targetPos.first.toPx().toInt() }
            val widthPx = with(density) { targetPos.second.toPx().toInt() }
            val scrollDestination = (targetPx - widthPx / 2).coerceAtLeast(0)
            scrollState.animateScrollTo(scrollDestination)
        }
    }

    val currentTarget = tabPositions[selectedIndex]
    val targetOffset = currentTarget?.first ?: 0.dp
    val targetWidth = currentTarget?.second ?: 0.dp
    val hasMeasured = currentTarget != null

    var previousIndex by remember { mutableIntStateOf(selectedIndex) }
    val isMovingRight = selectedIndex >= previousIndex

    LaunchedEffect(selectedIndex) {
        previousIndex = selectedIndex
    }

    val animatedOffset by animateDpAsState(
        targetValue = targetOffset,
        animationSpec = spring(
            dampingRatio = 0.74f,
            stiffness = if (!isMovingRight) androidx.compose.animation.core.Spring.StiffnessMedium else androidx.compose.animation.core.Spring.StiffnessLow
        ),
        label = "scrollable_tab_offset"
    )

    val animatedWidth by animateDpAsState(
        targetValue = targetWidth,
        animationSpec = spring(
            dampingRatio = 0.74f,
            stiffness = if (isMovingRight) androidx.compose.animation.core.Spring.StiffnessMedium else androidx.compose.animation.core.Spring.StiffnessLow
        ),
        label = "scrollable_tab_width"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .horizontalScroll(scrollState)
            .padding(contentPadding)
            .onGloballyPositioned { containerCoordinates = it },
        contentAlignment = Alignment.CenterStart
    ) {
        // Sliding Background Indicator Layer
        if (hasMeasured && targetWidth > 0.dp) {
            Box(
                modifier = Modifier
                    .offset(x = animatedOffset)
                    .width(animatedWidth)
                    .fillMaxHeight()
                    .clayShadow(
                        cornerRadius = 9999.dp,
                        ambientShadowColor = Color.Black.copy(alpha = if (isDark) 0.35f else 0.12f),
                        spotShadowColor = Color.Black.copy(alpha = if (isDark) 0.45f else 0.18f),
                        blurRadius = 8.dp
                    )
                    .clip(RoundedCornerShape(9999.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                PillActiveBackground,
                                PillActiveBackground.copy(alpha = if (isDark) 0.94f else 0.98f)
                            )
                        ),
                        shape = RoundedCornerShape(9999.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = if (isDark) 0.45f else 0.60f),
                        shape = RoundedCornerShape(9999.dp)
                    )
                    .drawBehind {
                        drawRect(
                            color = Color.White.copy(alpha = if (isDark) 0.50f else 0.80f),
                            size = size.copy(height = 1.2.dp.toPx())
                        )
                    }
            )
        }

        // Tab Items Layer
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxHeight()
        ) {
            tabs.forEachIndexed { index, tab ->
                val isSelected = index == selectedIndex
                val interactionSource = remember { MutableInteractionSource() }

                val textColor by animateColorAsState(
                    targetValue = if (isSelected) PillActiveText else PillInactiveText,
                    animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
                    label = "scrollable_tab_text_color"
                )

                val textScale by animateFloatAsState(
                    targetValue = if (isSelected) 1.04f else 1.0f,
                    animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
                    label = "scrollable_tab_scale"
                )

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .onGloballyPositioned { tabCoords ->
                            val container = containerCoordinates
                            if (container != null && container.isAttached && tabCoords.isAttached) {
                                val positionInContainer = container.localPositionOf(tabCoords, androidx.compose.ui.geometry.Offset.Zero)
                                val xDp = with(density) { positionInContainer.x.toDp() }
                                val wDp = with(density) { tabCoords.size.width.toDp() }
                                tabPositions[index] = Pair(xDp, wDp)
                            }
                        }
                        .clip(RoundedCornerShape(9999.dp))
                        .background(
                            color = if (isSelected) Color.Transparent else PillInactiveBackground,
                            shape = RoundedCornerShape(9999.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = if (isSelected) Color.Transparent else PillInactiveBorder,
                            shape = RoundedCornerShape(9999.dp)
                        )
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = { onTabSelected(tab) }
                        )
                        .padding(horizontal = 18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = labelProvider(tab),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontSize = fontSize,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                        ),
                        color = textColor,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        modifier = Modifier.graphicsLayer {
                            scaleX = textScale
                            scaleY = textScale
                        }
                    )
                }
            }
        }
    }
}

