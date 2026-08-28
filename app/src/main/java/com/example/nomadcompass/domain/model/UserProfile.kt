package com.example.nomadcompass.domain.model

data class UserProfile(
    val userName: String,
    val homeCountryCca3: String,
    val baseCurrencyCode: String,
    val tempUnit: String, // "C" or "F"
    val photoUri: String? = null,
    val isBiometricEnabled: Boolean = false,
    val accessCode: String = "", // 4-digit PIN
    val themeMode: String = "DARK", // "DARK" or "LIGHT"
    val bgPhotoUri: String? = null, // Custom background wallpaper photo path
    val bgBlurRadius: Float = 24f, // Background blur radius in dp (0f..50f)
)
