package com.example.nomadcompass.domain.model

data class Trip(
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
