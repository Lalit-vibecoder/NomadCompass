package com.example.nomadcompass.data.repository

import com.example.nomadcompass.data.local.dao.CacheDao
import com.example.nomadcompass.data.local.entity.CurrencyCacheEntity
import com.example.nomadcompass.data.remote.api.ExchangeRateApi
import com.example.nomadcompass.data.remote.api.FrankfurterApi
import com.example.nomadcompass.domain.model.CurrencyRate
import com.example.nomadcompass.domain.repository.CurrencyRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyRepositoryImpl @Inject constructor(
    private val cacheDao: CacheDao,
    private val api: FrankfurterApi,
    private val exchangeRateApi: ExchangeRateApi? = null,
) : CurrencyRepository {

    private val ttlMillis = 24 * 60 * 60 * 1000L // 24 hours TTL

    // Reference exchange rates (Units per 1 USD) for offline fallback & unsupported currencies
    private val usdRates = mapOf(
        "USD" to 1.0,
        "EUR" to 0.92,
        "GBP" to 0.79,
        "JPY" to 155.0,
        "AUD" to 1.52,
        "CAD" to 1.36,
        "CHF" to 0.90,
        "CNY" to 7.23,
        "INR" to 83.5,
        "HKD" to 7.82,
        "NZD" to 1.64,
        "SGD" to 1.35,
        "SEK" to 10.5,
        "KRW" to 1360.0,
        "NOK" to 10.7,
        "MXN" to 17.5,
        "BRL" to 5.15,
        "ZAR" to 18.5,
        "THB" to 36.5,
        "IDR" to 16000.0,
        "MYR" to 4.70,
        "PHP" to 58.0,
        "CZK" to 23.0,
        "DKK" to 6.85,
        "HUF" to 360.0,
        "PLN" to 3.95,
        "TRY" to 32.5,
        "ILS" to 3.70,
        "AED" to 3.67,
        "SAR" to 3.75,
        "CLP" to 940.0,
        "COP" to 3900.0,
        "EGP" to 47.0,
        "VND" to 25400.0
    )

    override suspend fun getRate(
        baseCurrency: String,
        targetCurrency: String,
        forceRefresh: Boolean
    ): CurrencyRate? {
        val base = baseCurrency.uppercase()
        val target = targetCurrency.uppercase()

        if (base == target) {
            return CurrencyRate(base, target, 1.0)
        }

        val key = "${base}_${target}"
        val minTimestamp = System.currentTimeMillis() - ttlMillis

        // 1. Check valid cache (within TTL) if not forcing refresh
        if (!forceRefresh) {
            val cached = cacheDao.getCurrency(key, minTimestamp)
            if (cached != null) {
                return CurrencyRate(base, target, cached.rate)
            }
        }

        // 2. Remote fetch via ExchangeRate API (supports 160+ currencies worldwide)
        if (exchangeRateApi != null) {
            try {
                val response = exchangeRateApi.getLatestRates(base)
                val fetchedRate = response.rates?.get(target)
                if (fetchedRate != null) {
                    cacheDao.insertCurrency(
                        CurrencyCacheEntity(
                            key = key,
                            rate = fetchedRate,
                            cachedAt = System.currentTimeMillis(),
                        )
                    )
                    return CurrencyRate(base, target, fetchedRate)
                }
            } catch (_: Exception) {
                // ExchangeRate API call failed, fallback to Frankfurter
            }
        }

        // 3. Remote fetch via Frankfurter API
        try {
            val response = api.getLatestRates(base)
            val fetchedRate = response.rates?.get(target)
            if (fetchedRate != null) {
                cacheDao.insertCurrency(
                    CurrencyCacheEntity(
                        key = key,
                        rate = fetchedRate,
                        cachedAt = System.currentTimeMillis(),
                    )
                )
                return CurrencyRate(base, target, fetchedRate)
            }
        } catch (_: Exception) {
            // Network failure or unsupported base currency in Frankfurter
        }

        // 4. Fallback to expired cache
        val expired = cacheDao.getCurrency(key, 0)
        if (expired != null) {
            return CurrencyRate(base, target, expired.rate)
        }

        // 4. Calculate fallback cross-rate using USD reference table
        val fallbackRate = calculateFallbackRate(base, target)
        if (fallbackRate != null) {
            cacheDao.insertCurrency(
                CurrencyCacheEntity(
                    key = key,
                    rate = fallbackRate,
                    cachedAt = System.currentTimeMillis(),
                )
            )
            return CurrencyRate(base, target, fallbackRate)
        }

        return CurrencyRate(base, target, 1.0)
    }

    private fun calculateFallbackRate(base: String, target: String): Double? {
        val baseUsdRate = usdRates[base]
        val targetUsdRate = usdRates[target]
        if (baseUsdRate != null && targetUsdRate != null && baseUsdRate > 0) {
            return targetUsdRate / baseUsdRate
        }
        return null
    }
}

