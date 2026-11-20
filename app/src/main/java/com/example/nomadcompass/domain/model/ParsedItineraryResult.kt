package com.example.nomadcompass.domain.model

/**
 * Represents the extracted plan for an individual day in an itinerary,
 * preserving the hierarchical relationship: Day -> Location/City -> Places to Visit.
 */
data class ParsedDayPlan(
    val dayLabel: String,
    val date: String?,
    val location: String?,
    val placesToVisit: List<String>,
    val notes: String?
)

/**
 * Represents the complete parsed itinerary with hierarchical day structures.
 */
data class ParsedItineraryResult(
    val tripTitle: String?,
    val days: List<ParsedDayPlan>
)

/**
 * Converts hierarchical parsed day plans into a flat list of [DraftItineraryEvent]s
 * suitable for review, editing, and persisting into the local database.
 */
fun ParsedItineraryResult.toDraftEvents(defaultDestination: String = ""): List<DraftItineraryEvent> {
    val events = mutableListOf<DraftItineraryEvent>()

    for (day in days) {
        val effectiveLocation = day.location ?: defaultDestination

        if (day.placesToVisit.isNotEmpty()) {
            for (place in day.placesToVisit) {
                val cat = inferCategory(place)
                events.add(
                    DraftItineraryEvent(
                        title = place,
                        category = cat,
                        location = effectiveLocation,
                        startDateTime = day.date ?: "",
                        endDateTime = "",
                        confirmationCode = "",
                        notes = day.notes ?: "",
                        rawSnippet = "${day.dayLabel}: $place",
                        confidenceScore = 0.95f,
                        isSelected = true
                    )
                )
            }
        } else if (!day.location.isNullOrBlank() || day.notes != null) {
            val locationCandidate = day.location ?: "Day Plan"
            val cat = inferCategory(locationCandidate)
            val title = if (locationCandidate.startsWith("Flight", ignoreCase = true) ||
                locationCandidate.startsWith("Stay", ignoreCase = true) ||
                locationCandidate.startsWith("Tour", ignoreCase = true)
            ) {
                locationCandidate
            } else {
                "${day.dayLabel}: ${locationCandidate.ifBlank { "Day Plan" }}"
            }

            events.add(
                DraftItineraryEvent(
                    title = title,
                    category = cat,
                    location = effectiveLocation,
                    startDateTime = day.date ?: "",
                    endDateTime = "",
                    confirmationCode = "",
                    notes = day.notes ?: "",
                    rawSnippet = day.dayLabel,
                    confidenceScore = 0.85f,
                    isSelected = true
                )
            )
        }
    }

    return events
}

private fun inferCategory(text: String): ItineraryCategory {
    val lower = text.lowercase()
    return when {
        lower.contains("flight") || lower.contains("airline") -> ItineraryCategory.FLIGHT
        lower.contains("hotel") || lower.contains("resort") || lower.contains("hostel") || lower.contains("stay at") || lower.contains("check-in") || lower.contains("airbnb") -> ItineraryCategory.ACCOMMODATION
        lower.contains("train") || lower.contains("bus") || lower.contains("rail") || lower.contains("ferry") || lower.contains("transit") || lower.contains("subway") -> ItineraryCategory.TRANSPORT
        lower.contains("dinner") || lower.contains("lunch") || lower.contains("breakfast") || lower.contains("restaurant") || lower.contains("bistro") || lower.contains("cafe") -> ItineraryCategory.RESTAURANT
        else -> ItineraryCategory.ACTIVITY
    }
}
