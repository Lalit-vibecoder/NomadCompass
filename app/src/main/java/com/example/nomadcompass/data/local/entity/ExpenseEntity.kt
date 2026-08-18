package com.example.nomadcompass.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "expenses",
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
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tripId: Int,
    val title: String,
    val amountLocal: Double,
    val currencyCode: String,
    val amountHome: Double,
    val isUnconverted: Boolean = false,
    val category: String,
    val date: Long,
    val notes: String = "",
)
