package com.example.nomadcompass.ui.screens.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nomadcompass.R
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

    val infiniteTransition = rememberInfiniteTransition(label = "compass")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {

        // Center Content
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Rotating 3D Clay Compass Logo
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "Nomad Compass Logo",
                modifier = Modifier
                    .size(200.dp)
                    .rotate(rotation)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // App Title
            Text(
                text = "NOMAD COMPASS",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                ),
                color = OnSurface,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Progress Bar (Claymorphism segment)
            val animatedProgress by androidx.compose.animation.core.animateFloatAsState(
                targetValue = uiState.progress / 100f,
                animationSpec = androidx.compose.animation.core.tween(durationMillis = 350, easing = androidx.compose.animation.core.FastOutSlowInEasing),
                label = "splash_progress"
            )

            Box(
                modifier = Modifier
                    .width(220.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(9999.dp))
                    .background(Color(0xFF353439))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .fillMaxWidth(animatedProgress)
                        .clip(RoundedCornerShape(9999.dp))
                        .background(Color(0xFFCCC6BC))
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress text & percentage
            Row(
                modifier = Modifier.width(220.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = uiState.statusMessage,
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant.copy(alpha = 0.6f)
                )
                Text(
                    text = "${uiState.progress}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }

        // Atmospheric Footer
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassIconBadge(
                    icon = PhosphorIcons.WifiSlash,
                    contentDescription = "Offline Mode",
                    size = 36.dp,
                    iconSize = 18.dp,
                    tint = Color.White.copy(alpha = 0.70f)
                )
                GlassIconBadge(
                    icon = PhosphorIcons.GlobeSimple,
                    contentDescription = "Global Data",
                    size = 36.dp,
                    iconSize = 18.dp,
                    tint = Color.White.copy(alpha = 0.70f)
                )
                GlassIconBadge(
                    icon = PhosphorIcons.ShieldCheck,
                    contentDescription = "Secure",
                    size = 36.dp,
                    iconSize = 18.dp,
                    tint = Color.White.copy(alpha = 0.70f)
                )
            }

            Text(
                text = "V4.2.0 STABLE BUILD",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 3.sp,
                    fontSize = 11.sp
                ),
                color = OnSurfaceVariant.copy(alpha = 0.25f)
            )
        }
    }
}
