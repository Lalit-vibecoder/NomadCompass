package com.example.nomadcompass.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HighlightItemDto(
    @Json(name = "title") val title: String = "",
    @Json(name = "category") val category: String = "",
    @Json(name = "image_url") val imageUrl: String = "",
)

@JsonClass(generateAdapter = true)
data class CountryHighlightDto(
    @Json(name = "top_places") val topPlaces: List<String> = emptyList(),
    @Json(name = "famous_festivals") val famousFestivals: List<String> = emptyList(),
    @Json(name = "attractive_features") val attractiveFeatures: List<String> = emptyList(),
    @Json(name = "highlight_items") val highlightItems: List<HighlightItemDto> = emptyList(),
)
