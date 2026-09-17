package com.example.nomadcompass.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.nomadcompass.data.local.entity.ItineraryEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItineraryEventDao {

    @Query("SELECT * FROM itinerary_events WHERE tripId = :tripId ORDER BY startDateTime ASC, id ASC")
    fun getItineraryEventsByTrip(tripId: Long): Flow<List<ItineraryEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItineraryEvent(event: ItineraryEventEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItineraryEvents(events: List<ItineraryEventEntity>): List<Long>

    @Update
    suspend fun updateItineraryEvent(event: ItineraryEventEntity)

    @Query("DELETE FROM itinerary_events WHERE id = :id")
    suspend fun deleteItineraryEvent(id: Long)

    @Query("DELETE FROM itinerary_events WHERE tripId = :tripId")
    suspend fun deleteItineraryEventsByTrip(tripId: Long)
}
