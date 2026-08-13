package com.example.nomadcompass.domain.model

data class Advisory(
    val countryCode: String,
    val score: Double,
    val message: String,
)
