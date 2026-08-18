package com.example.nomadcompass.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "packing_items",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["tripId"])]
)
data class PackingItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tripId: Int,
    val name: String,
    val isPacked: Boolean = false,
)
