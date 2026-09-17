package com.example.nomadcompass.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Adds a physical spring bounce / scale-down touch effect on press.
 * Runs completely on the GPU render layer via graphicsLayer, avoiding unnecessary recompositions or layout passes.
 */
fun Modifier.bounceClick(
    scaleDown: Float = 0.95f,
    onClick: (() -> Unit)? = null,
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleDown else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "bounce_scale"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .then(
            if (onClick != null) {
                Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
            } else {
                Modifier
            }
        )
}

/**
 * Adds tactile spring press scaling using an external interaction source.
 */
fun Modifier.pressScale(
    interactionSource: MutableInteractionSource,
    scaleDown: Float = 0.96f,
): Modifier = composed {
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleDown else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "press_scale"
    )

    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/**
 * Animated bounce pop triggered when a boolean state toggles to true (e.g. Favorite heart, Checkbox).
 */
fun Modifier.bounceOnState(
    state: Boolean,
    maxScale: Float = 1.3f,
): Modifier = composed {
    val animScale = remember { Animatable(1.0f) }

    LaunchedEffect(state) {
        if (state) {
            animScale.animateTo(
                targetValue = maxScale,
                animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing)
            )
            animScale.animateTo(
                targetValue = 1.0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        } else {
            animScale.snapTo(1.0f)
        }
    }

    this.graphicsLayer {
        scaleX = animScale.value
        scaleY = animScale.value
    }
}
