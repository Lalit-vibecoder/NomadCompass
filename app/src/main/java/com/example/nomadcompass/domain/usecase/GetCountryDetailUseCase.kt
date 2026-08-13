package com.example.nomadcompass.domain.usecase

import com.example.nomadcompass.domain.model.Advisory
import com.example.nomadcompass.domain.model.Country
import com.example.nomadcompass.domain.model.CurrencyRate
import com.example.nomadcompass.domain.model.Holiday
import com.example.nomadcompass.domain.model.Weather
import com.example.nomadcompass.domain.repository.AdvisoryRepository
import com.example.nomadcompass.domain.repository.CountryRepository
import com.example.nomadcompass.domain.repository.CurrencyRepository
import com.example.nomadcompass.domain.repository.HolidayRepository
import com.example.nomadcompass.domain.repository.WeatherRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.util.Calendar
import javax.inject.Inject

data class CountryDetailResult(
    val country: Country,
    val weather: Weather?,
    val currencyRate: CurrencyRate?,
    val advisory: Advisory?,
    val holidays: List<Holiday>,
    val nci: NciResult,
    val neighbors: List<Country>,
)

class GetCountryDetailUseCase @Inject constructor(
    private val countryRepository: CountryRepository,
    private val weatherRepository: WeatherRepository,
    private val currencyRepository: CurrencyRepository,
    private val advisoryRepository: AdvisoryRepository,
    private val holidayRepository: HolidayRepository,
    private val calculateNciUseCase: CalculateNciUseCase,
) {
    suspend operator fun invoke(cca3: String, baseCurrency: String = "USD"): CountryDetailResult? {
        val country = countryRepository.getCountryByCode(cca3) ?: return null

        return coroutineScope {
            val weatherDeferred = async { weatherRepository.getWeather(country.latitude, country.longitude) }
            val currencyRateDeferred = async { currencyRepository.getRate(baseCurrency, country.currencyCode) }
            val advisoryDeferred = async { advisoryRepository.getAdvisory(country.cca3) }
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            val holidaysDeferred = async { holidayRepository.getHolidays(country.cca3, currentYear) }

            val weather = weatherDeferred.await()
            val currencyRate = currencyRateDeferred.await()
            val advisory = advisoryDeferred.await()
            val holidays = holidaysDeferred.await()
            val nci = calculateNciUseCase(weather, advisory)

            val neighbors = country.borders.mapNotNull { borderCode ->
                countryRepository.getCountryByCode(borderCode)
            }

            CountryDetailResult(
                country = country,
                weather = weather,
                currencyRate = currencyRate,
                advisory = advisory,
                holidays = holidays,
                nci = nci,
                neighbors = neighbors,
            )
        }
    }
}
