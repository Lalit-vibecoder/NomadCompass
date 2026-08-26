package com.example.nomadcompass.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.nomadcompass.ui.theme.SurfaceContainer

import com.example.nomadcompass.ui.theme.LocalThemeController

/**
 * A composable that applies tactile depth:
 * - Dynamic background color
 * - Theme-aware outer soft shadow for depth (slate in light mode, deep black in dark mode)
 * - Subtle top-edge light reflection highlight
 * - Crisp adaptive border
 */
@Composable
fun ClayCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    backgroundColor: Color = SurfaceContainer,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val isDark = LocalThemeController.current.isDarkMode
    val shape = RoundedCornerShape(cornerRadius)

    val shadowColor = if (isDark) Color.Black.copy(alpha = 0.45f) else Color(0x1A0F172A)
    val borderColor = if (isDark) Color.White.copy(alpha = 0.09f) else Color(0x180F172A)
    val glowColor = if (isDark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.6f)

    Box(
        modifier = modifier
            .then(
                if (onClick != null) {
                    Modifier.bounceClick(scaleDown = 0.98f, onClick = onClick)
                } else {
                    Modifier
                }
            )
            .clayShadow(
                cornerRadius = cornerRadius,
                shadowColor = shadowColor,
                blurRadius = 14.dp,
                offsetX = 0.dp,
                offsetY = 6.dp,
            )
            .clip(shape)
            .background(backgroundColor, shape)
            .border(1.dp, borderColor, shape)
            .drawBehind {
                // Subtle top-edge ambient highlight reflection
                drawRect(
                    color = glowColor,
                    size = size.copy(height = 1.5.dp.toPx()),
                )
            },
        content = content,
    )
}

/**
 * Hardware-accelerated GPU shadow modifier running on Android RenderThread.
 * Eliminates software canvas layer overhead to ensure 60-120fps lag-free screen transitions.
 */
fun Modifier.clayShadow(
    cornerRadius: Dp = 24.dp,
    shadowColor: Color = Color.Black.copy(alpha = 0.3f),
    blurRadius: Dp = 12.dp,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 4.dp,
): Modifier = this.graphicsLayer {
    val elevationPx = blurRadius.toPx()
    if (elevationPx > 0f) {
        this.shadowElevation = elevationPx
        this.shape = RoundedCornerShape(cornerRadius)
        this.clip = false
        this.ambientShadowColor = shadowColor
        this.spotShadowColor = shadowColor
    }
}

