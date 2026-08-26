package com.example.nomadcompass.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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

@Composable
fun NomadBottomNavigationBar(
    currentTab: NomadNavTab,
    onExploreClick: () -> Unit,
    onPlannerClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDark = LocalThemeController.current.isDarkMode

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceContainerHigh.copy(alpha = if (isDark) 0.94f else 0.98f))
            .border(width = 1.dp, color = OutlineVariant)
            .navigationBarsPadding()
            .height(72.dp)
            .padding(horizontal = 24.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NomadNavItem(
                iconRes = R.drawable.ic_travel_explore,
                label = "Explore",
                isSelected = currentTab == NomadNavTab.EXPLORE,
                onClick = onExploreClick
            )

            NomadNavItem(
                iconRes = R.drawable.ic_planner_custom,
                label = "Planner",
                isSelected = currentTab == NomadNavTab.PLANNER,
                onClick = onPlannerClick
            )
        }
    }
}

@Composable
private fun NomadNavItem(
    iconRes: Int,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val isDark = LocalThemeController.current.isDarkMode

    val bgColor by animateColorAsState(
        targetValue = if (isSelected) {
            Primary.copy(alpha = if (isDark) 0.18f else 0.12f)
        } else {
            Color.Transparent
        },
        animationSpec = tween(200),
        label = "nav_bg"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) {
            Primary.copy(alpha = if (isDark) 0.45f else 0.35f)
        } else {
            Color.Transparent
        },
        animationSpec = tween(200),
        label = "nav_border"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Primary else OnSurfaceVariant,
        animationSpec = tween(200),
        label = "nav_content"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .bounceClick(scaleDown = 0.94f, onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 10.dp),
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
                modifier = Modifier.size(24.dp)
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
