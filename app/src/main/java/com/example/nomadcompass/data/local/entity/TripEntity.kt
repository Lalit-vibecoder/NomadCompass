package com.example.nomadcompass.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val destinationCca3: String,
    val countryName: String,
    val flagEmoji: String,
    val flagUrl: String,
    val startDate: String,
    val endDate: String,
    val budgetUsd: Double,
    val notes: String,
    val status: String = "Upcoming",
)
