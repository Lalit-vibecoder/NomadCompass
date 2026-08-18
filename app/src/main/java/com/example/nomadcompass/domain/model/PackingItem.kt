package com.example.nomadcompass.domain.model

data class PackingItem(
    val id: Long = 0,
    val tripId: Int,
    val name: String,
    val isPacked: Boolean = false,
)
