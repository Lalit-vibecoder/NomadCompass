package com.example.nomadcompass.data.repository

import com.example.nomadcompass.data.local.dao.CacheDao
import com.example.nomadcompass.data.local.entity.HolidayCacheEntity
import com.example.nomadcompass.data.remote.api.NagerDateApi
import com.example.nomadcompass.domain.model.Holiday
import com.example.nomadcompass.domain.repository.HolidayRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HolidayRepositoryImpl @Inject constructor(
    private val cacheDao: CacheDao,
    private val api: NagerDateApi,
) : HolidayRepository {

    private val ttlMillis = 30L * 24 * 60 * 60 * 1000L // 30 days TTL

    override suspend fun getHolidays(countryCode: String, year: Int): List<Holiday> {
        val codeUpper = countryCode.uppercase()
        val minTimestamp = System.currentTimeMillis() - ttlMillis

        val cached = cacheDao.getHolidays(codeUpper, year, minTimestamp)
        if (cached.isNotEmpty()) {
            return cached.map {
                Holiday(
                    date = it.date,
                    localName = it.localName,
                    name = it.name,
                    countryCode = it.countryCode
                )
            }
        }

        return try {
            val dtos = api.getPublicHolidays(year, codeUpper)
            val entities = dtos.map { dto ->
                HolidayCacheEntity(
                    countryCode = codeUpper,
                    year = year,
                    date = dto.date ?: "",
                    localName = dto.localName ?: "",
                    name = dto.name ?: "",
                    cachedAt = System.currentTimeMillis(),
                )
            }

            cacheDao.clearHolidays(codeUpper, year)
            cacheDao.insertHolidays(entities)

            entities.map {
                Holiday(
                    date = it.date,
                    localName = it.localName,
                    name = it.name,
                    countryCode = it.countryCode
                )
            }
        } catch (e: Exception) {
            val expired = cacheDao.getHolidays(codeUpper, year, 0)
            expired.map {
                Holiday(
                    date = it.date,
                    localName = it.localName,
                    name = it.name,
                    countryCode = it.countryCode
                )
            }
        }
    }
}
