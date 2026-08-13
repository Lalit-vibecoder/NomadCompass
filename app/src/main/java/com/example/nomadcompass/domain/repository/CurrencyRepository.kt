package com.example.nomadcompass.domain.repository

import com.example.nomadcompass.domain.model.CurrencyRate

interface CurrencyRepository {
    suspend fun getRate(baseCurrency: String, targetCurrency: String, forceRefresh: Boolean = false): CurrencyRate?
}
