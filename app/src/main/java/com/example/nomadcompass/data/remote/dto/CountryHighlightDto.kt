package com.example.nomadcompass.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CountryHighlightDto(
    @Json(name = "top_places") val topPlaces: List<String> = emptyList(),
    @Json(name = "famous_festivals") val famousFestivals: List<String> = emptyList(),
    @Json(name = "attractive_features") val attractiveFeatures: List<String> = emptyList(),
)
