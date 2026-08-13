package com.example.nomadcompass.domain.repository

import com.example.nomadcompass.domain.model.Holiday

interface HolidayRepository {
    suspend fun getHolidays(countryCode: String, year: Int): List<Holiday>
}
