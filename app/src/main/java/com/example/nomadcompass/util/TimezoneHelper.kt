package com.example.nomadcompass.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt

/**
 * Utility for accurate timezone offset and local time resolution for any destination.
 */
object TimezoneHelper {

    data class TimezoneInfo(
        val utcDisplay: String,
        val localTimeDisplay: String,
    )

    private val countryZoneMap = mapOf(
        "AFG" to "Asia/Kabul",
        "ALB" to "Europe/Tirane",
        "DZA" to "Africa/Algiers",
        "AND" to "Europe/Andorra",
        "AGO" to "Africa/Luanda",
        "ARG" to "America/Argentina/Buenos_Aires",
        "ARM" to "Asia/Yerevan",
        "AUS" to "Australia/Sydney",
        "AUT" to "Europe/Vienna",
        "AZE" to "Asia/Baku",
        "BHS" to "America/Nassau",
        "BHR" to "Asia/Bahrain",
        "BGD" to "Asia/Dhaka",
        "BRB" to "America/Barbados",
        "BLR" to "Europe/Minsk",
        "BEL" to "Europe/Brussels",
        "BLZ" to "America/Belize",
        "BEN" to "Africa/Porto-Novo",
        "BTN" to "Asia/Thimphu",
        "BOL" to "America/La_Paz",
        "BIH" to "Europe/Sarajevo",
        "BWA" to "Africa/Gaborone",
        "BRA" to "America/Sao_Paulo",
        "BRN" to "Asia/Brunei",
        "BGR" to "Europe/Sofia",
        "BFA" to "Africa/Ouagadougou",
        "BDI" to "Africa/Bujumbura",
        "KHM" to "Asia/Phnom_Penh",
        "CMR" to "Africa/Douala",
        "CAN" to "America/Toronto",
        "CPV" to "Atlantic/Cape_Verde",
        "CAF" to "Africa/Bangui",
        "TCD" to "Africa/Ndjamena",
        "CHL" to "America/Santiago",
        "CHN" to "Asia/Shanghai",
        "COL" to "America/Bogota",
        "COG" to "Africa/Brazzaville",
        "COD" to "Africa/Kinshasa",
        "CRI" to "America/Costa_Rica",
        "HRV" to "Europe/Zagreb",
        "CUB" to "America/Havana",
        "CYP" to "Asia/Nicosia",
        "CZE" to "Europe/Prague",
        "DNK" to "Europe/Copenhagen",
        "DJI" to "Africa/Djibouti",
        "DOM" to "America/Santo_Domingo",
        "ECU" to "America/Guayaquil",
        "EGY" to "Africa/Cairo",
        "SLV" to "America/El_Salvador",
        "EST" to "Europe/Tallinn",
        "ETH" to "Africa/Addis_Ababa",
        "FIN" to "Europe/Helsinki",
        "FRA" to "Europe/Paris",
        "GEO" to "Asia/Tbilisi",
        "DEU" to "Europe/Berlin",
        "GHA" to "Africa/Accra",
        "GRC" to "Europe/Athens",
        "GTM" to "America/Guatemala",
        "GIN" to "Africa/Conakry",
        "HTI" to "America/Port-au-Prince",
        "HND" to "America/Tegucigalpa",
        "HKG" to "Asia/Hong_Kong",
        "HUN" to "Europe/Budapest",
        "ISL" to "Atlantic/Reykjavik",
        "IND" to "Asia/Kolkata",
        "IDN" to "Asia/Jakarta",
        "IRN" to "Asia/Tehran",
        "IRQ" to "Asia/Baghdad",
        "IRL" to "Europe/Dublin",
        "ISR" to "Asia/Jerusalem",
        "ITA" to "Europe/Rome",
        "JAM" to "America/Jamaica",
        "JPN" to "Asia/Tokyo",
        "JOR" to "Asia/Amman",
        "KAZ" to "Asia/Almaty",
        "KEN" to "Africa/Nairobi",
        "KOR" to "Asia/Seoul",
        "KWT" to "Asia/Kuwait",
        "KGZ" to "Asia/Bishkek",
        "LAO" to "Asia/Vientiane",
        "LVA" to "Europe/Riga",
        "LBN" to "Asia/Beirut",
        "LBY" to "Africa/Tripoli",
        "LTU" to "Europe/Vilnius",
        "LUX" to "Europe/Luxembourg",
        "MAC" to "Asia/Macau",
        "MKD" to "Europe/Skopje",
        "MDG" to "Indian/Antananarivo",
        "MYS" to "Asia/Kuala_Lumpur",
        "MLI" to "Africa/Bamako",
        "MLT" to "Europe/Malta",
        "MEX" to "America/Mexico_City",
        "MDA" to "Europe/Chisinau",
        "MCO" to "Europe/Monaco",
        "MNG" to "Asia/Ulaanbaatar",
        "MNE" to "Europe/Podgorica",
        "MAR" to "Africa/Casablanca",
        "MOZ" to "Africa/Maputo",
        "MMR" to "Asia/Yangon",
        "NPL" to "Asia/Kathmandu",
        "NLD" to "Europe/Amsterdam",
        "NZL" to "Pacific/Auckland",
        "NIC" to "America/Managua",
        "NGA" to "Africa/Lagos",
        "NOR" to "Europe/Oslo",
        "OMN" to "Asia/Muscat",
        "PAK" to "Asia/Karachi",
        "PAN" to "America/Panama",
        "PRY" to "America/Asuncion",
        "PER" to "America/Lima",
        "PHL" to "Asia/Manila",
        "POL" to "Europe/Warsaw",
        "PRT" to "Europe/Lisbon",
        "PRI" to "America/Puerto_Rico",
        "QAT" to "Asia/Qatar",
        "ROU" to "Europe/Bucharest",
        "RUS" to "Europe/Moscow",
        "RWA" to "Africa/Kigali",
        "SAU" to "Asia/Riyadh",
        "SEN" to "Africa/Dakar",
        "SRB" to "Europe/Belgrade",
        "SGP" to "Asia/Singapore",
        "SVK" to "Europe/Bratislava",
        "SVN" to "Europe/Ljubljana",
        "ZAF" to "Africa/Johannesburg",
        "ESP" to "Europe/Madrid",
        "LKA" to "Asia/Colombo",
        "SWE" to "Europe/Stockholm",
        "CHE" to "Europe/Zurich",
        "TWN" to "Asia/Taipei",
        "TZA" to "Africa/Dar_es_Salaam",
        "THA" to "Asia/Bangkok",
        "TUN" to "Africa/Tunis",
        "TUR" to "Europe/Istanbul",
        "UGA" to "Africa/Kampala",
        "UKR" to "Europe/Kyiv",
        "ARE" to "Asia/Dubai",
        "GBR" to "Europe/London",
        "USA" to "America/New_York",
        "URY" to "America/Montevideo",
        "UZB" to "Asia/Tashkent",
        "VEN" to "America/Caracas",
        "VNM" to "Asia/Ho_Chi_Minh",
        "ZMB" to "Africa/Lusaka",
        "ZWE" to "Africa/Harare"
    )

