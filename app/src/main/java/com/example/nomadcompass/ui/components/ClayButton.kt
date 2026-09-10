package com.example.nomadcompass.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.nomadcompass.ui.theme.LocalThemeController
import com.example.nomadcompass.ui.theme.OnPrimaryContainer
import com.example.nomadcompass.ui.theme.PillActiveBackground
import com.example.nomadcompass.ui.theme.PillActiveText

/**
 * A tactile pill/capsule primary button with soft drop shadows.
 */
@Composable
fun ClayButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = PillActiveBackground,
    contentColor: Color = PillActiveText,
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
    content: @Composable RowScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Button(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = modifier
            .pressScale(interactionSource, scaleDown = 0.96f)
            .clayShadow(
                cornerRadius = 9999.dp,
                ambientShadowColor = Color.Black.copy(alpha = 0.50f),
                spotShadowColor = Color.Black.copy(alpha = 0.70f),
                blurRadius = 10.dp,
            ),
        enabled = enabled,
        shape = RoundedCornerShape(9999.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.4f),
            disabledContentColor = contentColor.copy(alpha = 0.4f),
        ),
        contentPadding = contentPadding,
        content = content,
    )
}

/**
 * Translucent frosted white glass pill button matching the bottom navigation bar active pill aesthetic.
 */
@Composable
fun GlassPillButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 9999.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 22.dp, vertical = 12.dp),
    content: @Composable RowScope.() -> Unit,
) {
    val isDark = LocalThemeController.current.isDarkMode
    val pillShape = remember(cornerRadius) { RoundedCornerShape(cornerRadius) }
    val bgBrush = remember(isDark) {
        Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = if (isDark) 0.18f else 0.30f),
                Color.White.copy(alpha = if (isDark) 0.08f else 0.15f)
            )
        )
    }
    val highlightColor = remember(isDark) { Color.White.copy(alpha = if (isDark) 0.40f else 0.70f) }

    Box(
        modifier = modifier
            .bounceClick(scaleDown = 0.95f, onClick = onClick)
            .clayShadow(
                cornerRadius = cornerRadius,
                ambientShadowColor = Color.Black.copy(alpha = if (isDark) 0.35f else 0.12f),
                spotShadowColor = Color.Black.copy(alpha = if (isDark) 0.45f else 0.18f),
                blurRadius = 8.dp
            )
            .clip(pillShape)
            .background(brush = bgBrush, shape = pillShape)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = if (isDark) 0.28f else 0.45f),
                shape = pillShape
            )
            .drawBehind {
                drawRect(
                    color = highlightColor,
                    size = size.copy(height = 1.5.dp.toPx())
                )
            }
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            content = content
        )
    }
}


