package com.example.nomadcompass.data.repository

import android.content.Context
import com.example.nomadcompass.data.local.dao.PackingItemDao
import com.example.nomadcompass.data.local.entity.PackingItemEntity
import com.example.nomadcompass.domain.model.PackingItem
import com.example.nomadcompass.domain.repository.PackingRepository
import com.example.nomadcompass.widget.PackingWidgetProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PackingRepositoryImpl @Inject constructor(
    private val packingItemDao: PackingItemDao,
    @ApplicationContext private val context: Context,
) : PackingRepository {

    private val defaultPackingItems = listOf(
        "Passport & Travel Visa Documents",
        "Nomad Laptop & Power Brick",
        "Universal Plug Adapter & Power Bank",
        "Multi-Currency Credit Card",
        "Noise-Canceling Headphones",
        "Travel Insurance Policy & Emergency Contacts"
    )

    override fun getPackingItemsForTrip(tripId: Int): Flow<List<PackingItem>> {
        return packingItemDao.getPackingItemsForTrip(tripId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun togglePackingItem(item: PackingItem) {
        val updated = item.copy(isPacked = !item.isPacked)
        packingItemDao.updatePackingItem(updated.toEntity())
        PackingWidgetProvider.notifyDataChanged(context)
    }

    override suspend fun addPackingItem(tripId: Int, name: String): Long {
        val entity = PackingItemEntity(
            tripId = tripId,
            name = name,
            isPacked = false
        )
        val id = packingItemDao.insertPackingItem(entity)
        PackingWidgetProvider.notifyDataChanged(context)
        return id
    }

    override suspend fun deletePackingItem(id: Long) {
        packingItemDao.deletePackingItem(id)
        PackingWidgetProvider.notifyDataChanged(context)
    }

    override suspend fun seedDefaultsIfEmpty(tripId: Int) {
        if (packingItemDao.getPackingItemCount(tripId) == 0) {
            val entities = defaultPackingItems.map { name ->
                PackingItemEntity(
                    tripId = tripId,
                    name = name,
                    isPacked = false
                )
            }
            packingItemDao.insertAll(entities)
            PackingWidgetProvider.notifyDataChanged(context)
        }
    }

    private fun PackingItemEntity.toDomain(): PackingItem = PackingItem(
        id = id,
        tripId = tripId,
        name = name,
        isPacked = isPacked
    )

    private fun PackingItem.toEntity(): PackingItemEntity = PackingItemEntity(
        id = id,
        tripId = tripId,
        name = name,
        isPacked = isPacked
    )
}
