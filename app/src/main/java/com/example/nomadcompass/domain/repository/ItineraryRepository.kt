package com.example.nomadcompass.domain.repository

import com.example.nomadcompass.domain.model.ItineraryEvent
import kotlinx.coroutines.flow.Flow

interface ItineraryRepository {
    fun getItineraryEvents(tripId: Long): Flow<List<ItineraryEvent>>
    suspend fun saveItineraryEvent(event: ItineraryEvent): Long
    suspend fun saveItineraryEvents(events: List<ItineraryEvent>): List<Long>
    suspend fun updateItineraryEvent(event: ItineraryEvent)
    suspend fun deleteItineraryEvent(id: Long)
    suspend fun deleteItineraryEventsByTrip(tripId: Long)
}
