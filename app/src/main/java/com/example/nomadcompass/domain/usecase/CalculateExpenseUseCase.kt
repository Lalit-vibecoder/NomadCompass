package com.example.nomadcompass.domain.usecase

import com.example.nomadcompass.data.local.dao.CacheDao
import javax.inject.Inject

data class ConversionResult(
    val amountHome: Double,
    val isUnconverted: Boolean,
    val conversionRate: Double?
)

class CalculateExpenseUseCase @Inject constructor(
    private val cacheDao: CacheDao,
) {
    // Reference exchange rates (Units per 1 USD) for offline cross-currency calculation
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

    suspend fun calculateHomeAmount(
        amountLocal: Double,
        currencyCode: String,
        homeCurrencyCode: String,
    ): ConversionResult {
        val localCode = currencyCode.uppercase().trim()
        val homeCode = homeCurrencyCode.uppercase().trim()

        if (localCode == homeCode || amountLocal <= 0) {
            return ConversionResult(
                amountHome = amountLocal,
                isUnconverted = false,
                conversionRate = 1.0
            )
        }

        // 1. Try direct pair in local currency_cache table
        val directKey = "${localCode}_${homeCode}"
        val directCache = cacheDao.getCurrency(directKey, 0)
        if (directCache != null && directCache.rate > 0) {
            val rate = directCache.rate
            val converted = kotlin.math.round(amountLocal * rate * 100.0) / 100.0
            return ConversionResult(
                amountHome = converted,
                isUnconverted = false,
                conversionRate = rate
            )
        }

        // 2. Try inverse pair in local currency_cache table
        val inverseKey = "${homeCode}_${localCode}"
        val inverseCache = cacheDao.getCurrency(inverseKey, 0)
        if (inverseCache != null && inverseCache.rate > 0) {
            val rate = 1.0 / inverseCache.rate
            val converted = kotlin.math.round(amountLocal * rate * 100.0) / 100.0
            return ConversionResult(
                amountHome = converted,
                isUnconverted = false,
                conversionRate = rate
            )
        }

        // 3. Try offline fallback cross-rate calculation using reference USD map
        val localUsdRate = usdRates[localCode]
        val homeUsdRate = usdRates[homeCode]
        if (localUsdRate != null && homeUsdRate != null && localUsdRate > 0) {
            // (Home units / USD) / (Local units / USD) = Home units / Local unit
            val rate = homeUsdRate / localUsdRate
            val converted = kotlin.math.round(amountLocal * rate * 100.0) / 100.0
            return ConversionResult(
                amountHome = converted,
                isUnconverted = false,
                conversionRate = rate
            )
        }

        // 4. Offline Fallback: No rate available
        return ConversionResult(
            amountHome = amountLocal,
            isUnconverted = true,
            conversionRate = null
        )
    }
}
