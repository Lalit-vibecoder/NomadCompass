package com.example.nomadcompass.data.repository

import com.example.nomadcompass.data.local.dao.CacheDao
import com.example.nomadcompass.data.local.entity.AdvisoryCacheEntity
import com.example.nomadcompass.data.local.entity.CurrencyCacheEntity
import com.example.nomadcompass.data.local.entity.HolidayCacheEntity
import com.example.nomadcompass.data.local.entity.WeatherCacheEntity
import com.example.nomadcompass.data.remote.api.ExchangeRateApi
import com.example.nomadcompass.data.remote.api.FrankfurterApi
import com.example.nomadcompass.data.remote.dto.ExchangeRateResponse
import com.example.nomadcompass.data.remote.dto.FrankfurterResponse
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class FakeCacheDao : CacheDao {
    private val cache = mutableMapOf<String, CurrencyCacheEntity>()

    override suspend fun getCurrency(key: String, minTimestamp: Long): CurrencyCacheEntity? {
        val item = cache[key] ?: return null
        return if (item.cachedAt >= minTimestamp) item else null
    }

    override suspend fun insertCurrency(entity: CurrencyCacheEntity) {
        cache[entity.key] = entity
    }

    override suspend fun getWeather(key: String, minTimestamp: Long): WeatherCacheEntity? = null
    override suspend fun insertWeather(entity: WeatherCacheEntity) {}
    override suspend fun getAdvisory(code: String, minTimestamp: Long): AdvisoryCacheEntity? = null
    override suspend fun insertAdvisory(entity: AdvisoryCacheEntity) {}
    override suspend fun getHolidays(code: String, year: Int, minTimestamp: Long): List<HolidayCacheEntity> = emptyList()
    override suspend fun insertHolidays(entities: List<HolidayCacheEntity>) {}
    override suspend fun clearHolidays(code: String, year: Int) {}
}

class FakeFrankfurterApi(var shouldThrow: Boolean = false, var customResponse: FrankfurterResponse? = null) : FrankfurterApi {
    override suspend fun getLatestRates(base: String): FrankfurterResponse {
        if (shouldThrow) throw RuntimeException("API Error")
        return customResponse ?: FrankfurterResponse(base = base, rates = mapOf("EUR" to 0.92, "JPY" to 155.0))
    }
}

class FakeExchangeRateApi(var shouldThrow: Boolean = false, var customResponse: ExchangeRateResponse? = null) : ExchangeRateApi {
    override suspend fun getLatestRates(base: String): ExchangeRateResponse {
        if (shouldThrow) throw RuntimeException("Exchange Rate API Error")
        return customResponse ?: ExchangeRateResponse(result = "success", baseCode = base, rates = mapOf("EUR" to 0.92, "INR" to 83.5, "JPY" to 155.0))
    }
}

class CurrencyRepositoryTest {

    @Test
    fun testSameCurrencyReturnsOne() = runBlocking {
        val repo = CurrencyRepositoryImpl(FakeCacheDao(), FakeFrankfurterApi(), FakeExchangeRateApi())
        val rate = repo.getRate("USD", "USD")
        assertNotNull(rate)
        assertEquals(1.0, rate!!.rate, 0.001)
    }

    @Test
    fun testExchangeRateApiSuccess() = runBlocking {
        val fakeApi = FakeExchangeRateApi(
            customResponse = ExchangeRateResponse("success", "USD", rates = mapOf("EUR" to 0.95))
        )
        val repo = CurrencyRepositoryImpl(FakeCacheDao(), FakeFrankfurterApi(), fakeApi)
        val rate = repo.getRate("USD", "EUR")
        assertNotNull(rate)
        assertEquals(0.95, rate!!.rate, 0.001)
    }

    @Test
    fun testForceRefreshBypassesCache() = runBlocking {
        val cache = FakeCacheDao()
        cache.insertCurrency(CurrencyCacheEntity("USD_EUR", 0.88, System.currentTimeMillis()))

        val fakeApi = FakeExchangeRateApi(
            customResponse = ExchangeRateResponse("success", "USD", rates = mapOf("EUR" to 0.93))
        )
        val repo = CurrencyRepositoryImpl(cache, FakeFrankfurterApi(), fakeApi)

        // Without force refresh -> cached rate (0.88)
        val cachedRate = repo.getRate("USD", "EUR", forceRefresh = false)
        assertEquals(0.88, cachedRate!!.rate, 0.001)

        // With force refresh -> new live rate (0.93)
        val freshRate = repo.getRate("USD", "EUR", forceRefresh = true)
        assertEquals(0.93, freshRate!!.rate, 0.001)
    }

    @Test
    fun testOfflineFallbackCrossRate() = runBlocking {
        val fakeFrankfurter = FakeFrankfurterApi(shouldThrow = true)
        val fakeExchange = FakeExchangeRateApi(shouldThrow = true)
        val repo = CurrencyRepositoryImpl(FakeCacheDao(), fakeFrankfurter, fakeExchange)

        // USD -> EUR fallback (0.92)
        val usdEur = repo.getRate("USD", "EUR")
        assertNotNull(usdEur)
        assertEquals(0.92, usdEur!!.rate, 0.01)

        // EUR -> JPY fallback (155.0 / 0.92 ≈ 168.478)
        val eurJpy = repo.getRate("EUR", "JPY")
        assertNotNull(eurJpy)
        assertEquals(168.478, eurJpy!!.rate, 0.1)
    }
}
