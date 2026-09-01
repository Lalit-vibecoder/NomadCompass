package com.example.nomadcompass.util.extractor

import com.example.nomadcompass.domain.model.DraftItineraryEvent
import com.example.nomadcompass.domain.model.ItineraryCategory
import com.google.mlkit.nl.entityextraction.EntityAnnotation
import com.google.mlkit.nl.entityextraction.EntityExtraction
import com.google.mlkit.nl.entityextraction.EntityExtractionParams
import com.google.mlkit.nl.entityextraction.EntityExtractorOptions
import com.google.mlkit.nl.entityextraction.FlightNumberEntity
import com.google.mlkit.nl.entityextraction.DateTimeEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

/**
 * 100% On-Device Travel Entity Parser.
 * Uses a combined ML Kit Entity Extraction and comprehensive Travel Domain RegEx Heuristics Engine
 * to parse dates, times, flight codes, hotels, activities, and PNRs from unformatted documents.
 */
object OfflineTravelHeuristicParser {

    // ── Regex Pattern Library ──

    // IATA/ICAO Airline Code + Flight Number (e.g. SQ 321, AA 100, EK 202, 6E 2341, BA 1452)
    private val FLIGHT_CODE_REGEX = Pattern.compile(
        "\\b([A-Z]{2}|[A-Z]\\d|\\d[A-Z])\\s?([0-9]{1,4})\\b",
        Pattern.CASE_INSENSITIVE
    )

    // Airport Code Routing (e.g. SIN -> LHR, JFK - CDG, DPS to SYD)
    private val ROUTE_REGEX = Pattern.compile(
        "\\b([A-Z]{3})\\s*(?:->|–|-|to|→|—)\\s*([A-Z]{3})\\b",
        Pattern.CASE_INSENSITIVE
    )

    // PNR & Confirmation Code Patterns (e.g., PNR: 7J9KL2, Booking Ref #ABCD12, Confirmation: 8493021)
    private val CONFIRMATION_CODE_REGEX = Pattern.compile(
        "\\b(?:PNR|Booking(?:\\s+Ref|\\s+Reference|\\s+Code|\\s+Number|\\s+#)?|Confirmation(?:\\s+Code|\\s+Number|\\s+#)?|Reservation(?:\\s+Code|\\s+Number|\\s+#)?|E-Ticket|Ticket(?:\\s+Number|\\s+#)?|Record\\s+Locator)[:\\s#=]*([A-Z0-9]{5,10})\\b",
        Pattern.CASE_INSENSITIVE
    )

    // Standard Formatted Dates (YYYY-MM-DD, DD/MM/YYYY, MM/DD/YYYY, DD.MM.YYYY)
    private val NUMERIC_DATE_REGEX = Pattern.compile(
        "\\b((?:20[2-3][0-9])[-/.](?:0?[1-9]|1[0-2])[-/.](?:0?[1-9]|[12][0-9]|3[01]))\\b|" +
        "\\b((?:0?[1-9]|[12][0-9]|3[01])[-/.](?:0?[1-9]|1[0-2])[-/.](?:20[2-3][0-9]))\\b"
    )

    // Textual Dates (e.g., 15 Oct 2026, October 15, 2026, 15th October 2026)
    private val TEXT_DATE_REGEX = Pattern.compile(
        "\\b(?:(?:0?[1-9]|[12][0-9]|3[01])(?:st|nd|rd|th)?\\s+(?:Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:tember)?|Oct(?:ober)?|Nov(?:ember)?|Dec(?:ember)?)(?:[\\s,]+(?:20[2-3][0-9]))?)\\b|" +
        "\\b(?:(?:Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:tember)?|Oct(?:ober)?|Nov(?:ember)?|Dec(?:ember)?)\\s+(?:0?[1-9]|[12][0-9]|3[01])(?:st|nd|rd|th)?(?:[\\s,]+(?:20[2-3][0-9]))?)\\b",
        Pattern.CASE_INSENSITIVE
    )

