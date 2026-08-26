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
)
