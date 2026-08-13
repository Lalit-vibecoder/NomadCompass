package com.example.nomadcompass.data.repository

import com.example.nomadcompass.data.local.dao.CacheDao
import com.example.nomadcompass.data.local.entity.AdvisoryCacheEntity
import com.example.nomadcompass.data.remote.api.TravelAdvisoryApi
import com.example.nomadcompass.domain.model.Advisory
import com.example.nomadcompass.domain.repository.AdvisoryRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdvisoryRepositoryImpl @Inject constructor(
    private val cacheDao: CacheDao,
    private val api: TravelAdvisoryApi,
) : AdvisoryRepository {

    private val ttlMillis = 24 * 60 * 60 * 1000L // 24 hours TTL

    override suspend fun getAdvisory(countryCode: String): Advisory? {
        val codeUpper = countryCode.uppercase()
        val minTimestamp = System.currentTimeMillis() - ttlMillis

        val cached = cacheDao.getAdvisory(codeUpper, minTimestamp)
        if (cached != null) {
            return Advisory(countryCode = cached.countryCode, score = cached.score, message = cached.message)
        }

        return try {
            val response = api.getAdvisories()
            val dataItem = response.data?.get(codeUpper)?.advisory
            val score = dataItem?.score ?: 1.0
            val message = dataItem?.message ?: "Low risk area"

            cacheDao.insertAdvisory(
                AdvisoryCacheEntity(
                    countryCode = codeUpper,
                    score = score,
                    message = message,
                    cachedAt = System.currentTimeMillis(),
                )
            )

            Advisory(countryCode = codeUpper, score = score, message = message)
        } catch (e: Exception) {
            val expired = cacheDao.getAdvisory(codeUpper, 0)
            if (expired != null) {
                Advisory(countryCode = expired.countryCode, score = expired.score, message = expired.message)
            } else {
                Advisory(countryCode = codeUpper, score = 1.2, message = "Low travel risk (offline default)")
            }
        }
    }
}
