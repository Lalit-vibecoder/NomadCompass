package com.example.nomadcompass.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nomadcompass.data.local.entity.AdvisoryCacheEntity
import com.example.nomadcompass.data.local.entity.CurrencyCacheEntity
import com.example.nomadcompass.data.local.entity.HolidayCacheEntity
import com.example.nomadcompass.data.local.entity.WeatherCacheEntity

@Dao
interface CacheDao {

    // Weather
    @Query("SELECT * FROM weather_cache WHERE `key` = :key AND cachedAt > :minTimestamp LIMIT 1")
    suspend fun getWeather(key: String, minTimestamp: Long): WeatherCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(entity: WeatherCacheEntity)

    // Currency
    @Query("SELECT * FROM currency_cache WHERE `key` = :key AND cachedAt > :minTimestamp LIMIT 1")
    suspend fun getCurrency(key: String, minTimestamp: Long): CurrencyCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrency(entity: CurrencyCacheEntity)

    // Advisory
    @Query("SELECT * FROM advisory_cache WHERE countryCode = :code AND cachedAt > :minTimestamp LIMIT 1")
    suspend fun getAdvisory(code: String, minTimestamp: Long): AdvisoryCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdvisory(entity: AdvisoryCacheEntity)

    // Holidays
    @Query("SELECT * FROM holiday_cache WHERE countryCode = :code AND year = :year AND cachedAt > :minTimestamp")
    suspend fun getHolidays(code: String, year: Int, minTimestamp: Long): List<HolidayCacheEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHolidays(entities: List<HolidayCacheEntity>)

    @Query("DELETE FROM holiday_cache WHERE countryCode = :code AND year = :year")
    suspend fun clearHolidays(code: String, year: Int)
}