    // Times (e.g. 10:30 AM, 22:45, 08:00pm)
    private val TIME_REGEX = Pattern.compile(
        "\\b((?:[01]?[0-9]|2[0-3]):[0-5][0-9](?:\\s*(?:AM|PM|am|pm))?)\\b"
    )

    // Accommodation Keywords
    private val HOTEL_KEYWORDS = listOf(
        "hotel", "resort", "hostel", "airbnb", "villa", "suites", "inn", "guest house",
        "homestay", "lodge", "motel", "hyatt", "marriott", "hilton", "radisson",
        "booking.com", "agoda", "check-in", "check in", "check-out", "check out"
    )

    // Transport Keywords
    private val TRANSPORT_KEYWORDS = listOf(
        "train", "rail", "eurostar", "shinkansen", "tgv", "amtrak", "bus", "flixbus",
        "greyhound", "ferry", "car rental", "hertz", "avis", "sixt", "enterprise",
        "uber", "grab", "taxi", "shuttle"
    )

    // Activity Keywords
    private val ACTIVITY_KEYWORDS = listOf(
        "tour", "excursion", "museum", "safari", "diving", "scuba", "snorkel", "hike",
        "trek", "temple", "palace", "entry ticket", "pass", "coworking", "hub",
        "conference", "summit", "meetup", "workshop", "festival"
    )

    // Dining Keywords
    private val DINING_KEYWORDS = listOf(
        "restaurant", "dinner", "lunch", "breakfast", "brunch", "cafe", "bistro",
        "table reservation", "tasting menu", "reservation for"
    )

    /**
     * Parses raw extracted document text into a list of structured Draft Itinerary Events.
     */
    suspend fun parse(rawText: String, defaultTripDestination: String = ""): List<DraftItineraryEvent> = withContext(Dispatchers.Default) {
        if (rawText.isBlank()) return@withContext emptyList()

        val results = mutableListOf<DraftItineraryEvent>()

        // Step 1: Run ML Kit Entity Extraction if available
        val mlKitEntities = tryExtractWithMlKit(rawText)

        // Step 2: Split text into paragraphs/sections to isolate individual itinerary items
        val sections = splitIntoLogicalSections(rawText)

        for (section in sections) {
            val event = parseSection(section, defaultTripDestination)
            if (event != null) {
                // Enhance event with ML Kit annotations if matching
                enrichWithMlKit(event, mlKitEntities)
                results.add(event)
            }
        }

        // Fallback: If section splitter found no discrete items, parse entire text as a single event
        if (results.isEmpty()) {
            val fallbackEvent = parseSection(rawText, defaultTripDestination)
            if (fallbackEvent != null) {
                enrichWithMlKit(fallbackEvent, mlKitEntities)
                results.add(fallbackEvent)
            } else {
                // Generic draft event with extracted dates
                val dates = extractDates(rawText)
                val times = extractTimes(rawText)
                val pnr = extractConfirmationCode(rawText)

                results.add(
                    DraftItineraryEvent(
                        title = if (defaultTripDestination.isNotBlank()) "Travel Leg: $defaultTripDestination" else "Travel Itinerary Event",
                        category = ItineraryCategory.OTHER,
                        location = defaultTripDestination,
                        startDateTime = formatDateTime(dates.firstOrNull() ?: getCurrentDate(), times.firstOrNull() ?: ""),
                        endDateTime = if (dates.size > 1 || times.size > 1) formatDateTime(dates.getOrNull(1) ?: (dates.firstOrNull() ?: ""), times.getOrNull(1) ?: "") else "",
                        confirmationCode = pnr,
                        notes = rawText.take(200),
                        rawSnippet = rawText.take(400)
                    )
                )
            }
        }

        // Deduplicate and return sorted by startDateTime
        results.distinctBy { "${it.title}_${it.startDateTime}_${it.confirmationCode}" }
            .sortedBy { it.startDateTime }
    }

