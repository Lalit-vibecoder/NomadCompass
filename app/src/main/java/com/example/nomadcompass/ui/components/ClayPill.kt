package com.example.nomadcompass.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.nomadcompass.ui.theme.OnPrimary
import com.example.nomadcompass.ui.theme.OnSurfaceVariant
import com.example.nomadcompass.ui.theme.PrimaryContainer
import com.example.nomadcompass.ui.theme.SurfaceContainerHigh

import com.example.nomadcompass.ui.theme.LocalThemeController

/**
 * Filter pill for category & filter bars.
 * Adaptive theme colors and tactile bounce feedback.
 */
@Composable
fun ClayPill(
    text: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDark = LocalThemeController.current.isDarkMode
    val shape = RoundedCornerShape(9999.dp)
    
    val inactiveBorder = if (isDark) Color.White.copy(alpha = 0.09f) else Color(0x1A0F172A)
    val shadowColor = if (isDark) Color.Black.copy(alpha = 0.38f) else Color(0x140F172A)

    // Smooth animated color transitions
    val animatedBgColor by animateColorAsState(
        targetValue = if (isActive) PrimaryContainer else SurfaceContainerHigh,
        animationSpec = tween(durationMillis = 200),
        label = "pill_bg_color"
    )
    val animatedTextColor by animateColorAsState(
        targetValue = if (isActive) com.example.nomadcompass.ui.theme.OnPrimaryContainer else OnSurfaceVariant,
        animationSpec = tween(durationMillis = 200),
        label = "pill_text_color"
    )
    val animatedBorderColor by animateColorAsState(
        targetValue = if (isActive) com.example.nomadcompass.ui.theme.Primary.copy(alpha = 0.5f) else inactiveBorder,
        animationSpec = tween(durationMillis = 200),
        label = "pill_border_color"
    )

    val shadowModifier: Modifier = if (!isActive) {
        Modifier.clayShadow(
            cornerRadius = 9999.dp,
            shadowColor = shadowColor,
            blurRadius = 12.dp,
            offsetX = 0.dp,
            offsetY = 4.dp,
        )
    } else {
        Modifier
    }

    val activeHighlightModifier: Modifier = if (isActive) {
        Modifier.drawBehind {
            drawRect(
                color = Color.Black.copy(alpha = 0.2f),
                size = size.copy(height = 2.dp.toPx()),
            )
        }
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .bounceClick(scaleDown = 0.94f, onClick = onClick)
            .bounceOnState(state = isActive, maxScale = 1.05f)
            .then(shadowModifier)
            .clip(shape)
            .background(animatedBgColor, shape)
            .border(1.dp, animatedBorderColor, shape)
            .then(activeHighlightModifier)
            .padding(horizontal = 28.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = animatedTextColor,
        )
    }
}
