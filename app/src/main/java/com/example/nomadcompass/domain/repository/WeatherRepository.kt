package com.example.nomadcompass.domain.repository

import com.example.nomadcompass.domain.model.Weather

interface WeatherRepository {
    suspend fun getWeather(latitude: Double, longitude: Double): Weather?
}