    private fun parseSection(section: String, defaultDestination: String): DraftItineraryEvent? {
        val trimmed = section.trim()
        if (trimmed.length < 10) return null

        val lower = trimmed.lowercase(Locale.US)

        // 1. Detect Category
        val category = determineCategory(lower)

        // 2. Extract Title
        val flightCode = extractFlightCode(trimmed)
        val route = extractRoute(trimmed)
        val pnr = extractConfirmationCode(trimmed)
        val dates = extractDates(trimmed)
        val times = extractTimes(trimmed)
        val location = extractLocation(trimmed, defaultDestination)

        val title = when (category) {
            ItineraryCategory.FLIGHT -> {
                when {
                    flightCode.isNotBlank() && route.isNotBlank() -> "Flight $flightCode: $route"
                    flightCode.isNotBlank() -> "Flight $flightCode"
                    route.isNotBlank() -> "Flight to $route"
                    else -> "Flight Booking"
                }
            }
            ItineraryCategory.ACCOMMODATION -> {
                val hotelName = extractNamedEntity(trimmed, HOTEL_KEYWORDS)
                if (hotelName.isNotBlank()) "Stay at $hotelName" else "Hotel Accommodation"
            }
            ItineraryCategory.TRANSPORT -> {
                val transportName = extractNamedEntity(trimmed, TRANSPORT_KEYWORDS)
                if (transportName.isNotBlank()) "$transportName Transport" else "Ground Transport"
            }
            ItineraryCategory.ACTIVITY -> {
                val activityName = extractNamedEntity(trimmed, ACTIVITY_KEYWORDS)
                if (activityName.isNotBlank()) activityName else "Activity & Sightseeing"
            }
            ItineraryCategory.RESTAURANT -> {
                val diningName = extractNamedEntity(trimmed, DINING_KEYWORDS)
                if (diningName.isNotBlank()) "Dining at $diningName" else "Restaurant Reservation"
            }
            ItineraryCategory.OTHER -> {
                extractFirstMeaningfulLine(trimmed).ifBlank { "Itinerary Event" }
            }
        }

        val startDate = dates.firstOrNull() ?: ""
        val startTime = times.firstOrNull() ?: ""
        val endDate = dates.getOrNull(1) ?: (if (times.size > 1) startDate else "")
        val endTime = times.getOrNull(1) ?: ""

        val startDateTime = formatDateTime(startDate, startTime)
        val endDateTime = formatDateTime(endDate, endTime)

        return DraftItineraryEvent(
            title = title,
            category = category,
            location = location,
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            confirmationCode = pnr,
            notes = trimmed.take(250).replace("\n", " "),
            rawSnippet = trimmed.take(400),
            confidenceScore = calculateConfidence(category, dates.isNotEmpty(), times.isNotEmpty(), pnr.isNotEmpty())
        )
    }

    private fun determineCategory(text: String): ItineraryCategory {
        return when {
            FLIGHT_CODE_REGEX.matcher(text).find() || text.contains("flight") || text.contains("airline") || text.contains("boarding") || text.contains("terminal") || text.contains("gate") -> ItineraryCategory.FLIGHT
            HOTEL_KEYWORDS.any { text.contains(it) } -> ItineraryCategory.ACCOMMODATION
            TRANSPORT_KEYWORDS.any { text.contains(it) } -> ItineraryCategory.TRANSPORT
            ACTIVITY_KEYWORDS.any { text.contains(it) } -> ItineraryCategory.ACTIVITY
            DINING_KEYWORDS.any { text.contains(it) } -> ItineraryCategory.RESTAURANT
            else -> ItineraryCategory.OTHER
        }
    }

