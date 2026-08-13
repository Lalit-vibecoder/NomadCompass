package com.example.nomadcompass.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.nomadcompass.ui.theme.OnPrimary
import com.example.nomadcompass.ui.theme.PrimaryContainer

/**
 * A Claymorphism-styled primary button.
 *
 * Replicates the `.clay-button` from Stitch mockups:
 * - Light pastel background (#e8e2d8 / Primary Container)
 * - Outer shadow for depth
 * - Inset top highlight for the "puffy" look
 */
@Composable
fun ClayButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 18.dp),
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .clayShadow(
                cornerRadius = 9999.dp,
                shadowColor = Color.Black.copy(alpha = 0.3f),
                blurRadius = 15.dp,
                offsetX = 0.dp,
                offsetY = 8.dp,
            ),
        enabled = enabled,
        shape = RoundedCornerShape(9999.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryContainer,
            contentColor = OnPrimary,
            disabledContainerColor = PrimaryContainer.copy(alpha = 0.5f),
            disabledContentColor = OnPrimary.copy(alpha = 0.5f),
        ),
        contentPadding = contentPadding,
        content = content,
    )
}
