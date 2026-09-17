package com.example.nomadcompass.ui.theme

import androidx.compose.ui.graphics.Color

// ── Midnight & Aurora Color System ──

// Primary Action & Accent Tokens
val ActionPrimary = Color(0xFFFF2E63) // #FF2E63 for primary action buttons (Save, Add Expense)
val OnActionPrimary = Color(0xFFFFFFFF)

val AuroraCyan = Color(0xFF00ADB5) // #00ADB5 for navigation icons, active toggles, selected tab indicators, blur slider
val OnAuroraCyan = Color(0xFFFFFFFF)

// Standardized Glassmorphism Card Tokens: rgba(18, 18, 18, 0.5) + rgba(255, 255, 255, 0.1) border
val GlassCardBackground = Color(0x80121212) // rgba(18, 18, 18, 0.5)
val GlassCardBorder = Color(0x1AFFFFFF) // rgba(255, 255, 255, 0.1)

// 🌙 Dark Theme: Midnight Slate Base
val BackgroundDark = Color(0xFF121212)
val SurfaceDark = Color(0x80121212)
val SurfaceContainerLowestDark = Color(0xFF0D0D0D)
val SurfaceContainerLowDark = Color(0x66181818)
val SurfaceContainerDark = Color(0x80121212)
val SurfaceContainerHighDark = Color(0x99181818)
val SurfaceContainerHighestDark = Color(0xCC202020)
val SurfaceBrightDark = Color(0xFF2A2A2A)

val PrimaryDark = AuroraCyan // #00ADB5
val OnPrimaryDark = Color(0xFFFFFFFF)
val PrimaryContainerDark = ActionPrimary // #FF2E63
val OnPrimaryContainerDark = Color(0xFFFFFFFF)

// ── Tactile Pill & Capsule Button Color Tokens ──
val PillActiveBackground = AuroraCyan // #00ADB5
val PillActiveText = Color(0xFFFFFFFF)
val PillInactiveBackground = Color(0x2EFFFFFF)
val PillInactiveText = Color(0xFFB3B3B3) // #B3B3B3 Secondary Text
val PillInactiveBorder = Color(0x1AFFFFFF) // rgba(255, 255, 255, 0.1)

val SecondaryDark = AuroraCyan
val OnSecondaryDark = Color(0xFF121212)
val SecondaryContainerDark = Color(0x3300ADB5)
val OnSecondaryContainerDark = Color(0xFFE0F7FA)

// ── Typography & Surface Hierarchy Tokens ──
val OnSurfaceDark = Color(0xFFFFFFFF) // #FFFFFF Primary Text
val OnSurfaceVariantDark = Color(0xFFB3B3B3) // #B3B3B3 Secondary/Helper Text
val OnBackgroundDark = Color(0xFFFFFFFF) // #FFFFFF Primary Text

val OutlineDark = Color(0x1AFFFFFF) // rgba(255, 255, 255, 0.1)
val OutlineVariantDark = Color(0x1AFFFFFF)

// ── Glassmorphic Destination & Card Surface Tokens ──
val SageCardDark = Color(0x80121212)
val SageCardSurfaceDark = Color(0x99181818)
val MossButtonGradientStart = Color(0xFFFF2E63)
val MossButtonGradientEnd = Color(0xFFD9204F)
val MossButtonBorder = Color(0x4DFF2E63)

// ── Common Semantic & Accent Colors ──
val Error = Color(0xFFEF4444)
val OnError = Color(0xFFFFFFFF)
val ErrorContainer = Color(0xFFFEE2E2)
val OnErrorContainer = Color(0xFF991B1B)

val InverseSurface = Color(0xFF0F1E19)
val InverseOnSurface = Color(0xFFF2FAF6)
val SurfaceTint = AuroraCyan

val SafetyGreen = Color(0xFF10B981)
val SafetyYellow = Color(0xFFF59E0B)
val SafetyRed = Color(0xFFEF4444)
val AccentAmber = Color(0xFFF59E0B)

