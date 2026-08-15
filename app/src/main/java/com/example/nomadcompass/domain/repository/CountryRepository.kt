package com.example.nomadcompass.domain.repository

import com.example.nomadcompass.domain.model.Country
import kotlinx.coroutines.flow.Flow

interface CountryRepository {
    fun getAllCountries(): Flow<List<Country>>
    fun getFavorites(): Flow<List<Country>>
    fun search(query: String): Flow<List<Country>>
    suspend fun getCountryByCode(cca3: String): Country?
    suspend fun getCountriesByCodes(cca3s: List<String>): List<Country>
    suspend fun toggleFavorite(cca3: String)
    suspend fun seedIfNeeded()
}
