package com.example.nomadcompass.domain.model

data class UserProfile(
    val userName: String,
    val homeCountryCca3: String,
    val baseCurrencyCode: String,
    val tempUnit: String, // "C" or "F"
)
