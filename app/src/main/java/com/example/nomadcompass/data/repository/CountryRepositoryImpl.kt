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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
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
        countryDao.getAll()
            .map { entities -> entities.map { it.toDomain(highlightsMap) } }
            .flowOn(Dispatchers.Default)

    override fun getFavorites(): Flow<List<Country>> =
        countryDao.getFavorites()
            .map { entities -> entities.map { it.toDomain(highlightsMap) } }
            .flowOn(Dispatchers.Default)

    override fun search(query: String): Flow<List<Country>> =
        countryDao.search(query)
            .map { entities -> entities.map { it.toDomain(highlightsMap) } }
            .flowOn(Dispatchers.Default)

    override suspend fun getCountryByCode(cca3: String): Country? = withContext(Dispatchers.Default) {
        countryDao.getByCode(cca3)?.toDomain(highlightsMap)
    }

    override suspend fun getCountriesByCodes(cca3s: List<String>): List<Country> = withContext(Dispatchers.Default) {
        if (cca3s.isEmpty()) return@withContext emptyList()
        countryDao.getByCodes(cca3s).map { it.toDomain(highlightsMap) }
    }

    override fun getCountryHighlight(cca3: String): CountryHighlight? {
        val code = cca3.uppercase()
        return highlightsMap[code] ?: createFallbackHighlight(code)
    }

    override suspend fun toggleFavorite(cca3: String) {
        countryDao.toggleFavorite(cca3)
    }

    override suspend fun seedIfNeeded() = withContext(Dispatchers.IO) {
        if (countryDao.count() > 0) return@withContext
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

    private fun createFallbackHighlight(code: String): CountryHighlight {
        val p1 = "$code Historic Center"
        val p2 = "$code National Parks"
        val p3 = "$code Scenic Valleys"
        val f1 = "$code Heritage Festival"
        val f2 = "$code Arts & Music Gala"
        val a1 = "Traditional Culinary Delights"
        val a2 = "Scenic Nature Trails"
        val a3 = "Historic Monuments"

        val samplePhotos = listOf(
            "https://images.unsplash.com/photo-1502602898657-3e91760cbb34?auto=format&fit=crop&w=600&q=80",
            "https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?auto=format&fit=crop&w=600&q=80",
            "https://images.unsplash.com/photo-1564507592333-c60657eea523?auto=format&fit=crop&w=600&q=80",
            "https://images.unsplash.com/photo-1579783902614-a3fb3927b675?auto=format&fit=crop&w=600&q=80",
            "https://images.unsplash.com/photo-1582878826629-29b7ad1cdc43?auto=format&fit=crop&w=600&q=80",
            "https://images.unsplash.com/photo-1599833975787-5c143f373c30?auto=format&fit=crop&w=600&q=80",
            "https://images.unsplash.com/photo-1533105079780-92b9be482077?auto=format&fit=crop&w=600&q=80",
            "https://images.unsplash.com/photo-1568402102990-bc541580b59f?auto=format&fit=crop&w=600&q=80"
        )

        return CountryHighlight(
            topPlaces = listOf(p1, p2, p3),
            famousFestivals = listOf(f1, f2),
            attractiveFeatures = listOf(a1, a2, a3),
            mediaItems = listOf(
                HighlightMediaItem(p1, "TOP PLACE", samplePhotos[0]),
                HighlightMediaItem(p2, "TOP PLACE", samplePhotos[1]),
                HighlightMediaItem(p3, "TOP PLACE", samplePhotos[2]),
                HighlightMediaItem(f1, "FESTIVAL", samplePhotos[3]),
                HighlightMediaItem(f2, "FESTIVAL", samplePhotos[4]),
                HighlightMediaItem(a1, "ATTRACTION", samplePhotos[5]),
                HighlightMediaItem(a2, "ATTRACTION", samplePhotos[6]),
                HighlightMediaItem(a3, "ATTRACTION", samplePhotos[7])
            )
        )
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
    val code = cca3.uppercase()
    val name = commonName.ifBlank { code }
    val highlight = highlightsMap[code] ?: CountryHighlight(
        topPlaces = listOf("$name Historic Center", "National Parks of $name", "$name Scenic Coastline"),
        famousFestivals = listOf("$name Cultural Festival", "$name National Music Gala"),
        attractiveFeatures = listOf("Traditional $name Cuisine", "Nature & Wildlife Trails", "Historic Monuments"),
        mediaItems = listOf(
            HighlightMediaItem("$name Historic Center", "TOP PLACE", "https://images.unsplash.com/photo-1502602898657-3e91760cbb34?auto=format&fit=crop&w=600&q=80"),
            HighlightMediaItem("$name Cultural Festival", "FESTIVAL", "https://images.unsplash.com/photo-1579783902614-a3fb3927b675?auto=format&fit=crop&w=600&q=80")
        )
    )
    val snippet = highlight.getRandomHighlightString()
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
