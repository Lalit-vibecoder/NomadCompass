package com.example.nomadcompass.domain.model

data class CountryHighlight(
    val topPlaces: List<String> = emptyList(),
    val famousFestivals: List<String> = emptyList(),
    val attractiveFeatures: List<String> = emptyList(),
) {
    /**
     * Dynamically selects a single formatted highlight string from available candidates,
     * or returns null if no highlight data exists for this country.
     */
    fun getRandomHighlightString(): String? {
        val candidates = mutableListOf<String>()
        topPlaces.forEach { candidates.add("📍 Top Place: $it") }
        famousFestivals.forEach { candidates.add("✨ Festival: $it") }
        attractiveFeatures.forEach { candidates.add("🌟 Feature: $it") }

        if (candidates.isEmpty()) return null
        return candidates.random()
    }
}
