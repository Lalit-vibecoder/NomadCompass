package com.example.nomadcompass.domain.model

data class Country(
    val cca3: String,
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
    val borders: List<String>,
    val latitude: Double,
    val longitude: Double,
    val isFavorite: Boolean = false,
)
