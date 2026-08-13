package com.example.nomadcompass.domain.model

data class CurrencyRate(
    val baseCurrency: String,
    val targetCurrency: String,
    val rate: Double,
)
