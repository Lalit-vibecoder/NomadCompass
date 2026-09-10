package com.example.nomadcompass.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.nomadcompass.ui.theme.LocalThemeController
import com.example.nomadcompass.ui.theme.SurfaceContainer

/**
 * A composable that renders a modern frosted glassmorphic card:
 * - Translucent glass surface tint allowing background wallpaper/content to shine through
 * - Vertical frosted glass specular gradient sheen
 * - Glass refraction rim border
 * - Omni-directional ambient & spot drop shadows
 * - Top-edge specular light highlight reflection
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
    val shape = remember(cornerRadius) { RoundedCornerShape(cornerRadius) }

    val effectiveBg = remember(backgroundColor, isDark) {
        if (backgroundColor.alpha == 1f) {
            backgroundColor.copy(alpha = if (isDark) 0.55f else 0.70f)
        } else {
            backgroundColor
        }
    }

    val ambientShadow = remember(isDark) { Color.Black.copy(alpha = if (isDark) 0.35f else 0.12f) }
    val spotShadow = remember(isDark) { Color.Black.copy(alpha = if (isDark) 0.45f else 0.18f) }

    val borderBrush = remember(isDark) {
        Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = if (isDark) 0.28f else 0.45f),
                Color.White.copy(alpha = if (isDark) 0.08f else 0.18f),
            )
        )
    }

    val frostedSheenBrush = remember(isDark) {
        Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = if (isDark) 0.12f else 0.24f),
                Color.White.copy(alpha = if (isDark) 0.03f else 0.08f),
                Color.Transparent,
            )
        )
    }

    val topHighlightBrush = remember(isDark) {
        Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,
                Color.White.copy(alpha = if (isDark) 0.35f else 0.60f),
                Color.Transparent,
            )
        )
    }

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
            .background(effectiveBg, shape)
            .background(brush = frostedSheenBrush, shape = shape)
            .border(width = 1.dp, brush = borderBrush, shape = shape)
            .drawBehind {
                // Subtle frosted top-edge specular light highlight reflection
                drawRect(
                    brush = topHighlightBrush,
                    size = size.copy(height = 1.5.dp.toPx()),
                )
            },
        content = content,
    )
}

/**
 * Semantic alias for ClayCard with Frosted Glassmorphism aesthetics.
 */
@Composable
fun FrostedGlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    backgroundColor: Color = SurfaceContainer,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) = ClayCard(
    modifier = modifier,
    cornerRadius = cornerRadius,
    backgroundColor = backgroundColor,
    onClick = onClick,
    content = content,
)

/**
 * Semantic alias for ClayCard.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    backgroundColor: Color = SurfaceContainer,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) = ClayCard(
    modifier = modifier,
    cornerRadius = cornerRadius,
    backgroundColor = backgroundColor,
    onClick = onClick,
    content = content,
)

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

