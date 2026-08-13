package com.example.nomadcompass.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.nomadcompass.data.local.dao.CacheDao
import com.example.nomadcompass.data.local.dao.CountryDao
import com.example.nomadcompass.data.local.dao.TripAttachmentDao
import com.example.nomadcompass.data.local.dao.TripDao
import com.example.nomadcompass.data.local.dao.UserProfileDao
import com.example.nomadcompass.data.local.entity.AdvisoryCacheEntity
import com.example.nomadcompass.data.local.entity.CountryEntity
import com.example.nomadcompass.data.local.entity.CurrencyCacheEntity
import com.example.nomadcompass.data.local.entity.HolidayCacheEntity
import com.example.nomadcompass.data.local.entity.TripAttachmentEntity
import com.example.nomadcompass.data.local.entity.TripEntity
import com.example.nomadcompass.data.local.entity.UserProfileEntity
import com.example.nomadcompass.data.local.entity.WeatherCacheEntity

@Database(
    entities = [
        CountryEntity::class,
        UserProfileEntity::class,
        WeatherCacheEntity::class,
        CurrencyCacheEntity::class,
        AdvisoryCacheEntity::class,
        HolidayCacheEntity::class,
        TripEntity::class,
        TripAttachmentEntity::class,
    ],
    version = 3,
    exportSchema = false,
)
abstract class NomadDatabase : RoomDatabase() {
    abstract fun countryDao(): CountryDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun cacheDao(): CacheDao
    abstract fun tripDao(): TripDao
    abstract fun tripAttachmentDao(): TripAttachmentDao
}

