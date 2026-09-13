package com.example.nomadcompass.ui.screens.lock

import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.nomadcompass.ui.theme.icons.PhosphorIcons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import coil3.compose.AsyncImage
import com.example.nomadcompass.ui.components.ClayButton
import com.example.nomadcompass.ui.components.bounceClick
import com.example.nomadcompass.ui.components.clayShadow
import com.example.nomadcompass.ui.theme.Background
import com.example.nomadcompass.ui.theme.Error
import com.example.nomadcompass.ui.theme.OnPrimaryContainer
import com.example.nomadcompass.ui.theme.OnSurface
import com.example.nomadcompass.ui.theme.OnSurfaceVariant
import com.example.nomadcompass.ui.theme.OutlineVariant
import com.example.nomadcompass.ui.theme.Primary
import com.example.nomadcompass.ui.theme.PrimaryContainer
import com.example.nomadcompass.ui.theme.Secondary
import com.example.nomadcompass.ui.theme.SecondaryContainer
import com.example.nomadcompass.ui.theme.SurfaceContainer
import com.example.nomadcompass.ui.theme.SurfaceContainerHigh
import com.example.nomadcompass.ui.theme.SurfaceContainerLow
import com.example.nomadcompass.util.BiometricHelper
import java.io.File
import kotlin.math.roundToInt

