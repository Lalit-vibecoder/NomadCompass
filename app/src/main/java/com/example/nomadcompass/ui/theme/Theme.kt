package com.example.nomadcompass.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

class ThemeController {
    val isDarkMode: Boolean = true
}

val LocalThemeController = staticCompositionLocalOf { ThemeController() }

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    surfaceContainer = SurfaceContainerDark,
    surfaceContainerHigh = SurfaceContainerHighDark,
    surfaceContainerHighest = SurfaceContainerHighestDark,
    surfaceContainerLow = SurfaceContainerLowDark,
    surfaceContainerLowest = SurfaceContainerLowestDark,
    surfaceBright = SurfaceBrightDark,
    error = Error,
    onError = OnError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,
    outline = OutlineDark,
    outlineVariant = OutlineVariantDark,
)

// Dynamic Compose Theme Helper Properties
val Background @Composable get() = MaterialTheme.colorScheme.background
val Surface @Composable get() = MaterialTheme.colorScheme.surface
val SurfaceContainer @Composable get() = MaterialTheme.colorScheme.surfaceContainer
val SurfaceContainerHigh @Composable get() = MaterialTheme.colorScheme.surfaceContainerHigh
val SurfaceContainerLow @Composable get() = MaterialTheme.colorScheme.surfaceContainerLow
val SurfaceContainerHighest @Composable get() = MaterialTheme.colorScheme.surfaceContainerHighest
val SurfaceContainerLowest @Composable get() = MaterialTheme.colorScheme.surfaceContainerLowest
val SurfaceBright @Composable get() = MaterialTheme.colorScheme.surfaceBright
val OnSurface @Composable get() = MaterialTheme.colorScheme.onSurface
val OnSurfaceVariant @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant
val OnBackground @Composable get() = MaterialTheme.colorScheme.onBackground
val Primary @Composable get() = MaterialTheme.colorScheme.primary
val OnPrimary @Composable get() = MaterialTheme.colorScheme.onPrimary
val PrimaryContainer @Composable get() = MaterialTheme.colorScheme.primaryContainer
val OnPrimaryContainer @Composable get() = MaterialTheme.colorScheme.onPrimaryContainer
val Secondary @Composable get() = MaterialTheme.colorScheme.secondary
val OnSecondary @Composable get() = MaterialTheme.colorScheme.onSecondary
val SecondaryContainer @Composable get() = MaterialTheme.colorScheme.secondaryContainer
val OnSecondaryContainer @Composable get() = MaterialTheme.colorScheme.onSecondaryContainer
val Outline @Composable get() = MaterialTheme.colorScheme.outline
val OutlineVariant @Composable get() = MaterialTheme.colorScheme.outlineVariant
val Accent @Composable get() = AccentAmber
val SageCard @Composable get() = SageCardDark
val SageCardSurface @Composable get() = SageCardSurfaceDark


@Composable
fun NomadCompassTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalThemeController provides ThemeController()) {
        MaterialTheme(
            colorScheme = DarkColorScheme,
            typography = NomadTypography,
            content = content,
        )
    }
}
