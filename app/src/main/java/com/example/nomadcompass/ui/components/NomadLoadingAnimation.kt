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
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

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
 * Animated Paperplane Loading Screen.
 * Renders the animated Paperplane vector loading screen centered
 * on the exact same background as the other pages of the app.
 */
@Composable
fun NomadLoadingAnimation(
    modifier: Modifier = Modifier,
    message: String? = null,
    progress: Float? = null,
    logoSize: Dp = 190.dp,
    barWidth: Dp = 210.dp,
    animationSize: Dp = 320.dp,
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.paperplane_loading)
    )
    val lottieProgress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LottieAnimation(
                composition = composition,
                progress = { lottieProgress },
                modifier = Modifier.size(animationSize)
            )
            if (!message.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.titleSmall,
                    color = OnSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
