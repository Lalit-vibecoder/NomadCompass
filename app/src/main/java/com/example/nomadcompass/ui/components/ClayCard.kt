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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.nomadcompass.ui.theme.SurfaceContainer

/**
 * A composable that applies the Claymorphism tactile look:
 * - Dark matte background
 * - Outer soft shadow for depth
 * - Subtle top-left inset highlight
 * - Thin translucent border
 *
 * Matches the `.clay-card` CSS from the Stitch mockups.
 */
@Composable
fun ClayCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 32.dp,
    backgroundColor: Color = SurfaceContainer,
    content: @Composable BoxScope.() -> Unit,
) {
    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .clayShadow(
                cornerRadius = cornerRadius,
                shadowColor = Color.Black.copy(alpha = 0.3f),
                blurRadius = 12.dp,
                offsetX = 4.dp,
                offsetY = 4.dp,
            )
            .clip(shape)
            .background(backgroundColor, shape)
            .border(1.dp, Color.White.copy(alpha = 0.03f), shape)
            // Inset highlight simulated via a thin top/left inner padding glow
            .drawBehind {
                // Top-left inset highlight
                drawRect(
                    color = Color.White.copy(alpha = 0.05f),
                    size = size.copy(height = 2.dp.toPx()),
                )
                drawRect(
                    color = Color.White.copy(alpha = 0.03f),
                    size = size.copy(width = 2.dp.toPx()),
                )
            },
        content = content,
    )
}

/**
 * Custom shadow modifier using Canvas Paint blur for soft drop-shadow effect.
 */
fun Modifier.clayShadow(
    cornerRadius: Dp = 32.dp,
    shadowColor: Color = Color.Black.copy(alpha = 0.3f),
    blurRadius: Dp = 12.dp,
    offsetX: Dp = 4.dp,
    offsetY: Dp = 4.dp,
): Modifier = this.drawBehind {
    drawIntoCanvas { canvas ->
        val paint = Paint().also {
            val frameworkPaint = it.asFrameworkPaint()
            frameworkPaint.color = shadowColor.toArgb()
            frameworkPaint.setShadowLayer(
                blurRadius.toPx(),
                offsetX.toPx(),
                offsetY.toPx(),
                shadowColor.toArgb(),
            )
        }
        val cornerPx = cornerRadius.toPx()
        canvas.drawRoundRect(
            left = 0f,
            top = 0f,
            right = size.width,
            bottom = size.height,
            radiusX = cornerPx,
            radiusY = cornerPx,
            paint = paint,
        )
    }
}