    fun getTimezoneInfo(cca3: String, cca2: String = "", longitude: Double = 0.0): TimezoneInfo {
        val zoneId = countryZoneMap[cca3.uppercase()]
            ?: countryZoneMap[cca2.uppercase()]
            ?: getFallbackZoneId(longitude)

        val timeZone = TimeZone.getTimeZone(zoneId)

        val now = Date()
        val offsetMillis = timeZone.getOffset(now.time)
        val offsetMinutesTotal = offsetMillis / (1000 * 60)
        val offsetHours = offsetMinutesTotal / 60
        val offsetMins = Math.abs(offsetMinutesTotal % 60)

        val utcDisplay = when {
            offsetMinutesTotal == 0 -> "UTC +0"
            offsetMins == 0 && offsetHours > 0 -> "UTC +$offsetHours"
            offsetMins == 0 && offsetHours < 0 -> "UTC $offsetHours"
            offsetHours >= 0 -> "UTC +$offsetHours:${String.format(Locale.US, "%02d", offsetMins)}"
            else -> "UTC $offsetHours:${String.format(Locale.US, "%02d", offsetMins)}"
        }

        val timeFormat = SimpleDateFormat("HH:mm", Locale.US)
        timeFormat.timeZone = timeZone
        val localTimeDisplay = "${timeFormat.format(now)} (Local)"

        return TimezoneInfo(
            utcDisplay = utcDisplay,
            localTimeDisplay = localTimeDisplay
        )
    }

    private fun getFallbackZoneId(longitude: Double): String {
        val offsetHours = (longitude / 15.0).roundToInt().coerceIn(-12, 14)
        val sign = if (offsetHours >= 0) "+" else "-"
        val absHours = Math.abs(offsetHours)
        return String.format(Locale.US, "GMT%s%02d:00", sign, absHours)
    }
}
