package com.example.nomadcompass.domain.repository

import com.example.nomadcompass.domain.model.Trip
import com.example.nomadcompass.domain.model.TripAttachment
import kotlinx.coroutines.flow.Flow

interface TripRepository {
    fun getAllTrips(): Flow<List<Trip>>
    suspend fun addTrip(trip: Trip)
    suspend fun deleteTrip(tripId: Int)

    fun getAttachmentsForTrip(tripId: Int): Flow<List<TripAttachment>>
    suspend fun addAttachment(attachment: TripAttachment): Long
    suspend fun deleteAttachment(attachmentId: Long)
}

