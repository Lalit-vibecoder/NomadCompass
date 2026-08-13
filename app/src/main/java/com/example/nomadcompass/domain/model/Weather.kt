package com.example.nomadcompass.domain.model

data class Weather(
    val temperatureCelsius: Double,
    val temperatureFahrenheit: Double,
    val weatherCode: Int,
    val weatherDescription: String,
    val windSpeedKmh: Double,
    val uvIndex: Double,
)
