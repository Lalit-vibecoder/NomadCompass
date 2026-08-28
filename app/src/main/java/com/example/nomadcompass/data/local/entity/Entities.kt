package com.example.nomadcompass.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "countries")
data class CountryEntity(
    @PrimaryKey val cca3: String,
    val cca2: String = "",
    val commonName: String,
    val officialName: String,
    val capital: String,
    val region: String,
    val subregion: String,
    val flagEmoji: String,
    val flagUrl: String,
    val currencyCode: String,
    val currencyName: String,
    val currencySymbol: String,
    val languages: String,
    val borders: String, // comma-separated cca3 codes
    val latitude: Double,
    val longitude: Double,
    val isFavorite: Boolean = false,
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val userName: String,
    val homeCountryCca3: String,
    val baseCurrencyCode: String,
    val tempUnit: String,
    val photoUri: String? = null,
    val isBiometricEnabled: Boolean = false,
    val accessCode: String = "",
    val themeMode: String = "DARK",
    val bgPhotoUri: String? = null,
    val bgBlurRadius: Float = 24f,
)

@Entity(tableName = "weather_cache")
data class WeatherCacheEntity(
    @PrimaryKey val key: String, // "lat,lon"
    val temperatureCelsius: Double,
    val temperatureFahrenheit: Double,
    val weatherCode: Int,
    val weatherDescription: String,
    val windSpeedKmh: Double,
    val uvIndex: Double,
    val cachedAt: Long,
)

@Entity(tableName = "currency_cache")
data class CurrencyCacheEntity(
    @PrimaryKey val key: String, // "base_target"
    val rate: Double,
    val cachedAt: Long,
)

@Entity(tableName = "advisory_cache")
data class AdvisoryCacheEntity(
    @PrimaryKey val countryCode: String,
    val score: Double,
    val message: String,
    val cachedAt: Long,
)

@Entity(tableName = "holiday_cache")
data class HolidayCacheEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val countryCode: String,
    val year: Int,
    val date: String,
    val localName: String,
    val name: String,
    val cachedAt: Long,
)
