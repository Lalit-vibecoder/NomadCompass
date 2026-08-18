package com.example.nomadcompass.domain.repository

import com.example.nomadcompass.domain.model.PackingItem
import kotlinx.coroutines.flow.Flow

interface PackingRepository {
    fun getPackingItemsForTrip(tripId: Int): Flow<List<PackingItem>>
    suspend fun togglePackingItem(item: PackingItem)
    suspend fun addPackingItem(tripId: Int, name: String): Long
    suspend fun deletePackingItem(id: Long)
    suspend fun seedDefaultsIfEmpty(tripId: Int)
}
