package com.example.nomadcompass.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nomadcompass.R
import com.example.nomadcompass.ui.theme.icons.PhosphorIcons
import com.example.nomadcompass.ui.theme.LocalThemeController
import com.example.nomadcompass.ui.theme.OnSurfaceVariant
import com.example.nomadcompass.ui.theme.OutlineVariant
import com.example.nomadcompass.ui.theme.Primary
import com.example.nomadcompass.ui.theme.SurfaceContainerHigh

enum class NomadNavTab {
    EXPLORE,
    PLANNER
}

/**
 * Stationary Bottom Navigation Bar with fluid elastic liquid glass morphing animation.
 * Features asymmetric leading/trailing liquid stretching when transitioning between tabs.
 */
@Composable
fun NomadBottomNavigationBar(
    currentTab: NomadNavTab,
    onExploreClick: () -> Unit,
    onPlannerClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDark = LocalThemeController.current.isDarkMode
    var activeTab by remember(currentTab) { mutableStateOf(currentTab) }
    val targetIndex = if (activeTab == NomadNavTab.EXPLORE) 0 else 1

    var previousIndex by remember { mutableIntStateOf(targetIndex) }
    val isMovingRight = targetIndex >= previousIndex

    LaunchedEffect(targetIndex) {
        previousIndex = targetIndex
    }

    val navBarShape = remember { RoundedCornerShape(32.dp) }
    val surfaceHigh = SurfaceContainerHigh
    val navBarBgBrush = remember(surfaceHigh, isDark) {
        Brush.verticalGradient(
            colors = listOf(
                surfaceHigh.copy(alpha = if (isDark) 0.75f else 0.85f),
                surfaceHigh.copy(alpha = if (isDark) 0.65f else 0.75f)
            )
        )
    }
    val navBarSpecularColor = remember(isDark) {
        Color.White.copy(alpha = if (isDark) 0.30f else 0.50f)
    }
    val pillShape = remember { RoundedCornerShape(24.dp) }
    val pillBgBrush = remember(isDark) {
        Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = if (isDark) 0.18f else 0.30f),
                Color.White.copy(alpha = if (isDark) 0.08f else 0.15f)
            )
        )
    }
    val pillSpecularColor = remember(isDark) {
        Color.White.copy(alpha = if (isDark) 0.40f else 0.70f)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clayShadow(
                    cornerRadius = 32.dp,
                    ambientShadowColor = Color.Black.copy(alpha = if (isDark) 0.60f else 0.22f),
                    spotShadowColor = Color.Black.copy(alpha = if (isDark) 0.80f else 0.32f),
                    blurRadius = 20.dp
                )
                .clip(navBarShape)
                .background(
                    brush = navBarBgBrush,
                    shape = navBarShape
                )
                .border(
                    width = 1.2.dp,
                    color = if (isDark) Color.White.copy(alpha = 0.22f) else Color.White.copy(alpha = 0.40f),
                    shape = navBarShape
                )
                .drawBehind {
                    drawRect(
                        color = navBarSpecularColor,
                        size = size.copy(height = 1.5.dp.toPx())
                    )
                }
                .padding(horizontal = 8.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                contentAlignment = Alignment.CenterStart
            ) {
            val totalWidth = maxWidth

            // ── Fluid Elastic Liquid Physics: Asymmetric Leading & Trailing Spring ──
            val leftFraction by animateFloatAsState(
                targetValue = if (targetIndex == 0) 0f else 0.5f,
                animationSpec = spring(
                    dampingRatio = 0.72f, // Natural liquid bounce
                    stiffness = if (!isMovingRight) Spring.StiffnessMedium else Spring.StiffnessLow
                ),
                label = "liquid_left"
            )

            val rightFraction by animateFloatAsState(
                targetValue = if (targetIndex == 0) 0.5f else 1.0f,
                animationSpec = spring(
                    dampingRatio = 0.72f, // Natural liquid bounce
                    stiffness = if (isMovingRight) Spring.StiffnessMedium else Spring.StiffnessLow
                ),
                label = "liquid_right"
            )

            val leftOffset = totalWidth * leftFraction
            val pillWidth = totalWidth * (rightFraction - leftFraction)

            // ── Morphing / Fluid Liquid Glass Pill Indicator ──
            Box(
                modifier = Modifier
                    .offset(x = leftOffset)
                    .width(pillWidth)
                    .fillMaxHeight()
                    .padding(horizontal = 4.dp)
                    .clayShadow(
                        cornerRadius = 24.dp,
                        ambientShadowColor = Color.Black.copy(alpha = if (isDark) 0.35f else 0.12f),
                        spotShadowColor = Color.Black.copy(alpha = if (isDark) 0.45f else 0.18f),
                        blurRadius = 8.dp
                    )
                    .clip(pillShape)
                    .background(
                        brush = pillBgBrush,
                        shape = pillShape
                    )
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = if (isDark) 0.28f else 0.45f),
                        shape = pillShape
                    )
                    .drawBehind {
                        // Liquid top glass specular light reflection
                        drawRect(
                            color = pillSpecularColor,
                            size = size.copy(height = 1.5.dp.toPx())
                        )
                    }
            )

            // ── Interactive Navigation Tab Items Layer ──
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NomadNavTabItem(
                    icon = PhosphorIcons.Compass,
                    label = "Explore",
                    isSelected = activeTab == NomadNavTab.EXPLORE,
                    onClick = {
                        if (activeTab != NomadNavTab.EXPLORE) {
                            activeTab = NomadNavTab.EXPLORE
                            onExploreClick()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )

                NomadNavTabItem(
                    icon = PhosphorIcons.CalendarDots,
                    label = "Planner",
                    isSelected = activeTab == NomadNavTab.PLANNER,
                    onClick = {
                        if (activeTab != NomadNavTab.PLANNER) {
                            activeTab = NomadNavTab.PLANNER
                            onPlannerClick()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
        }
    }
}
}

@Composable
private fun NomadNavTabItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }

    // Text & icon color animation
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Primary else OnSurfaceVariant,
        animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
        label = "nav_content_color"
    )

    // Micro-scale for icon on active tab
    val iconScale by animateFloatAsState(
        targetValue = if (isSelected) 1.08f else 1.0f,
        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
        label = "nav_icon_scale"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier
                    .size(24.dp)
                    .graphicsLayer {
                        scaleX = iconScale
                        scaleY = iconScale
                    }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                ),
                color = contentColor
            )
        }
    }
}
