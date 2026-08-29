package com.example.nomadcompass.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.nomadcompass.R
import java.io.File

/**
 * Global App Background with user-customizable wallpaper and real-time blur effect.
 * Defaults to the iconic Ferris Wheel night photograph with customizable blurriness.
 */
@Composable
fun AppBackground(
    bgPhotoUri: String? = null,
    blurRadius: Float = 24f,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val bgFile = remember(bgPhotoUri) {
        if (!bgPhotoUri.isNullOrBlank()) File(bgPhotoUri) else null
    }
    val effectiveBlur = blurRadius.coerceIn(0f, 60f)

    val gradientBrush = remember {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF091612).copy(alpha = 0.55f),
                Color(0xFF0E1E19).copy(alpha = 0.65f),
                Color(0xFF07120E).copy(alpha = 0.75f)
            )
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Blurred Background Wallpaper with GPU layer caching
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    clip = true
                }
        ) {
            val imageModifier = Modifier
                .fillMaxSize()
                .then(
                    if (effectiveBlur > 0f) Modifier.blur(effectiveBlur.dp)
                    else Modifier
                )

            if (bgFile != null && bgFile.exists()) {
                AsyncImage(
                    model = bgFile,
                    contentDescription = "App Background",
                    contentScale = ContentScale.Crop,
                    modifier = imageModifier
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.app_default_bg),
                    contentDescription = "Default App Background",
                    contentScale = ContentScale.Crop,
                    modifier = imageModifier
                )
            }

            // Deep emerald & slate darkening scrim overlay for contrast & readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(brush = gradientBrush)
            )
        }

        // Foreground App Screen Content
        content()
    }
}
