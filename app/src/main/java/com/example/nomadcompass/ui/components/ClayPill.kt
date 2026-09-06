package com.example.nomadcompass.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nomadcompass.ui.theme.PillActiveBackground
import com.example.nomadcompass.ui.theme.PillActiveText
import com.example.nomadcompass.ui.theme.PillInactiveBackground
import com.example.nomadcompass.ui.theme.PillInactiveBorder
import com.example.nomadcompass.ui.theme.PillInactiveText

/**
 * Fast, responsive tactile pill/capsule filter button matching the porcelain & charcoal aesthetic.
 */
@Composable
fun ClayPill(
    text: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(9999.dp)
    val interactionSource = remember { MutableInteractionSource() }

    // Fast 120ms color transition for crisp responsiveness
    val animatedBgColor by animateColorAsState(
        targetValue = if (isActive) PillActiveBackground else PillInactiveBackground,
        animationSpec = tween(durationMillis = 120),
        label = "pill_bg_color"
    )
    val animatedTextColor by animateColorAsState(
        targetValue = if (isActive) PillActiveText else PillInactiveText,
        animationSpec = tween(durationMillis = 120),
        label = "pill_text_color"
    )
    val animatedBorderColor by animateColorAsState(
        targetValue = if (isActive) Color.Transparent else PillInactiveBorder,
        animationSpec = tween(durationMillis = 120),
        label = "pill_border_color"
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(animatedBgColor, shape)
            .border(1.dp, animatedBorderColor, shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 22.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 14.sp,
                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Medium
            ),
            color = animatedTextColor,
        )
    }
}

