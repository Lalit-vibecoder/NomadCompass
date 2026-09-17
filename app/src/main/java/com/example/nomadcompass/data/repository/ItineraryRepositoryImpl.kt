package com.example.nomadcompass.data.repository

import com.example.nomadcompass.data.local.dao.ItineraryEventDao
import com.example.nomadcompass.data.local.entity.ItineraryEventEntity
import com.example.nomadcompass.domain.model.ItineraryEvent
import com.example.nomadcompass.domain.repository.ItineraryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItineraryRepositoryImpl @Inject constructor(
    private val itineraryEventDao: ItineraryEventDao,
) : ItineraryRepository {

    override fun getItineraryEvents(tripId: Long): Flow<List<ItineraryEvent>> {
        return itineraryEventDao.getItineraryEventsByTrip(tripId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun saveItineraryEvent(event: ItineraryEvent): Long {
        return itineraryEventDao.insertItineraryEvent(ItineraryEventEntity.fromDomain(event))
    }

    override suspend fun saveItineraryEvents(events: List<ItineraryEvent>): List<Long> {
        val entities = events.map { ItineraryEventEntity.fromDomain(it) }
        return itineraryEventDao.insertItineraryEvents(entities)
    }

    override suspend fun updateItineraryEvent(event: ItineraryEvent) {
        itineraryEventDao.updateItineraryEvent(ItineraryEventEntity.fromDomain(event))
    }

    override suspend fun deleteItineraryEvent(id: Long) {
        itineraryEventDao.deleteItineraryEvent(id)
    }

    override suspend fun deleteItineraryEventsByTrip(tripId: Long) {
        itineraryEventDao.deleteItineraryEventsByTrip(tripId)
    }
}
