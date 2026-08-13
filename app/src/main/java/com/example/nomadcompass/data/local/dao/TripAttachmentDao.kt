package com.example.nomadcompass.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nomadcompass.data.local.entity.TripAttachmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TripAttachmentDao {

    @Query("SELECT * FROM trip_attachments WHERE tripId = :tripId ORDER BY displayOrder ASC, id ASC")
    fun getAttachmentsForTrip(tripId: Int): Flow<List<TripAttachmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttachment(entity: TripAttachmentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttachments(entities: List<TripAttachmentEntity>)

    @Query("DELETE FROM trip_attachments WHERE id = :id")
    suspend fun deleteAttachment(id: Long)

    @Query("DELETE FROM trip_attachments WHERE tripId = :tripId")
    suspend fun deleteAllForTrip(tripId: Int)
}
