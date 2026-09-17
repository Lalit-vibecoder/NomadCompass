package com.example.nomadcompass.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nomadcompass.R
import com.example.nomadcompass.ui.theme.OnSurface
import com.example.nomadcompass.ui.theme.OnSurfaceVariant

/**
 * Luminous light loading bar with support for both indeterminate
 * shimmer/beam animation and determinate progress filling.
 */
@Composable
fun NomadLightLoadingBar(
    modifier: Modifier = Modifier,
    progress: Float? = null,
    width: Dp = 200.dp,
    height: Dp = 5.dp,
    trackColor: Color = Color.White.copy(alpha = 0.18f),
    barColor: Color = Color.White,
) {
    val shape = RoundedCornerShape(9999.dp)

    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(shape)
            .background(trackColor)
            .border(0.75.dp, Color.White.copy(alpha = 0.22f), shape)
    ) {
        if (progress != null) {
            // Determinate progress mode
            val animatedProgress by animateFloatAsState(
                targetValue = progress.coerceIn(0f, 1f),
                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
                label = "determinate_light_progress"
            )

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .clip(shape)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFFE2E8F0),
                                Color(0xFFFFFFFF),
                                Color(0xFFF1F5F9)
                            )
                        )
                    )
            )
        } else {
            // Indeterminate luminous sweep beam
            val infiniteTransition = rememberInfiniteTransition(label = "light_bar_sweep")
            val sweepPhase by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "sweep_phase"
            )

            Canvas(modifier = Modifier.fillMaxSize()) {
                val totalWidth = size.width
                val totalHeight = size.height
                val beamWidth = totalWidth * 0.38f
                val startX = (totalWidth + beamWidth * 2) * sweepPhase - beamWidth
                val endX = startX + beamWidth

                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.10f),
                            Color.White,
                            Color.White.copy(alpha = 0.10f)
                        ),
                        startX = startX,
                        endX = endX
                    ),
                    topLeft = Offset(startX, 0f),
                    size = Size(beamWidth, totalHeight),
                    cornerRadius = CornerRadius(totalHeight / 2, totalHeight / 2)
                )
            }
        }
    }
}

/**
 * Custom animated loading view for NomadCompass.
 * Displays the Nomad Compass brand logo with a light loading bar below it,
 * breathing status typography, and branded subtitle.
 */
@Composable
fun NomadLoadingAnimation(
    modifier: Modifier = Modifier,
    message: String = "Locating destination...",
    progress: Float? = null,
    logoSize: Dp = 190.dp,
    barWidth: Dp = 210.dp,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "nomad_loading")

    // Subtle breathing pulse scale animation (non-rotating)
    val scalePulse by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Text alpha pulse animation
    val alphaPulse by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Brand Logo (Unclipped and enlarged, without rotation)
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "Nomad Compass Logo",
                modifier = Modifier
                    .size(logoSize)
                    .scale(scalePulse)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Light Loading Bar below the logo
            NomadLightLoadingBar(
                progress = progress,
                width = barWidth,
                height = 5.dp
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                color = OnSurface,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.alpha(alphaPulse)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "NOMAD COMPASS",
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant.copy(alpha = 0.65f),
                letterSpacing = 2.sp
            )
        }
    }
}
