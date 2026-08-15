package com.example.nomadcompass.data.repository

import android.content.Context
import com.example.nomadcompass.data.local.dao.CountryDao
import com.example.nomadcompass.data.local.entity.CountryEntity
import com.example.nomadcompass.data.remote.api.RestCountriesApi
import com.example.nomadcompass.data.remote.dto.RestCountryDto
import com.example.nomadcompass.data.remote.dto.CountryHighlightDto
import com.example.nomadcompass.domain.model.Country
import com.example.nomadcompass.domain.model.CountryHighlight
import com.example.nomadcompass.domain.model.HighlightMediaItem
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

    private val highlightsMap: Map<String, CountryHighlight> by lazy {
        loadHighlightsFromAssets()
    }

    override fun getAllCountries(): Flow<List<Country>> =
        countryDao.getAll().map { entities -> entities.map { it.toDomain(highlightsMap) } }

    override fun getFavorites(): Flow<List<Country>> =
        countryDao.getFavorites().map { entities -> entities.map { it.toDomain(highlightsMap) } }

    override fun search(query: String): Flow<List<Country>> =
        countryDao.search(query).map { entities -> entities.map { it.toDomain(highlightsMap) } }

    override suspend fun getCountryByCode(cca3: String): Country? =
        countryDao.getByCode(cca3)?.toDomain(highlightsMap)

    override suspend fun getCountriesByCodes(cca3s: List<String>): List<Country> {
        if (cca3s.isEmpty()) return emptyList()
        return countryDao.getByCodes(cca3s).map { it.toDomain(highlightsMap) }
    }

    override fun getCountryHighlight(cca3: String): CountryHighlight? =
        highlightsMap[cca3.uppercase()]

    override suspend fun toggleFavorite(cca3: String) {
        countryDao.toggleFavorite(cca3)
    }

    override suspend fun seedIfNeeded() {
        if (countryDao.count() > 0) return
        val seedDtos = loadSeedFromAssets()
        val entities = seedDtos.mapNotNull { it.toEntity() }
        countryDao.insertAll(entities)
    }

    private fun loadSeedFromAssets(): List<RestCountryDto> {
        val json = context.assets.open("seed_countries.json")
            .bufferedReader()
            .use { it.readText() }
        val type = Types.newParameterizedType(List::class.java, RestCountryDto::class.java)
        val adapter = moshi.adapter<List<RestCountryDto>>(type)
        return adapter.fromJson(json) ?: emptyList()
    }

    private fun loadHighlightsFromAssets(): Map<String, CountryHighlight> {
        return try {
            val json = context.assets.open("country_highlights.json")
                .bufferedReader()
                .use { it.readText() }
            val type = Types.newParameterizedType(
                Map::class.java,
                String::class.java,
                CountryHighlightDto::class.java
            )
            val adapter = moshi.adapter<Map<String, CountryHighlightDto>>(type)
            val dtoMap = adapter.fromJson(json) ?: emptyMap()

            dtoMap.mapKeys { it.key.uppercase() }.mapValues { (_, dto) ->
                CountryHighlight(
                    topPlaces = dto.topPlaces,
                    famousFestivals = dto.famousFestivals,
                    attractiveFeatures = dto.attractiveFeatures,
                    mediaItems = dto.highlightItems.map {
                        HighlightMediaItem(
                            title = it.title,
                            category = it.category,
                            imageUrl = it.imageUrl,
                        )
                    }
                )
            }
        } catch (_: Exception) {
            emptyMap()
        }
    }
}

// ── Mappers ──

private fun RestCountryDto.toEntity(): CountryEntity? {
    val code = cca3 ?: return null
    val firstCurrency = currencies?.entries?.firstOrNull()
    return CountryEntity(
        cca3 = code,
        cca2 = cca2 ?: "",
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

private fun CountryEntity.toDomain(highlightsMap: Map<String, CountryHighlight>): Country {
    val highlight = highlightsMap[cca3.uppercase()]
    val snippet = highlight?.getRandomHighlightString()
    return Country(
        cca3 = cca3,
        cca2 = cca2,
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
        highlightSnippet = snippet,
    )
}
