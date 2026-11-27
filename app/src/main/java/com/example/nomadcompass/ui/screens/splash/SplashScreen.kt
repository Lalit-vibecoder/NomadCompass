package com.example.nomadcompass.ui.screens.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.nomadcompass.ui.theme.icons.GlassIconBadge
import com.example.nomadcompass.ui.theme.icons.PhosphorIcons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nomadcompass.R
import com.example.nomadcompass.ui.components.NomadLightLoadingBar
import com.example.nomadcompass.ui.theme.OnSurface
import com.example.nomadcompass.ui.theme.OnSurfaceVariant
import com.example.nomadcompass.ui.theme.SurfaceContainerLow

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onNavigateNext: (hasProfile: Boolean, isSecurityLocked: Boolean) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isComplete) {
        if (uiState.isComplete) {
            onNavigateNext(uiState.hasProfile, uiState.isSecurityLocked)
        }
    }

    // Subtle cinematic ambient zoom for the Canva splash visual
    val infiniteTransition = rememberInfiniteTransition(label = "splash_zoom")
    val zoomScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.035f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "splash_bg_scale"
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Full-bleed Canva Splash Screen Artwork
        Image(
            painter = painterResource(id = R.drawable.app_splash_canva),
            contentDescription = "Nomad Compass Splash",
            modifier = Modifier
                .fillMaxSize()
                .scale(zoomScale),
            contentScale = ContentScale.Crop
        )

        // Subtle gradient vignette at the bottom to ensure high contrast for loading bar and footer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.38f)
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x80000000),
                            Color(0xD9000000)
                        )
                    )
                )
        )

        // Bottom Loading & Status Controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Light Loading Bar
            val animatedProgress by animateFloatAsState(
                targetValue = uiState.progress / 100f,
                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
                label = "splash_progress"
            )

            NomadLightLoadingBar(
                progress = animatedProgress,
                width = 220.dp,
                height = 5.dp
            )

            // Progress text & percentage
            Row(
                modifier = Modifier.width(220.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = uiState.statusMessage,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFCAF0F8).copy(alpha = 0.90f)
                )
                Text(
                    text = "${uiState.progress}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFCAF0F8).copy(alpha = 0.90f)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Badges row
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassIconBadge(
                    icon = PhosphorIcons.WifiSlash,
                    contentDescription = "Offline Mode",
                    size = 34.dp,
                    iconSize = 17.dp,
                    tint = Color.White.copy(alpha = 0.85f)
                )
                GlassIconBadge(
                    icon = PhosphorIcons.GlobeSimple,
                    contentDescription = "Global Data",
                    size = 34.dp,
                    iconSize = 17.dp,
                    tint = Color.White.copy(alpha = 0.85f)
                )
                GlassIconBadge(
                    icon = PhosphorIcons.ShieldCheck,
                    contentDescription = "Secure",
                    size = 34.dp,
                    iconSize = 17.dp,
                    tint = Color.White.copy(alpha = 0.85f)
                )
            }

            Text(
                text = "V4.2.0 STABLE BUILD",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 3.sp,
                    fontSize = 10.5.sp
                ),
                color = Color(0xFFCAF0F8).copy(alpha = 0.55f)
            )
        }
    }
}
