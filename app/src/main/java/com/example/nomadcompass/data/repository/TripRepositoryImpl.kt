package com.example.nomadcompass.data.repository

import com.example.nomadcompass.data.local.dao.TripAttachmentDao
import com.example.nomadcompass.data.local.dao.TripDao
import com.example.nomadcompass.data.local.entity.TripEntity
import com.example.nomadcompass.data.local.entity.toDomain
import com.example.nomadcompass.data.local.entity.toEntity
import com.example.nomadcompass.domain.model.Trip
import com.example.nomadcompass.domain.model.TripAttachment
import com.example.nomadcompass.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TripRepositoryImpl @Inject constructor(
    private val tripDao: TripDao,
    private val attachmentDao: TripAttachmentDao,
) : TripRepository {

    override fun getAllTrips(): Flow<List<Trip>> =
        tripDao.getAllTrips().map { entities ->
            entities.map {
                Trip(
                    id = it.id,
                    destinationCca3 = it.destinationCca3,
                    countryName = it.countryName,
                    flagEmoji = it.flagEmoji,
                    flagUrl = it.flagUrl,
                    startDate = it.startDate,
                    endDate = it.endDate,
                    budgetUsd = it.budgetUsd,
                    notes = it.notes,
                    status = it.status
                )
            }
        }

    override suspend fun addTrip(trip: Trip) {
        tripDao.insertTrip(
            TripEntity(
                id = trip.id,
                destinationCca3 = trip.destinationCca3,
                countryName = trip.countryName,
                flagEmoji = trip.flagEmoji,
                flagUrl = trip.flagUrl,
                startDate = trip.startDate,
                endDate = trip.endDate,
                budgetUsd = trip.budgetUsd,
                notes = trip.notes,
                status = trip.status
            )
        )
    }

    override suspend fun deleteTrip(tripId: Int) {
        attachmentDao.deleteAllForTrip(tripId)
        tripDao.deleteTrip(tripId)
    }

    override fun getAttachmentsForTrip(tripId: Int): Flow<List<TripAttachment>> =
        attachmentDao.getAttachmentsForTrip(tripId).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun addAttachment(attachment: TripAttachment): Long =
        attachmentDao.insertAttachment(attachment.toEntity())

    override suspend fun updateAttachments(attachments: List<TripAttachment>) {
        attachmentDao.insertAttachments(attachments.map { it.toEntity() })
    }

    override suspend fun deleteAttachment(attachmentId: Long) {
        attachmentDao.deleteAttachment(attachmentId)
    }
}

