package com.example.nomadcompass.data.repository

import android.content.Context
import com.example.nomadcompass.data.local.dao.CountryDao
import com.example.nomadcompass.data.local.entity.CountryEntity
import com.example.nomadcompass.data.remote.api.RestCountriesApi
import com.example.nomadcompass.data.remote.dto.RestCountryDto
import com.example.nomadcompass.domain.model.Country
import com.example.nomadcompass.domain.repository.CountryRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CountryRepositoryImpl @Inject constructor(
    private val countryDao: CountryDao,
    private val api: RestCountriesApi,
    private val moshi: Moshi,
    @ApplicationContext private val context: Context,
) : CountryRepository {

    override fun getAllCountries(): Flow<List<Country>> =
        countryDao.getAll().map { entities -> entities.map { it.toDomain() } }

    override fun getFavorites(): Flow<List<Country>> =
        countryDao.getFavorites().map { entities -> entities.map { it.toDomain() } }

    override fun search(query: String): Flow<List<Country>> =
        countryDao.search(query).map { entities -> entities.map { it.toDomain() } }

    override suspend fun getCountryByCode(cca3: String): Country? =
        countryDao.getByCode(cca3)?.toDomain()

    override suspend fun toggleFavorite(cca3: String) {
        countryDao.toggleFavorite(cca3)
    }

    override suspend fun seedIfNeeded() {
        val seedDtos = loadSeedFromAssets()
        if (countryDao.count() < seedDtos.size) {
            val entities = seedDtos.mapNotNull { it.toEntity() }
            countryDao.insertAll(entities)
        }
    }

    private fun loadSeedFromAssets(): List<RestCountryDto> {
        val json = context.assets.open("seed_countries.json")
            .bufferedReader()
            .use { it.readText() }
        val type = Types.newParameterizedType(List::class.java, RestCountryDto::class.java)
        val adapter = moshi.adapter<List<RestCountryDto>>(type)
        return adapter.fromJson(json) ?: emptyList()
    }
}

// ── Mappers ──

private fun RestCountryDto.toEntity(): CountryEntity? {
    val code = cca3 ?: return null
    val firstCurrency = currencies?.entries?.firstOrNull()
    return CountryEntity(
        cca3 = code,
        commonName = name?.common ?: "",
        officialName = name?.official ?: "",
        capital = capital?.firstOrNull() ?: "",
        region = region ?: "",
        subregion = subregion ?: "",
        flagEmoji = flag ?: "",
        flagUrl = flags?.png ?: "",
        currencyCode = firstCurrency?.key ?: "",
        currencyName = firstCurrency?.value?.name ?: "",
        currencySymbol = firstCurrency?.value?.symbol ?: "",
        languages = languages?.values?.joinToString(", ") ?: "",
        borders = borders?.joinToString(",") ?: "",
        latitude = latlng?.getOrNull(0) ?: 0.0,
        longitude = latlng?.getOrNull(1) ?: 0.0,
    )
}

private fun CountryEntity.toDomain(): Country = Country(
    cca3 = cca3,
    commonName = commonName,
    officialName = officialName,
    capital = capital,
    region = region,
    subregion = subregion,
    flagEmoji = flagEmoji,
    flagUrl = flagUrl,
    currencyCode = currencyCode,
    currencyName = currencyName,
    currencySymbol = currencySymbol,
    languages = languages,
    borders = if (borders.isBlank()) emptyList() else borders.split(","),
    latitude = latitude,
    longitude = longitude,
    isFavorite = isFavorite,
)
