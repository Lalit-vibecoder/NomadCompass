package com.example.nomadcompass.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.nomadcompass.R

/**
 * Paperplane vector loading overlay.
 * Renders the animated Paperplane on the exact same background as other screens.
 */
@Composable
fun FishLoadingOverlay(
    modifier: Modifier = Modifier,
    fishCount: Int = 0,
    animationSize: Dp = 320.dp,
    content: @Composable () -> Unit = {},
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.paperplane_loading)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(animationSize)
        )
        content()
    }
}

/**
 * Compact Paperplane vector loading overlay for modals and dialogs.
 */
@Composable
fun FishLoadingOverlayCompact(
    modifier: Modifier = Modifier,
    fishCount: Int = 0,
    animationSize: Dp = 160.dp,
    content: @Composable () -> Unit = {},
) {
    FishLoadingOverlay(
        modifier = modifier,
        animationSize = animationSize,
        content = content,
    )
}
