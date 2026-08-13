package com.example.nomadcompass.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

/**
 * Filter pill for the category bar.
 *
 * Active state: `.clay-pill-active` — pastel background with inset shadow.
 * Inactive state: `.clay-surface` — dark surface with outer shadow.
 */
@Composable
fun ClayPill(
    text: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(9999.dp)
    val bgColor = if (isActive) PrimaryContainer else SurfaceContainerHigh
    val textColor = if (isActive) OnPrimary else OnSurfaceVariant

    Box(
        modifier = modifier
            .then(
                if (!isActive) {
                    Modifier.clayShadow(
                        cornerRadius = 9999.dp,
                        shadowColor = Color.Black.copy(alpha = 0.4f),
                        blurRadius = 16.dp,
                        offsetX = 8.dp,
                        offsetY = 8.dp,
                    )
                } else {
                    Modifier
                }
            )
            .clip(shape)
            .background(bgColor, shape)
            .then(
                if (!isActive) {
                    Modifier.border(1.dp, Color.White.copy(alpha = 0.05f), shape)
                } else {
                    Modifier
                }
            )
            .then(
                if (isActive) {
                    Modifier.drawBehind {
                        // Inset shadow for active pill
                        drawRect(
                            color = Color.Black.copy(alpha = 0.2f),
                            size = size.copy(height = 2.dp.toPx()),
                        )
                    }
                } else {
                    Modifier
                }
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 28.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
        )
    }
}
