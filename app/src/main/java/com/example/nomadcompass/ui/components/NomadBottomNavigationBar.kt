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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nomadcompass.R
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
    val targetIndex = if (currentTab == NomadNavTab.EXPLORE) 0 else 1

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceContainerHigh.copy(alpha = if (isDark) 0.94f else 0.98f))
            .border(width = 1.dp, color = OutlineVariant)
            .navigationBarsPadding()
            .height(72.dp)
            .padding(horizontal = 20.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            val totalWidth = maxWidth

            // ── Fluid Elastic Liquid Physics: Asymmetric Leading & Trailing Spring ──
            val leftFraction by animateFloatAsState(
                targetValue = if (targetIndex == 0) 0f else 0.5f,
                animationSpec = spring(
                    dampingRatio = 0.72f, // Natural liquid bounce
                    stiffness = if (targetIndex == 0) Spring.StiffnessMedium else Spring.StiffnessLow
                ),
                label = "liquid_left"
            )

            val rightFraction by animateFloatAsState(
                targetValue = if (targetIndex == 0) 0.5f else 1.0f,
                animationSpec = spring(
                    dampingRatio = 0.72f, // Natural liquid bounce
                    stiffness = if (targetIndex == 1) Spring.StiffnessMedium else Spring.StiffnessLow
                ),
                label = "liquid_right"
            )

            val leftOffset = totalWidth * leftFraction
            val pillWidth = totalWidth * (rightFraction - leftFraction)

            val pillShape = RoundedCornerShape(24.dp)

            // ── Morphing / Fluid Liquid Glass Pill Indicator ──
            Box(
                modifier = Modifier
                    .offset(x = leftOffset)
                    .width(pillWidth)
                    .fillMaxHeight()
                    .padding(horizontal = 4.dp)
                    .clayShadow(
                        cornerRadius = 24.dp,
                        ambientShadowColor = Primary.copy(alpha = if (isDark) 0.38f else 0.22f),
                        spotShadowColor = Primary.copy(alpha = if (isDark) 0.48f else 0.32f),
                        blurRadius = 10.dp
                    )
                    .clip(pillShape)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Primary.copy(alpha = if (isDark) 0.24f else 0.16f),
                                Primary.copy(alpha = if (isDark) 0.18f else 0.12f),
                                Primary.copy(alpha = if (isDark) 0.24f else 0.16f)
                            )
                        ),
                        shape = pillShape
                    )
                    .border(1.5.dp, Primary.copy(alpha = if (isDark) 0.58f else 0.42f), pillShape)
                    .drawBehind {
                        // Liquid top glass specular light reflection
                        drawRect(
                            color = Color.White.copy(alpha = if (isDark) 0.22f else 0.65f),
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
                    iconRes = R.drawable.ic_travel_explore,
                    label = "Explore",
                    isSelected = currentTab == NomadNavTab.EXPLORE,
                    onClick = onExploreClick,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )

                NomadNavTabItem(
                    iconRes = R.drawable.ic_planner_custom,
                    label = "Planner",
                    isSelected = currentTab == NomadNavTab.PLANNER,
                    onClick = onPlannerClick,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
        }
    }
}

@Composable
private fun NomadNavTabItem(
    iconRes: Int,
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
                painter = painterResource(id = iconRes),
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
