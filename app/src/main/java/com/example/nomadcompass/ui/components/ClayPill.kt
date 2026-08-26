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
import com.example.nomadcompass.ui.theme.LocalThemeController
import com.example.nomadcompass.ui.theme.OnPrimaryContainer
import com.example.nomadcompass.ui.theme.OnSurfaceVariant
import com.example.nomadcompass.ui.theme.OutlineVariant
import com.example.nomadcompass.ui.theme.Primary
import com.example.nomadcompass.ui.theme.PrimaryContainer
import com.example.nomadcompass.ui.theme.SurfaceContainerHigh

/**
 * Filter pill for category & filter bars with 360-degree pop-out shadows.
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
    
    val ambientShadow = if (isDark) Color.Black.copy(alpha = 0.55f) else Color(0x280F172A)
    val spotShadow = if (isDark) Color.Black.copy(alpha = 0.75f) else Color(0x1E0F172A)

    // Smooth animated color transitions
    val animatedBgColor by animateColorAsState(
        targetValue = if (isActive) PrimaryContainer else SurfaceContainerHigh,
        animationSpec = tween(durationMillis = 200),
        label = "pill_bg_color"
    )
    val animatedTextColor by animateColorAsState(
        targetValue = if (isActive) OnPrimaryContainer else OnSurfaceVariant,
        animationSpec = tween(durationMillis = 200),
        label = "pill_text_color"
    )
    val animatedBorderColor by animateColorAsState(
        targetValue = if (isActive) Primary.copy(alpha = 0.5f) else OutlineVariant,
        animationSpec = tween(durationMillis = 200),
        label = "pill_border_color"
    )

    val shadowModifier: Modifier = if (!isActive) {
        Modifier.clayShadow(
            cornerRadius = 9999.dp,
            ambientShadowColor = ambientShadow,
            spotShadowColor = spotShadow,
            blurRadius = if (isDark) 10.dp else 8.dp,
        )
    } else {
        Modifier.clayShadow(
            cornerRadius = 9999.dp,
            ambientShadowColor = Primary.copy(alpha = 0.35f),
            spotShadowColor = Primary.copy(alpha = 0.45f),
            blurRadius = 10.dp,
        )
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
