package com.example.nomadcompass.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Supported categories for structured itinerary events.
 */
enum class ItineraryCategory(
    val displayName: String,
    val emoji: String,
    val icon: ImageVector,
    val defaultColor: Color,
) {
    FLIGHT("Flight", "✈️", Icons.Default.Flight, Color(0xFF6EAD91)),
    ACCOMMODATION("Accommodation", "🏨", Icons.Default.Hotel, Color(0xFF5D8877)),
    ACTIVITY("Activity & Tour", "🎯", Icons.Default.Event, Color(0xFFE5A65D)),
    TRANSPORT("Ground Transport", "🚆", Icons.Default.DirectionsBus, Color(0xFF5A9FD4)),
    RESTAURANT("Dining & Food", "🍽️", Icons.Default.Restaurant, Color(0xFFE06D53)),
    OTHER("Other Event", "📌", Icons.Default.MoreHoriz, Color(0xFFA1BEB2));

    companion object {
        fun fromString(value: String?): ItineraryCategory {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: OTHER
        }
    }
}

/**
 * Domain representation of a confirmed Itinerary Event linked to a trip.
 */
data class ItineraryEvent(
    val id: Long = 0,
    val tripId: Long,
    val title: String,
    val category: ItineraryCategory = ItineraryCategory.OTHER,
    val location: String = "",
    val startDateTime: String = "",
    val endDateTime: String = "",
    val confirmationCode: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
)

/**
 * Represents an extracted candidate event during offline ingestion / parsing, before saving to database.
 */
data class DraftItineraryEvent(
    val id: String = java.util.UUID.randomUUID().toString(),
    var title: String,
    var category: ItineraryCategory = ItineraryCategory.OTHER,
    var location: String = "",
    var startDateTime: String = "",
    var endDateTime: String = "",
    var confirmationCode: String = "",
    var notes: String = "",
    var rawSnippet: String = "",
    var confidenceScore: Float = 1.0f,
    var isSelected: Boolean = true,
)
