package com.example.nomadcompass.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.nomadcompass.domain.model.ItineraryCategory
import com.example.nomadcompass.domain.model.ItineraryEvent

@Entity(
    tableName = "itinerary_events",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [
        Index(value = ["tripId"]),
        Index(value = ["startDateTime"])
    ]
)
data class ItineraryEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tripId: Long,
    val title: String,
    val category: String,
    val location: String = "",
    val startDateTime: String = "",
    val endDateTime: String = "",
    val confirmationCode: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
) {
    fun toDomain(): ItineraryEvent = ItineraryEvent(
        id = id,
        tripId = tripId,
        title = title,
        category = ItineraryCategory.fromString(category),
        location = location,
        startDateTime = startDateTime,
        endDateTime = endDateTime,
        confirmationCode = confirmationCode,
        notes = notes,
        createdAt = createdAt,
    )

    companion object {
        fun fromDomain(event: ItineraryEvent): ItineraryEventEntity = ItineraryEventEntity(
            id = event.id,
            tripId = event.tripId,
            title = event.title,
            category = event.category.name,
            location = event.location,
            startDateTime = event.startDateTime,
            endDateTime = event.endDateTime,
            confirmationCode = event.confirmationCode,
            notes = event.notes,
            createdAt = event.createdAt,
        )
    }
}
