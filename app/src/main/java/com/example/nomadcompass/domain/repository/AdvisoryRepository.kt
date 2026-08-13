package com.example.nomadcompass.domain.repository

import com.example.nomadcompass.domain.model.Advisory

interface AdvisoryRepository {
    suspend fun getAdvisory(countryCode: String): Advisory?
}