    private fun extractFlightCode(text: String): String {
        val matcher = FLIGHT_CODE_REGEX.matcher(text)
        while (matcher.find()) {
            val airline = matcher.group(1)?.uppercase(Locale.US) ?: ""
            val number = matcher.group(2) ?: ""
            // Filter out common false positives (like "AM 10", "PM 12", "NO 1", "ID 12")
            if (airline !in listOf("AM", "PM", "NO", "ID", "UK", "US", "OK", "OR", "IN", "AT", "TO", "ON", "BY")) {
                return "$airline $number"
            }
        }
        return ""
    }

    private fun extractRoute(text: String): String {
        val matcher = ROUTE_REGEX.matcher(text)
        if (matcher.find()) {
            val origin = matcher.group(1)?.uppercase(Locale.US) ?: ""
            val dest = matcher.group(2)?.uppercase(Locale.US) ?: ""
            return "$origin → $dest"
        }
        return ""
    }

    private fun extractConfirmationCode(text: String): String {
        val matcher = CONFIRMATION_CODE_REGEX.matcher(text)
        if (matcher.find()) {
            return matcher.group(1)?.uppercase(Locale.US) ?: ""
        }
        return ""
    }

    private fun extractDates(text: String): List<String> {
        val dates = mutableListOf<String>()

        // Check textual dates
        val textMatcher = TEXT_DATE_REGEX.matcher(text)
        while (textMatcher.find()) {
            val raw = textMatcher.group().trim()
            val normalized = normalizeDateString(raw)
            if (normalized.isNotBlank() && normalized !in dates) {
                dates.add(normalized)
            }
        }

        // Check numeric dates
        val numMatcher = NUMERIC_DATE_REGEX.matcher(text)
        while (numMatcher.find()) {
            val raw = numMatcher.group().trim()
            val normalized = normalizeNumericDateString(raw)
            if (normalized.isNotBlank() && normalized !in dates) {
                dates.add(normalized)
            }
        }

        return dates
    }

    private fun extractTimes(text: String): List<String> {
        val times = mutableListOf<String>()
        val matcher = TIME_REGEX.matcher(text)
        while (matcher.find()) {
            val raw = matcher.group(1)?.trim() ?: ""
            if (raw.isNotBlank() && raw !in times) {
                times.add(raw)
            }
        }
        return times
    }

    private fun extractLocation(text: String, defaultDest: String): String {
        val route = extractRoute(text)
        if (route.isNotBlank()) return route

        // Check for airport/city keywords
        val lines = text.split("\n", ",")
        for (line in lines) {
            val clean = line.trim()
            if (clean.contains("Airport", ignoreCase = true) ||
                clean.contains("Terminal", ignoreCase = true) ||
                clean.contains("Station", ignoreCase = true) ||
                clean.contains("Street", ignoreCase = true) ||
                clean.contains("Road", ignoreCase = true)
            ) {
                return clean.take(40)
            }
        }
        return defaultDest
    }

    private fun extractNamedEntity(text: String, keywords: List<String>): String {
        val lines = text.split("\n")
        for (line in lines) {
            val clean = line.trim()
            for (kw in keywords) {
                if (clean.contains(kw, ignoreCase = true) && clean.length in 4..60) {
                    return clean.replace(Regex("(?i)(booking confirmation|reservation details|confirmed|check-in|check-out)"), "").trim()
                }
            }
        }
        return ""
    }

    private fun extractFirstMeaningfulLine(text: String): String {
        return text.split("\n")
            .map { it.trim() }
            .firstOrNull { it.length in 5..60 && !it.contains("http", ignoreCase = true) }
            ?: ""
    }

    private fun splitIntoLogicalSections(text: String): List<String> {
        val paragraphs = text.split(Regex("(?m)^\\s*(?:Flight|Leg|Stay|Hotel|Day \\d+|Day [A-Za-z]+|Segment|Reservation|Booking)\\s*[:#-]?"))
        val filtered = paragraphs.map { it.trim() }.filter { it.length > 20 }
        return if (filtered.size > 1) filtered else text.split("\n\n").map { it.trim() }.filter { it.length > 20 }
    }

