package com.example.nomadcompass.data.repository

import com.example.nomadcompass.data.local.dao.CacheDao
import com.example.nomadcompass.data.local.entity.WeatherCacheEntity
import com.example.nomadcompass.data.remote.api.OpenMeteoApi
import com.example.nomadcompass.domain.model.Weather
import com.example.nomadcompass.domain.repository.WeatherRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val cacheDao: CacheDao,
    private val api: OpenMeteoApi,
) : WeatherRepository {

    private val ttlMillis = 30 * 60 * 1000L // 30 minutes TTL

    override suspend fun getWeather(latitude: Double, longitude: Double): Weather? {
        val key = "${latitude},${longitude}"
        val minTimestamp = System.currentTimeMillis() - ttlMillis

        // Check cache first
        val cached = cacheDao.getWeather(key, minTimestamp)
        if (cached != null) {
            return Weather(
                temperatureCelsius = cached.temperatureCelsius,
                temperatureFahrenheit = cached.temperatureFahrenheit,
                weatherCode = cached.weatherCode,
                weatherDescription = cached.weatherDescription,
                windSpeedKmh = cached.windSpeedKmh,
                uvIndex = cached.uvIndex,
            )
        }

        // Fetch remote if cache is stale/missing
        return try {
            val response = api.getWeather(latitude = latitude, longitude = longitude)
            val current = response.current
            val tempC = current?.temperature ?: 20.0
            val tempF = (tempC * 9 / 5) + 32
            val code = current?.weatherCode ?: 0
            val desc = getWeatherDescription(code)
            val wind = current?.windSpeed ?: 10.0
            val uv = response.daily?.uvIndexMax?.firstOrNull() ?: 5.0

            val entity = WeatherCacheEntity(
                key = key,
                temperatureCelsius = tempC,
                temperatureFahrenheit = tempF,
                weatherCode = code,
                weatherDescription = desc,
                windSpeedKmh = wind,
                uvIndex = uv,
                cachedAt = System.currentTimeMillis(),
            )
            cacheDao.insertWeather(entity)

            Weather(
                temperatureCelsius = tempC,
                temperatureFahrenheit = tempF,
                weatherCode = code,
                weatherDescription = desc,
                windSpeedKmh = wind,
                uvIndex = uv,
            )
        } catch (e: Exception) {
            // Fallback to expired cache if available
            val expired = cacheDao.getWeather(key, 0)
            if (expired != null) {
                Weather(
                    temperatureCelsius = expired.temperatureCelsius,
                    temperatureFahrenheit = expired.temperatureFahrenheit,
                    weatherCode = expired.weatherCode,
                    weatherDescription = expired.weatherDescription,
                    windSpeedKmh = expired.windSpeedKmh,
                    uvIndex = expired.uvIndex,
                )
            } else {
                null
            }
        }
    }

    private fun getWeatherDescription(code: Int): String = when (code) {
        0 -> "Clear sky"
        1, 2, 3 -> "Partly cloudy"
        45, 48 -> "Foggy"
        51, 53, 55 -> "Drizzle"
        61, 63, 65 -> "Rainy"
        71, 73, 75 -> "Snowy"
        80, 81, 82 -> "Rain showers"
        95, 96, 99 -> "Thunderstorm"
        else -> "Sunny"
    }
}
