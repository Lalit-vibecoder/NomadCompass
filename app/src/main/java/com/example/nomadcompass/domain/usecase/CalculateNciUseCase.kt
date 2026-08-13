package com.example.nomadcompass.domain.usecase

import com.example.nomadcompass.domain.model.Advisory
import com.example.nomadcompass.domain.model.Weather
import javax.inject.Inject

data class NciResult(
    val score: Int, // 1 to 10
    val summaryQuote: String,
    val isHighUv: Boolean,
)

class CalculateNciUseCase @Inject constructor() {

    operator fun invoke(weather: Weather?, advisory: Advisory?): NciResult {
        if (weather == null) {
            return NciResult(score = 8, summaryQuote = "Moderate climate with reliable local connectivity.", isHighUv = false)
        }

        var score = 10.0

        // Temperature penalty (ideal 18°C to 28°C)
        val temp = weather.temperatureCelsius
        if (temp < 10 || temp > 35) {
            score -= 2.5
        } else if (temp < 18 || temp > 28) {
            score -= 1.0
        }

        // Wind penalty (> 30 km/h)
        if (weather.windSpeedKmh > 40) {
            score -= 1.5
        } else if (weather.windSpeedKmh > 25) {
            score -= 0.5
        }

        // UV penalty (high UV >= 6)
        val isHighUv = weather.uvIndex >= 6.0
        if (isHighUv) {
            score -= 0.5
        }

        // Advisory penalty (score > 3.0 means higher risk)
        val advisoryScore = advisory?.score ?: 1.0
        if (advisoryScore > 3.5) {
            score -= 2.0
        } else if (advisoryScore > 2.5) {
            score -= 1.0
        }

        val finalScore = score.coerceIn(1.0, 10.0).toInt()

        val quote = when {
            finalScore >= 9 -> "Extremely high safety, ultra-fast fiber internet, and unmatched public transport efficiency."
            finalScore >= 7 -> "Great balance of warm weather, vibrant digital nomad community, and affordable cost of living."
            finalScore >= 5 -> "Decent nomad base with modern amenities, though seasonal weather variations apply."
            else -> "Use caution: extreme weather conditions or heightened safety advisories currently reported."
        }

        return NciResult(
            score = finalScore,
            summaryQuote = quote,
            isHighUv = isHighUv,
        )
    }
}
