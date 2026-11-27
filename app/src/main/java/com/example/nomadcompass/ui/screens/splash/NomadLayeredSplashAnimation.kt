package com.example.nomadcompass.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.nomadcompass.R

/**
 * Layered Splash Screen Animation for NomadCompass.
 *
 * Stacks decoupled graphic layers (Bezel, Needle, Center Pivot) exported from Canva
 * (or default vector assets) and animates them using realistic magnetic spring physics,
 * continuous bezel rotation, and ambient radial glow.
 *
 * Layers used:
 * - [R.drawable.ic_splash_bezel]: Outer compass dial / degree marks (slow rotation).
 * - [R.drawable.ic_splash_needle]: Dual-toned magnetic needle (damped spring oscillation).
 * - [R.drawable.ic_splash_pivot]: Center pin / Nomad monogram (breathing pulse + glow).
 */
@Composable
fun NomadLayeredSplashAnimation(
    modifier: Modifier = Modifier,
    progress: Float = 0f,
    isComplete: Boolean = false,
    compassSize: Dp = 230.dp,
) {
    // 1. Continuous Bezel Rotation
    val infiniteTransition = rememberInfiniteTransition(label = "splash_bezel_transition")
    val bezelRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 28000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bezel_continuous_rotation"
    )

    // 2. Ambient Pulse Glow behind the compass
    val ambientPulse by infiniteTransition.animateFloat(
        initialValue = 0.82f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ambient_glow_pulse"
    )

    // 3. Center Pivot Breathing Scale
    val pivotScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pivot_breathing_scale"
    )

    // 4. Magnetic Needle Physics:
    // Starts searching (-45° to +40°), oscillates magnetically, and smoothly locks to True North (0°) on completion.
    val needleTargetAngle = remember(progress, isComplete) {
        when {
            isComplete -> 0f
            progress < 0.25f -> -36f + (progress * 80f)
            progress < 0.60f -> 28f - ((progress - 0.25f) * 110f)
            progress < 0.85f -> -12f + ((progress - 0.60f) * 48f)
            else -> 4f - ((progress - 0.85f) * 26f)
        }
    }

    val animatedNeedleRotation by animateFloatAsState(
        targetValue = needleTargetAngle,
        animationSpec = spring(
            dampingRatio = if (isComplete) Spring.DampingRatioMediumBouncy else 0.65f,
            stiffness = if (isComplete) Spring.StiffnessLow else Spring.StiffnessMediumLow
        ),
        label = "needle_magnetic_rotation"
    )

    // Overall entry scale animation
    val entryScale = remember { Animatable(0.85f) }
    LaunchedEffect(Unit) {
        entryScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMediumLow)
        )
    }

    Box(
        modifier = modifier
            .size(compassSize)
            .scale(entryScale.value)
            .drawBehind {
                val auraCenter = center
                val auraRadius = (size.width / 2f) * ambientPulse

                // Ambient Radial Glow (Cyan to Emerald signature halo)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x2E06B6D4), // Cyan highlight
                            Color(0x1810B981), // Emerald glow
                            Color.Transparent
                        ),
                        center = auraCenter,
                        radius = auraRadius
                    ),
                    radius = auraRadius,
                    center = auraCenter
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // LAYER 1: Outer Bezel Dial (Rotates slowly)
        Image(
            painter = painterResource(id = R.drawable.ic_splash_bezel),
            contentDescription = "Compass Dial Bezel",
            modifier = Modifier
                .fillMaxSize()
                .rotate(bezelRotation)
        )

        // LAYER 2: Magnetic Needle (Oscillates and snaps to North)
        Image(
            painter = painterResource(id = R.drawable.ic_splash_needle),
            contentDescription = "Compass Needle",
            modifier = Modifier
                .fillMaxSize()
                .rotate(animatedNeedleRotation)
        )

        // LAYER 3: Center Pivot & Nomad Pin (Pulsing highlight)
        Image(
            painter = painterResource(id = R.drawable.ic_splash_pivot),
            contentDescription = "Compass Center Pivot",
            modifier = Modifier
                .fillMaxSize()
                .scale(pivotScale)
        )
    }
}
