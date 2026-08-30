package com.example.nomadcompass.di

import android.content.Context
import androidx.room.Room
import com.example.nomadcompass.data.local.NomadDatabase
import com.example.nomadcompass.data.local.dao.CacheDao
import com.example.nomadcompass.data.local.dao.CountryDao
import com.example.nomadcompass.data.local.dao.ExpenseDao
import com.example.nomadcompass.data.local.dao.ItineraryEventDao
import com.example.nomadcompass.data.local.dao.PackingItemDao
import com.example.nomadcompass.data.local.dao.TripAttachmentDao
import com.example.nomadcompass.data.local.dao.TripDao
import com.example.nomadcompass.data.local.dao.UserProfileDao
import com.example.nomadcompass.data.remote.api.ExchangeRateApi
import com.example.nomadcompass.data.remote.api.FrankfurterApi
import com.example.nomadcompass.data.remote.api.NagerDateApi
import com.example.nomadcompass.data.remote.api.OpenMeteoApi
import com.example.nomadcompass.data.remote.api.RestCountriesApi
import com.example.nomadcompass.data.remote.api.TravelAdvisoryApi
import com.example.nomadcompass.data.repository.AdvisoryRepositoryImpl
import com.example.nomadcompass.data.repository.CountryRepositoryImpl
import com.example.nomadcompass.data.repository.CurrencyRepositoryImpl
import com.example.nomadcompass.data.repository.ExpenseRepositoryImpl
import com.example.nomadcompass.data.repository.HolidayRepositoryImpl
import com.example.nomadcompass.data.repository.ItineraryRepositoryImpl
import com.example.nomadcompass.data.repository.PackingRepositoryImpl
import com.example.nomadcompass.data.repository.ProfileRepositoryImpl
import com.example.nomadcompass.data.repository.TripRepositoryImpl
import com.example.nomadcompass.data.repository.WeatherRepositoryImpl
import com.example.nomadcompass.domain.repository.AdvisoryRepository
import com.example.nomadcompass.domain.repository.CountryRepository
import com.example.nomadcompass.domain.repository.CurrencyRepository
import com.example.nomadcompass.domain.repository.ExpenseRepository
import com.example.nomadcompass.domain.repository.HolidayRepository
import com.example.nomadcompass.domain.repository.ItineraryRepository
import com.example.nomadcompass.domain.repository.PackingRepository
import com.example.nomadcompass.domain.repository.ProfileRepository
import com.example.nomadcompass.domain.repository.TripRepository
import com.example.nomadcompass.domain.repository.WeatherRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(4, TimeUnit.SECONDS)
        .readTimeout(4, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NomadDatabase =
        Room.databaseBuilder(
            context,
            NomadDatabase::class.java,
            "nomad_compass.db"
        ).fallbackToDestructiveMigration().build()

    @Provides
    fun provideCountryDao(db: NomadDatabase): CountryDao = db.countryDao()

    @Provides
    fun provideUserProfileDao(db: NomadDatabase): UserProfileDao = db.userProfileDao()

    @Provides
    fun provideCacheDao(db: NomadDatabase): CacheDao = db.cacheDao()

    @Provides
    fun provideTripDao(db: NomadDatabase): TripDao = db.tripDao()

    @Provides
    fun provideTripAttachmentDao(db: NomadDatabase): TripAttachmentDao = db.tripAttachmentDao()

    @Provides
    fun provideExpenseDao(db: NomadDatabase): ExpenseDao = db.expenseDao()

    @Provides
    fun providePackingItemDao(db: NomadDatabase): PackingItemDao = db.packingItemDao()

    @Provides
    fun provideItineraryEventDao(db: NomadDatabase): ItineraryEventDao = db.itineraryEventDao()

    @Provides
    @Singleton
    fun provideRestCountriesApi(okHttpClient: OkHttpClient, moshi: Moshi): RestCountriesApi =
        Retrofit.Builder()
            .baseUrl("https://restcountries.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(RestCountriesApi::class.java)

    @Provides
    @Singleton
    fun provideOpenMeteoApi(okHttpClient: OkHttpClient, moshi: Moshi): OpenMeteoApi =
        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(OpenMeteoApi::class.java)

    @Provides
    @Singleton
    fun provideExchangeRateApi(okHttpClient: OkHttpClient, moshi: Moshi): ExchangeRateApi =
        Retrofit.Builder()
            .baseUrl("https://open.er-api.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ExchangeRateApi::class.java)

    @Provides
    @Singleton
    fun provideFrankfurterApi(okHttpClient: OkHttpClient, moshi: Moshi): FrankfurterApi =
        Retrofit.Builder()
            .baseUrl("https://api.frankfurter.dev/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(FrankfurterApi::class.java)

    @Provides
    @Singleton
    fun provideNagerDateApi(okHttpClient: OkHttpClient, moshi: Moshi): NagerDateApi =
        Retrofit.Builder()
            .baseUrl("https://date.nager.at/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(NagerDateApi::class.java)

    @Provides
    @Singleton
    fun provideTravelAdvisoryApi(okHttpClient: OkHttpClient, moshi: Moshi): TravelAdvisoryApi =
        Retrofit.Builder()
            .baseUrl("https://api.travel-advisory.info/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(TravelAdvisoryApi::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCountryRepository(impl: CountryRepositoryImpl): CountryRepository

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(impl: WeatherRepositoryImpl): WeatherRepository

    @Binds
    @Singleton
    abstract fun bindCurrencyRepository(impl: CurrencyRepositoryImpl): CurrencyRepository

    @Binds
    @Singleton
    abstract fun bindAdvisoryRepository(impl: AdvisoryRepositoryImpl): AdvisoryRepository

    @Binds
    @Singleton
    abstract fun bindHolidayRepository(impl: HolidayRepositoryImpl): HolidayRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindTripRepository(impl: TripRepositoryImpl): TripRepository

    @Binds
    @Singleton
    abstract fun bindExpenseRepository(impl: ExpenseRepositoryImpl): ExpenseRepository

    @Binds
    @Singleton
    abstract fun bindPackingRepository(impl: PackingRepositoryImpl): PackingRepository

    @Binds
    @Singleton
    abstract fun bindItineraryRepository(impl: ItineraryRepositoryImpl): ItineraryRepository
}
