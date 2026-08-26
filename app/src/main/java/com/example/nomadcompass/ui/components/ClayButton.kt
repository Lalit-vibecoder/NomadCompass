package com.example.nomadcompass.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.nomadcompass.ui.theme.LocalThemeController
import com.example.nomadcompass.ui.theme.OnPrimaryContainer
import com.example.nomadcompass.ui.theme.Primary
import com.example.nomadcompass.ui.theme.PrimaryContainer

/**
 * A Claymorphism-styled primary button with 360-degree omni-directional pop-out shadows.
 */
@Composable
fun ClayButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 18.dp),
    content: @Composable RowScope.() -> Unit,
) {
    val isDark = LocalThemeController.current.isDarkMode
    val interactionSource = remember { MutableInteractionSource() }

    val ambientShadow = if (isDark) Color.Black.copy(alpha = 0.65f) else Primary.copy(alpha = 0.40f)
    val spotShadow = if (isDark) Color.Black.copy(alpha = 0.85f) else Color(0x350F172A)

    Button(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = modifier
            .pressScale(interactionSource, scaleDown = 0.96f)
            .clayShadow(
                cornerRadius = 9999.dp,
                ambientShadowColor = ambientShadow,
                spotShadowColor = spotShadow,
                blurRadius = if (isDark) 14.dp else 12.dp,
            ),
        enabled = enabled,
        shape = RoundedCornerShape(9999.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryContainer,
            contentColor = OnPrimaryContainer,
            disabledContainerColor = PrimaryContainer.copy(alpha = 0.5f),
            disabledContentColor = OnPrimaryContainer.copy(alpha = 0.5f),
        ),
        contentPadding = contentPadding,
        content = content,
    )
}
