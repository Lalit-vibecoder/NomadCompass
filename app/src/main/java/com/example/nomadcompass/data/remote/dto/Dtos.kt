package com.example.nomadcompass.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
data class RestCountryDto(
    @Json(name = "cca3") val cca3: String? = null,
    @Json(name = "cca2") val cca2: String? = null,
    @Json(name = "name") val name: NameDto? = null,
    @Json(name = "capital") val capital: List<String>? = null,
    @Json(name = "region") val region: String? = null,
    @Json(name = "subregion") val subregion: String? = null,
    @Json(name = "flags") val flags: FlagsDto? = null,
    @Json(name = "flag") val flag: String? = null,
    @Json(name = "currencies") val currencies: Map<String, CurrencyInfoDto>? = null,
    @Json(name = "languages") val languages: Map<String, String>? = null,
    @Json(name = "borders") val borders: List<String>? = null,
    @Json(name = "latlng") val latlng: List<Double>? = null,
    @Json(name = "landlocked") val landlocked: Boolean? = null,
)

@JsonClass(generateAdapter = false)
data class NameDto(
    @Json(name = "common") val common: String? = null,
    @Json(name = "official") val official: String? = null,
)

@JsonClass(generateAdapter = false)
data class FlagsDto(
    @Json(name = "png") val png: String? = null,
    @Json(name = "svg") val svg: String? = null,
)

@JsonClass(generateAdapter = false)
data class CurrencyInfoDto(
    @Json(name = "name") val name: String? = null,
    @Json(name = "symbol") val symbol: String? = null,
)

// ── Open-Meteo ──

@JsonClass(generateAdapter = false)
data class OpenMeteoResponse(
    @Json(name = "current") val current: CurrentWeatherDto? = null,
    @Json(name = "daily") val daily: DailyWeatherDto? = null,
)

@JsonClass(generateAdapter = false)
data class CurrentWeatherDto(
    @Json(name = "temperature_2m") val temperature: Double? = null,
    @Json(name = "weather_code") val weatherCode: Int? = null,
    @Json(name = "wind_speed_10m") val windSpeed: Double? = null,
)

@JsonClass(generateAdapter = false)
data class DailyWeatherDto(
    @Json(name = "uv_index_max") val uvIndexMax: List<Double>? = null,
)

// ── Frankfurter ──

@JsonClass(generateAdapter = false)
data class FrankfurterResponse(
    @Json(name = "base") val base: String? = null,
    @Json(name = "date") val date: String? = null,
    @Json(name = "rates") val rates: Map<String, Double>? = null,
)

// ── Nager.Date ──

@JsonClass(generateAdapter = false)
data class NagerHolidayDto(
    @Json(name = "date") val date: String? = null,
    @Json(name = "localName") val localName: String? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "countryCode") val countryCode: String? = null,
)

// ── Travel Advisory ──

@JsonClass(generateAdapter = false)
data class TravelAdvisoryResponse(
    @Json(name = "data") val data: Map<String, AdvisoryDataDto>? = null,
)

@JsonClass(generateAdapter = false)
data class AdvisoryDataDto(
    @Json(name = "advisory") val advisory: AdvisoryInfoDto? = null,
)

@JsonClass(generateAdapter = false)
data class AdvisoryInfoDto(
    @Json(name = "score") val score: Double? = null,
    @Json(name = "message") val message: String? = null,
)