@Composable
fun AppLockScreen(
    viewModel: AppLockViewModel,
    onUnlocked: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context as? FragmentActivity

    LaunchedEffect(uiState.isUnlocked) {
        if (uiState.isUnlocked) {
            onUnlocked()
        }
    }

    val isBioMode = uiState.profile?.isBiometricEnabled == true

    // Auto-prompt biometric on screen launch if enabled
    LaunchedEffect(uiState.profile) {
        val profile = uiState.profile
        if (profile != null && profile.isBiometricEnabled && activity != null) {
            if (BiometricHelper.isBiometricAvailable(context)) {
                BiometricHelper.showBiometricPrompt(
                    activity = activity,
                    onSuccess = { viewModel.onBiometricSuccess() },
                    onError = { viewModel.onBiometricError(it) }
                )
            }
        }
    }

    // Shake animation on wrong PIN
    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(uiState.shakeTrigger) {
        if (uiState.shakeTrigger > 0) {
            for (i in 0..2) {
                shakeOffset.animateTo(24f, tween(50))
                shakeOffset.animateTo(-24f, tween(50))
            }
            shakeOffset.animateTo(0f, tween(50))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .statusBarsPadding()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // User Avatar Photo or Lock Icon
            val photoUri = uiState.profile?.photoUri
            val photoFile = if (!photoUri.isNullOrBlank()) File(photoUri) else null

            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clayShadow(
                        cornerRadius = 9999.dp,
                        ambientShadowColor = Primary.copy(alpha = 0.35f),
                        spotShadowColor = Primary.copy(alpha = 0.45f),
                        blurRadius = 12.dp
                    )
                    .clip(CircleShape)
                    .background(SecondaryContainer)
                    .border(2.dp, Primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (photoFile != null && photoFile.exists()) {
                    AsyncImage(
                        model = photoFile,
                        contentDescription = "Profile Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = if (isBioMode) PhosphorIcons.Fingerprint else PhosphorIcons.LockKey,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val userName = uiState.profile?.userName?.ifBlank { "Nomad" } ?: "Nomad"
            Text(
                text = "Welcome back, $userName",
                style = MaterialTheme.typography.headlineMedium,
                color = OnSurface,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            if (isBioMode) {
                // ── BIOMETRIC ONLY MODE ──
                Text(
                    text = "Touch the fingerprint sensor or use Face ID to unlock",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Pulsing glowing fingerprint button
                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                val pulseScale by infiniteTransition.animateColor(
                    initialValue = Secondary.copy(alpha = 0.3f),
                    targetValue = Secondary.copy(alpha = 0.05f),
                    animationSpec = infiniteRepeatable(
                        animation = tween(1200, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "pulse_color"
                )

                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clayShadow(
                            cornerRadius = 9999.dp,
                            ambientShadowColor = Secondary.copy(alpha = 0.4f),
                            spotShadowColor = Secondary.copy(alpha = 0.5f),
                            blurRadius = 16.dp
                        )
                        .clip(CircleShape)
                        .background(pulseScale)
                        .border(2.dp, Secondary, CircleShape)
                        .bounceClick {
                            if (activity != null) {
                                BiometricHelper.showBiometricPrompt(
                                    activity = activity,
                                    onSuccess = { viewModel.onBiometricSuccess() },
                                    onError = { viewModel.onBiometricError(it) }
                                )
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = PhosphorIcons.Fingerprint,
                        contentDescription = "Authenticate",
                        tint = Secondary,
                        modifier = Modifier.size(56.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                ClayButton(
                    onClick = {
                        if (activity != null) {
                            BiometricHelper.showBiometricPrompt(
                                activity = activity,
                                onSuccess = { viewModel.onBiometricSuccess() },
                                onError = { viewModel.onBiometricError(it) }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = PhosphorIcons.Fingerprint, contentDescription = null, tint = OnPrimaryContainer)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Authenticate with Biometrics",
                            color = OnPrimaryContainer,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (uiState.errorMessage != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = uiState.errorMessage ?: "",
                        style = MaterialTheme.typography.labelMedium,
                        color = Error,
                        fontWeight = FontWeight.SemiBold
                    )
                }

            } else {
                // ── 4-DIGIT PIN ONLY MODE ──
                Text(
                    text = "Enter your 4-digit security PIN",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )

                Spacer(modifier = Modifier.height(28.dp))

                // 4-Dot PIN Indicator with Shake Effect
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
                ) {
                    for (i in 0 until 4) {
                        val isFilled = i < uiState.pinInput.length
                        val dotColor by animateColorAsState(
                            targetValue = if (isFilled) Primary else SurfaceContainerHigh,
                            animationSpec = tween(150),
                            label = "pin_dot_$i"
                        )

                        val dotBorder by animateColorAsState(
                            targetValue = if (isFilled) Primary else OutlineVariant,
                            animationSpec = tween(150),
                            label = "pin_border_$i"
                        )

                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clayShadow(
                                    cornerRadius = 9999.dp,
                                    ambientShadowColor = if (isFilled) Primary.copy(alpha = 0.4f) else Color.Transparent,
                                    spotShadowColor = if (isFilled) Primary.copy(alpha = 0.5f) else Color.Transparent,
                                    blurRadius = 6.dp
                                )
                                .clip(CircleShape)
                                .background(dotColor)
                                .border(1.5.dp, dotBorder, CircleShape)
                        )
                    }
                }

                // Error Message
                if (uiState.errorMessage != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = uiState.errorMessage ?: "",
                        style = MaterialTheme.typography.labelMedium,
                        color = Error,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    Spacer(modifier = Modifier.height(26.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Numeric Keypad (1-9, Empty, 0, Backspace) - No Biometrics shown here!
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val rows = listOf(
                        listOf("1", "2", "3"),
                        listOf("4", "5", "6"),
                        listOf("7", "8", "9"),
                        listOf("EMPTY", "0", "DEL")
                    )

                    for (row in rows) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (key in row) {
                                when (key) {
                                    "EMPTY" -> {
                                        Spacer(modifier = Modifier.size(72.dp))
                                    }
                                    "DEL" -> {
                                        Box(
                                            modifier = Modifier
                                                .size(72.dp)
                                                .clayShadow(
                                                    cornerRadius = 9999.dp,
                                                    ambientShadowColor = Color.Black.copy(alpha = 0.25f),
                                                    spotShadowColor = Color.Black.copy(alpha = 0.35f),
                                                    blurRadius = 8.dp
                                                )
                                                .clip(CircleShape)
                                                .background(SurfaceContainerLow)
                                                .border(1.dp, OutlineVariant, CircleShape)
                                                .bounceClick { viewModel.onDeleteDigit() },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = PhosphorIcons.Backspace,
                                                contentDescription = "Delete Digit",
                                                tint = OnSurface,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                    else -> {
                                        Box(
                                            modifier = Modifier
                                                .size(72.dp)
                                                .clayShadow(
                                                    cornerRadius = 9999.dp,
                                                    ambientShadowColor = Color.Black.copy(alpha = 0.25f),
                                                    spotShadowColor = Color.Black.copy(alpha = 0.35f),
                                                    blurRadius = 8.dp
                                                )
                                                .clip(CircleShape)
                                                .background(SurfaceContainer)
                                                .border(1.dp, OutlineVariant, CircleShape)
                                                .bounceClick { viewModel.onDigitEntered(key) },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = key,
                                                style = MaterialTheme.typography.headlineMedium,
                                                color = OnSurface,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
