package com.example.nomadcompass.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.nomadcompass.ui.theme.LocalThemeController
import com.example.nomadcompass.ui.theme.OutlineVariant
import com.example.nomadcompass.ui.theme.SurfaceContainer

/**
 * A composable that applies tactile depth:
 * - Dynamic background color
 * - 360-degree omni-directional ambient and directional drop shadows on all sides to strongly pop out
 * - Subtle top-edge light reflection highlight
 * - Crisp theme-adaptive border
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

    val ambientShadow = if (isDark) Color.Black.copy(alpha = 0.70f) else Color(0x300F172A)
    val spotShadow = if (isDark) Color.Black.copy(alpha = 0.85f) else Color(0x250F172A)
    val borderColor = if (isDark) OutlineVariant else OutlineVariant
    val glowColor = if (isDark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.7f)

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
                ambientShadowColor = ambientShadow,
                spotShadowColor = spotShadow,
                blurRadius = if (isDark) 16.dp else 14.dp,
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
 * Hardware-accelerated GPU 360-degree omni-directional shadow modifier running on Android RenderThread.
 * Emits strong ambient shadow on all sides and directional spot shadow to make elements pop out.
 */
fun Modifier.clayShadow(
    cornerRadius: Dp = 24.dp,
    ambientShadowColor: Color = Color.Black.copy(alpha = 0.5f),
    spotShadowColor: Color = Color.Black.copy(alpha = 0.6f),
    blurRadius: Dp = 14.dp,
): Modifier = this.graphicsLayer {
    val elevationPx = blurRadius.toPx()
    if (elevationPx > 0f) {
        this.shadowElevation = elevationPx
        this.shape = RoundedCornerShape(cornerRadius)
        this.clip = false
        this.ambientShadowColor = ambientShadowColor
        this.spotShadowColor = spotShadowColor
    }
}