    private fun formatDateTime(date: String, time: String): String {
        return when {
            date.isNotBlank() && time.isNotBlank() -> "$date $time"
            date.isNotBlank() -> date
            time.isNotBlank() -> "${getCurrentDate()} $time"
            else -> ""
        }
    }

    private fun getCurrentDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }

    private fun normalizeDateString(raw: String): String {
        val cleaned = raw.replace(Regex("(?i)(st|nd|rd|th)"), "").trim()
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val formats = listOf(
            "dd MMM yyyy", "MMM dd yyyy", "dd MMMM yyyy", "MMMM dd yyyy",
            "dd MMM", "MMM dd", "dd MMMM", "MMMM dd"
        )
        for (fmt in formats) {
            try {
                val sdf = SimpleDateFormat(fmt, Locale.US)
                sdf.isLenient = false
                val date = sdf.parse(cleaned)
                if (date != null) {
                    val cal = Calendar.getInstance().apply { time = date }
                    if (!fmt.contains("yyyy")) {
                        cal.set(Calendar.YEAR, currentYear)
                    }
                    return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
                }
            } catch (_: Exception) { }
        }
        return raw
    }

    private fun normalizeNumericDateString(raw: String): String {
        val parts = raw.split("-", "/", ".")
        if (parts.size == 3) {
            if (parts[0].length == 4) {
                // YYYY-MM-DD
                val y = parts[0]
                val m = parts[1].padStart(2, '0')
                val d = parts[2].padStart(2, '0')
                return "$y-$m-$d"
            } else if (parts[2].length == 4) {
                // DD-MM-YYYY or MM-DD-YYYY -> format as YYYY-MM-DD
                val d = parts[0].padStart(2, '0')
                val m = parts[1].padStart(2, '0')
                val y = parts[2]
                return "$y-$m-$d"
            }
        }
        return raw
    }

    private fun calculateConfidence(category: ItineraryCategory, hasDate: Boolean, hasTime: Boolean, hasPnr: Boolean): Float {
        var score = 0.5f
        if (category != ItineraryCategory.OTHER) score += 0.2f
        if (hasDate) score += 0.15f
        if (hasTime) score += 0.05f
        if (hasPnr) score += 0.1f
        return score.coerceIn(0.1f, 1.0f)
    }

    // ── Optional ML Kit Entity Extraction On-Device Hook ──
    private suspend fun tryExtractWithMlKit(text: String): List<EntityAnnotation> {
        return try {
            val entityExtractor = EntityExtraction.getClient(
                EntityExtractorOptions.Builder(EntityExtractorOptions.ENGLISH).build()
            )
            // Attempt to check if local model is ready or available
            val isAvailable = entityExtractor.isModelDownloaded.awaitResult()
            if (isAvailable) {
                val params = EntityExtractionParams.Builder(text).build()
                entityExtractor.annotate(params).awaitResult()
            } else {
                emptyList()
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun enrichWithMlKit(event: DraftItineraryEvent, annotations: List<EntityAnnotation>) {
        for (annotation in annotations) {
            for (entity in annotation.entities) {
                when (entity) {
                    is FlightNumberEntity -> {
                        if (event.category == ItineraryCategory.OTHER) {
                            event.category = ItineraryCategory.FLIGHT
                        }
                        if (!event.title.contains(entity.flightNumber)) {
                            event.title = "Flight ${entity.airlineCode} ${entity.flightNumber}"
                        }
                    }
                    is DateTimeEntity -> {
                        if (event.startDateTime.isBlank()) {
                            try {
                                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
                                event.startDateTime = sdf.format(Date(entity.timestampMillis))
                            } catch (_: Exception) { }
                        }
                    }
                }
            }
        }
    }
}
