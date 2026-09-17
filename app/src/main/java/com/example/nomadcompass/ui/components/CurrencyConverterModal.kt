package com.example.nomadcompass.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import com.example.nomadcompass.ui.theme.icons.PhosphorIcons
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.nomadcompass.ui.theme.Background
import com.example.nomadcompass.ui.theme.OnPrimary
import com.example.nomadcompass.ui.theme.OnSurface
import com.example.nomadcompass.ui.theme.OnSurfaceVariant
import com.example.nomadcompass.ui.theme.Primary
import com.example.nomadcompass.ui.theme.PrimaryContainer
import com.example.nomadcompass.ui.theme.SafetyGreen
import com.example.nomadcompass.ui.theme.SecondaryContainer
import com.example.nomadcompass.ui.theme.SurfaceContainer
import com.example.nomadcompass.ui.theme.SurfaceContainerLow
import java.util.Locale

@Composable
fun CurrencyConverterModal(
    baseCurrency: String,
    targetCurrency: String,
    exchangeRate: Double,
    inputAmount: String,
    convertedAmount: Double,
    isSwapped: Boolean,
    isRefreshing: Boolean = false,
    refreshMessage: String? = null,
    onRefreshRate: () -> Unit = {},
    onInputAmountChanged: (String) -> Unit,
    onToggleSwap: () -> Unit,
    onDismiss: () -> Unit,
) {
    val fromCurrency = if (isSwapped) targetCurrency else baseCurrency
    val toCurrency = if (isSwapped) baseCurrency else targetCurrency
    val effectiveRate = if (isSwapped && exchangeRate > 0) 1.0 / exchangeRate else exchangeRate

    val swapRotation by animateFloatAsState(
        targetValue = if (isSwapped) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "swap_button_rotation"
    )

    FrostedGlassDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .padding(16.dp),
    ) {
        Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Live rate update popup banner (Top of popup)
                AnimatedVisibility(visible = refreshMessage != null) {
                    refreshMessage?.let { msg ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SafetyGreen.copy(alpha = 0.15f))
                                .border(1.dp, SafetyGreen.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = PhosphorIcons.CheckCircle,
                                    contentDescription = null,
                                    tint = SafetyGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = msg,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = OnSurface,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = PhosphorIcons.CurrencyCircleDollar,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "Currency Converter",
                            style = MaterialTheme.typography.titleLarge,
                            color = OnSurface,
                            fontWeight = FontWeight.Bold
                        )
                        // Refresh Rate button
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(PrimaryContainer.copy(alpha = 0.5f))
                                .border(1.dp, Primary.copy(alpha = 0.3f), CircleShape)
                                .clickable(enabled = !isRefreshing) { onRefreshRate() },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isRefreshing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = Primary
                                )
                            } else {
                                Icon(
                                    imageVector = PhosphorIcons.ArrowsClockwise,
                                    contentDescription = "Refresh Rate",
                                    tint = Primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = PhosphorIcons.X,
                            contentDescription = "Close",
                            tint = OnSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Rate summary pill
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .background(SurfaceContainerLow)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                        .padding(vertical = 10.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val formattedRateStr = String.format(Locale.US, "%.4f", effectiveRate)
                    Text(
                        text = "1 $fromCurrency = $formattedRateStr $toCurrency",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Primary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Input Box (From Currency)
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "YOU PAY ($fromCurrency)",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = inputAmount,
                        onValueChange = { newValue ->
                            if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                                onInputAmountChanged(newValue)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            focusedContainerColor = SurfaceContainerLow,
                            unfocusedContainerColor = SurfaceContainerLow,
                            focusedTextColor = OnSurface,
                            unfocusedTextColor = OnSurface,
                            cursorColor = Primary,
                        ),
                        shape = RoundedCornerShape(16.dp),
                        trailingIcon = {
                            Text(
                                text = fromCurrency,
                                style = MaterialTheme.typography.titleMedium,
                                color = Primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Preset Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val presets = listOf("10", "50", "100", "500")
                    presets.forEach { preset ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(CircleShape)
                                .background(com.example.nomadcompass.ui.theme.PillInactiveBackground)
                                .border(1.dp, com.example.nomadcompass.ui.theme.PillInactiveBorder, CircleShape)
                                .clickable { onInputAmountChanged(preset) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+$preset",
                                style = MaterialTheme.typography.labelSmall,
                                color = com.example.nomadcompass.ui.theme.PillInactiveText,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Swap Button
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clayShadow(
                            cornerRadius = 9999.dp,
                            ambientShadowColor = Color.Black.copy(alpha = 0.50f),
                            spotShadowColor = Color.Black.copy(alpha = 0.70f),
                            blurRadius = 8.dp
                        )
                        .clip(CircleShape)
                        .background(PrimaryContainer)
                        .clickable { onToggleSwap() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = PhosphorIcons.ArrowsDownUp,
                        contentDescription = "Swap currencies",
                        tint = com.example.nomadcompass.ui.theme.OnPrimaryContainer,
                        modifier = Modifier
                            .size(24.dp)
                            .graphicsLayer { rotationZ = swapRotation }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Output Display (To Currency)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceContainerLow)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "YOU RECEIVE ($toCurrency)",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val formattedResult = String.format(Locale.US, "%,.2f", convertedAmount)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AnimatedContent(
                            targetState = formattedResult,
                            transitionSpec = {
                                (slideInVertically { height -> height / 3 } + fadeIn(animationSpec = tween(150))) togetherWith
                                    (slideOutVertically { height -> -height / 3 } + fadeOut(animationSpec = tween(150)))
                            },
                            label = "converter_result_anim"
                        ) { resultText ->
                            Text(
                                text = resultText,
                                style = MaterialTheme.typography.headlineMedium,
                                color = Primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = toCurrency,
                            style = MaterialTheme.typography.titleMedium,
                            color = OnSurface,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Done Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .background(PrimaryContainer)
                        .clickable { onDismiss() }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "DONE",
                        style = MaterialTheme.typography.labelLarge,
                        color = OnPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
    }
}
