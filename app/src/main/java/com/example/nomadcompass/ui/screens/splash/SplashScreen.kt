package com.example.nomadcompass.ui.screens.splash

import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.view.Surface
import android.view.TextureView
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.nomadcompass.R
import com.example.nomadcompass.ui.components.NomadLightLoadingBar

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

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 1. Poster fallback (prevents black flash while video initializes)
        Image(
            painter = painterResource(id = R.drawable.app_splash_canva),
            contentDescription = "Nomad Compass Splash",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 2. Canva Video Animation (Plays the animated 4-second motion with preserved aspect ratio)
        SplashVideoPlayer(
            modifier = Modifier.fillMaxSize(),
            videoRawResId = R.raw.app_splash_video
        )

        // 3. Ultra-minimal slim progress bar & status at bottom edge (leaves the Canva art & title 100% clean)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 22.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val animatedProgress by animateFloatAsState(
                targetValue = uiState.progress / 100f,
                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
                label = "splash_progress"
            )

            NomadLightLoadingBar(
                progress = animatedProgress,
                width = 200.dp,
                height = 3.5.dp
            )

            Row(
                modifier = Modifier.width(200.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = uiState.statusMessage,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = Color.White.copy(alpha = 0.85f)
                )
                Text(
                    text = "${uiState.progress}%",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
    }
}

/**
 * Seamless video animation player for the Canva splash motion sequence.
 * Uses TextureView + MediaPlayer with true Center-Crop matrix transform to ensure
 * zero stretching or distortion on any screen aspect ratio.
 */
@Composable
private fun SplashVideoPlayer(
    modifier: Modifier = Modifier,
    videoRawResId: Int = R.raw.app_splash_video
) {
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    AndroidView(
        factory = { ctx ->
            val textureView = TextureView(ctx)
            var vidW = 1080
            var vidH = 1920

            fun applyAspectCrop(viewW: Int, viewH: Int) {
                if (viewW <= 0 || viewH <= 0 || vidW <= 0 || vidH <= 0) return
                val viewRatio = viewW.toFloat() / viewH.toFloat()
                val videoRatio = vidW.toFloat() / vidH.toFloat()
                val scaleX: Float
                val scaleY: Float
                if (viewRatio > videoRatio) {
                    scaleX = 1f
                    scaleY = (viewW.toFloat() / vidW.toFloat()) / (viewH.toFloat() / vidH.toFloat())
                } else {
                    scaleX = (viewH.toFloat() / vidH.toFloat()) / (viewW.toFloat() / vidW.toFloat())
                    scaleY = 1f
                }
                val matrix = Matrix()
                matrix.setScale(scaleX, scaleY, viewW / 2f, viewH / 2f)
                textureView.setTransform(matrix)
            }

            textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                private var currentW = 0
                private var currentH = 0

                override fun onSurfaceTextureAvailable(surface: SurfaceTexture, w: Int, h: Int) {
                    currentW = w
                    currentH = h
                    applyAspectCrop(w, h)
                    try {
                        val player = MediaPlayer().apply {
                            val afd = ctx.resources.openRawResourceFd(videoRawResId)
                            setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                            afd.close()
                            setSurface(Surface(surface))
                            isLooping = true
                            setOnVideoSizeChangedListener { _, vw, vh ->
                                if (vw > 0 && vh > 0) {
                                    vidW = vw
                                    vidH = vh
                                    applyAspectCrop(currentW, currentH)
                                }
                            }
                            setOnPreparedListener { mp ->
                                mp.start()
                            }
                            prepareAsync()
                        }
                        mediaPlayer = player
                    } catch (e: Exception) {
                        // Silently fall back to underlying poster image
                    }
                }

                override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, w: Int, h: Int) {
                    currentW = w
                    currentH = h
                    applyAspectCrop(w, h)
                }

                override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                    mediaPlayer?.release()
                    mediaPlayer = null
                    return true
                }

                override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
            }

            textureView
        },
        modifier = modifier
    )
}
