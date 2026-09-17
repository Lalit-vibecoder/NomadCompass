package com.example.nomadcompass.util

import java.util.Locale
import kotlin.math.abs

/**
 * Standardized currency formatting utility for NomadCompass.
 * Matches selected base currency symbols dynamically (e.g. ₹, ฿, $, €, £, ¥)
 * with consistent decimal formatting (%,.2f).
 */
object CurrencyFormatter {

    fun getSymbol(currencyCode: String): String {
        return when (currencyCode.uppercase().trim()) {
            "USD" -> "$"
            "EUR" -> "€"
            "GBP" -> "£"
            "JPY" -> "¥"
            "INR" -> "₹"
            "THB" -> "฿"
            "AUD" -> "A$"
            "CAD" -> "C$"
            "SGD" -> "S$"
            "CHF" -> "CHF "
            "BRL" -> "R$"
            "AED" -> "AED "
            "IDR" -> "Rp "
            "VND" -> "₫"
            "PHP" -> "₱"
            "MYR" -> "RM "
            "KRW" -> "₩"
            "CNY" -> "¥"
            "TRY" -> "₺"
            "MXN" -> "Mex$"
            "NZD" -> "NZ$"
            "SEK" -> "kr "
            "NOK" -> "kr "
            "DKK" -> "kr "
            "PLN" -> "zł "
            "HUF" -> "Ft "
            "CZK" -> "Kč "
            "ILS" -> "₪"
            "ZAR" -> "R "
            else -> if (currencyCode.isNotBlank()) "$currencyCode " else "$ "
        }
    }

    /**
     * Formats an amount with the currency symbol and 2 decimal places (e.g. "$1,250.00", "₹500.50").
     */
    fun formatAmount(amount: Double, currencyCode: String): String {
        val symbol = getSymbol(currencyCode)
        val absFormatted = String.format(Locale.US, "%,.2f", abs(amount))
        return if (amount < 0) "-$symbol$absFormatted" else "$symbol$absFormatted"
    }

    /**
     * Formats an exchange rate cleanly (no decimals if >= 100, 2 decimals otherwise).
     */
    fun formatRate(rate: Double, currencyCode: String): String {
        val symbol = getSymbol(currencyCode)
        val formatted = if (rate >= 100) {
            String.format(Locale.US, "%,.0f", rate)
        } else {
            String.format(Locale.US, "%,.2f", rate)
        }
        return "$symbol$formatted"
    }
}
