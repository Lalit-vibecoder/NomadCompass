package com.example.nomadcompass.domain.model

data class HighlightMediaItem(
    val title: String,
    val category: String,
    val imageUrl: String,
)

data class CountryHighlight(
    val topPlaces: List<String> = emptyList(),
    val famousFestivals: List<String> = emptyList(),
    val attractiveFeatures: List<String> = emptyList(),
    val mediaItems: List<HighlightMediaItem> = emptyList(),
) {
    /**
     * Fast deterministic highlight string snippet for list cards, avoiding repeated list allocations.
     */
    val highlightSnippet: String? by lazy {
        topPlaces.firstOrNull()?.let { "📍 Top Place: $it" }
            ?: famousFestivals.firstOrNull()?.let { "✨ Festival: $it" }
            ?: attractiveFeatures.firstOrNull()?.let { "🌟 Feature: $it" }
    }

    fun getRandomHighlightString(): String? = highlightSnippet
}
