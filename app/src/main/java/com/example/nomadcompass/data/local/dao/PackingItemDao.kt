package com.example.nomadcompass.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.nomadcompass.data.local.entity.PackingItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PackingItemDao {

    @Query("SELECT * FROM packing_items WHERE tripId = :tripId ORDER BY id ASC")
    fun getPackingItemsForTrip(tripId: Int): Flow<List<PackingItemEntity>>

    @Query("SELECT COUNT(*) FROM packing_items WHERE tripId = :tripId")
    suspend fun getPackingItemCount(tripId: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPackingItem(item: PackingItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<PackingItemEntity>)

    @Update
    suspend fun updatePackingItem(item: PackingItemEntity)

    @Query("DELETE FROM packing_items WHERE id = :id")
    suspend fun deletePackingItem(id: Long)
}
