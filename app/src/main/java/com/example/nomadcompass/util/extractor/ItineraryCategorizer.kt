package com.example.nomadcompass.util.extractor

import com.example.nomadcompass.domain.model.DraftItineraryEvent
import com.example.nomadcompass.domain.model.ItineraryCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

/**
 * Categorized text container for offline itinerary extraction review and adjustment.
 */
data class CategorizedItineraryText(
    var flightsText: String = "",
    var accommodationsText: String = "",
    var plansText: String = "",
) {
    fun toFormattedString(): String {
        val sb = StringBuilder()
        if (flightsText.isNotBlank()) {
            sb.append(HEADER_FLIGHTS).append("\n").append(flightsText.trim()).append("\n\n")
        }
        if (accommodationsText.isNotBlank()) {
            sb.append(HEADER_ACCOMMODATIONS).append("\n").append(accommodationsText.trim()).append("\n\n")
        }
        if (plansText.isNotBlank()) {
            sb.append(HEADER_PLANS).append("\n").append(plansText.trim()).append("\n")
        }
        return sb.toString().trim()
    }

    val totalItemCountEstimate: Int
        get() {
            fun countLines(text: String): Int =
                text.lines().filter { it.trim().length > 5 && !it.trim().startsWith("===") }.size
            return countLines(flightsText) + countLines(accommodationsText) + countLines(plansText)
        }

    val hasAnyContent: Boolean
        get() = flightsText.isNotBlank() || accommodationsText.isNotBlank() || plansText.isNotBlank()

    companion object {
        const val HEADER_FLIGHTS = "=== FLIGHTS ==="
        const val HEADER_ACCOMMODATIONS = "=== ACCOMMODATIONS ==="
        const val HEADER_PLANS = "=== PLANS ==="
    }
}

/**
 * 100% On-Device Itinerary Categorizer.
 * Separates raw unstructured text (from Google ML Kit OCR or PDFBox) into structured sections:
 * - Flights
 * - Accommodations
 * - Plans
 *
 * Supports two-way mapping between categorized sections and editable text fields.
 */
object ItineraryCategorizer {

    private val FLIGHT_KEYWORDS = listOf(
        "flight", "airline", "boarding", "terminal", "gate", "departure", "arrival",
        "layover", "cabin", "pnr", "ticket", "baggage", "seat", "airways", "airlines",
        "sq", "ba", "aa", "dl", "ek", "qr", "lh", "nh", "jl", "cx", "af", "kl", "qf",
        "e-ticket", "locator", "aircraft", "runway", "airbus", "boeing"
    )

    private val ACCOMMODATION_KEYWORDS = listOf(
        "hotel", "hostel", "resort", "airbnb", "villa", "suites", "inn", "guest house",
        "homestay", "lodge", "motel", "hyatt", "marriott", "hilton", "radisson", "sheraton",
        "booking.com", "agoda", "check-in", "check in", "check-out", "check out", "room",
        "bed", "nights", "reception", "property", "accommodation", "stay", "guest"
    )

    private val PLANS_KEYWORDS = listOf(
        "tour", "excursion", "museum", "safari", "diving", "scuba", "snorkel", "hike",
        "trek", "temple", "palace", "entry ticket", "pass", "coworking", "hub",
        "conference", "summit", "meetup", "workshop", "festival", "train", "rail",
        "eurostar", "shinkansen", "tgv", "amtrak", "bus", "flixbus", "greyhound", "ferry",
        "car rental", "hertz", "avis", "sixt", "enterprise", "uber", "grab", "taxi",
        "restaurant", "dinner", "lunch", "breakfast", "brunch", "cafe", "bistro",
        "table reservation", "sightseeing", "visit", "activity", "itinerary", "day trip"
    )

    /**
     * Splits raw extracted text into categorized sections (Flights, Accommodations, Plans).
     */
    fun categorizeRawText(rawText: String): CategorizedItineraryText {
        if (rawText.isBlank()) {
            return CategorizedItineraryText()
        }

        // Check if rawText is already structured with headers
        if (rawText.contains(CategorizedItineraryText.HEADER_FLIGHTS) ||
            rawText.contains(CategorizedItineraryText.HEADER_ACCOMMODATIONS) ||
            rawText.contains(CategorizedItineraryText.HEADER_PLANS)
        ) {
            return parseFromFormattedString(rawText)
        }

        val flights = mutableListOf<String>()
        val accommodations = mutableListOf<String>()
        val plans = mutableListOf<String>()

        // Split text by paragraphs or logical blocks
        val blocks = splitIntoBlocks(rawText)

        for (block in blocks) {
            val lower = block.lowercase(Locale.US)
            when (classifyBlock(lower)) {
                CategoryType.FLIGHTS -> flights.add(block)
                CategoryType.ACCOMMODATIONS -> accommodations.add(block)
                CategoryType.PLANS -> plans.add(block)
            }
        }

        // If everything fell into plans or uncategorized, inspect line by line
        if (flights.isEmpty() && accommodations.isEmpty() && plans.size <= 1) {
            val lines = rawText.lines().map { it.trim() }.filter { it.length > 5 }
            val fLines = mutableListOf<String>()
            val aLines = mutableListOf<String>()
            val pLines = mutableListOf<String>()

            for (line in lines) {
                val lowerLine = line.lowercase(Locale.US)
                when (classifyBlock(lowerLine)) {
                    CategoryType.FLIGHTS -> fLines.add(line)
                    CategoryType.ACCOMMODATIONS -> aLines.add(line)
                    CategoryType.PLANS -> pLines.add(line)
                }
            }

            if (fLines.isNotEmpty() || aLines.isNotEmpty()) {
                return CategorizedItineraryText(
                    flightsText = fLines.joinToString("\n"),
                    accommodationsText = aLines.joinToString("\n"),
                    plansText = pLines.joinToString("\n")
                )
            }
        }

        return CategorizedItineraryText(
            flightsText = flights.joinToString("\n\n"),
            accommodationsText = accommodations.joinToString("\n\n"),
            plansText = plans.joinToString("\n\n")
        )
    }

    /**
     * Parses structured text containing section headers back into a CategorizedItineraryText model.
     */
    fun parseFromFormattedString(formattedText: String): CategorizedItineraryText {
        var currentSection = CategoryType.PLANS
        val flights = StringBuilder()
        val accommodations = StringBuilder()
        val plans = StringBuilder()

        for (line in formattedText.lines()) {
            val trimmed = line.trim()
            when {
                trimmed.equals(CategorizedItineraryText.HEADER_FLIGHTS, ignoreCase = true) ||
                trimmed.startsWith("=== FLIGHT", ignoreCase = true) -> {
                    currentSection = CategoryType.FLIGHTS
                }
                trimmed.equals(CategorizedItineraryText.HEADER_ACCOMMODATIONS, ignoreCase = true) ||
                trimmed.startsWith("=== ACCOMMODATION", ignoreCase = true) ||
                trimmed.startsWith("=== HOTEL", ignoreCase = true) -> {
                    currentSection = CategoryType.ACCOMMODATIONS
                }
                trimmed.equals(CategorizedItineraryText.HEADER_PLANS, ignoreCase = true) ||
                trimmed.startsWith("=== PLAN", ignoreCase = true) ||
                trimmed.startsWith("=== ACTIVIT", ignoreCase = true) -> {
                    currentSection = CategoryType.PLANS
                }
                else -> {
                    when (currentSection) {
                        CategoryType.FLIGHTS -> flights.append(line).append("\n")
                        CategoryType.ACCOMMODATIONS -> accommodations.append(line).append("\n")
                        CategoryType.PLANS -> plans.append(line).append("\n")
                    }
                }
            }
        }

        return CategorizedItineraryText(
            flightsText = flights.toString().trim(),
            accommodationsText = accommodations.toString().trim(),
            plansText = plans.toString().trim()
        )
    }

    /**
     * Parses the categorized, user-reviewed text sections into structured [DraftItineraryEvent] list.
     */
    suspend fun parseToDraftEvents(
        categorized: CategorizedItineraryText,
        defaultDestination: String = ""
    ): List<DraftItineraryEvent> = withContext(Dispatchers.Default) {
        val results = mutableListOf<DraftItineraryEvent>()

        // 1. Parse Flights
        if (categorized.flightsText.isNotBlank()) {
            val flightEvents = OfflineTravelHeuristicParser.parse(
                categorized.flightsText,
                defaultDestination
            ).map { event ->
                event.copy(category = ItineraryCategory.FLIGHT)
            }
            results.addAll(flightEvents)
        }

        // 2. Parse Accommodations
        if (categorized.accommodationsText.isNotBlank()) {
            val accommodationEvents = OfflineTravelHeuristicParser.parse(
                categorized.accommodationsText,
                defaultDestination
            ).map { event ->
                event.copy(category = ItineraryCategory.ACCOMMODATION)
            }
            results.addAll(accommodationEvents)
        }

        // 3. Parse Plans
        if (categorized.plansText.isNotBlank()) {
            val plansEvents = OfflineTravelHeuristicParser.parse(
                categorized.plansText,
                defaultDestination
            ).map { event ->
                // Keep categorized as ACTIVITY, TRANSPORT, RESTAURANT, or fallback to ACTIVITY
                val cat = when (event.category) {
                    ItineraryCategory.FLIGHT, ItineraryCategory.ACCOMMODATION, ItineraryCategory.OTHER -> ItineraryCategory.ACTIVITY
                    else -> event.category
                }
                event.copy(category = cat)
            }
            results.addAll(plansEvents)
        }

        // Fallback: If no discrete events found, create a generic draft from any available content
        if (results.isEmpty() && categorized.hasAnyContent) {
            val combined = categorized.toFormattedString()
            val parsed = OfflineTravelHeuristicParser.parse(combined, defaultDestination)
            results.addAll(parsed)
        }

        results.distinctBy { "${it.title}_${it.startDateTime}_${it.confirmationCode}" }
            .sortedBy { it.startDateTime }
    }

    private fun splitIntoBlocks(text: String): List<String> {
        val parts = text.split(Regex("\n\\s*\n+"))
        val trimmed = parts.map { it.trim() }.filter { it.isNotEmpty() }
        if (trimmed.size > 1) return trimmed

        // Fallback to line splits if no paragraph breaks
        return text.lines().map { it.trim() }.filter { it.length > 10 }
    }

    private fun classifyBlock(lower: String): CategoryType {
        var flightScore = 0
        var hotelScore = 0
        var plansScore = 0

        for (kw in FLIGHT_KEYWORDS) {
            if (lower.contains(kw)) flightScore += 2
        }
        // Flight number regex bonus
        if (Regex("\\b([A-Za-z]{2}|[A-Za-z]\\d|\\d[A-Za-z])\\s?\\d{1,4}\\b").containsMatchIn(lower)) {
            flightScore += 3
        }

        for (kw in ACCOMMODATION_KEYWORDS) {
            if (lower.contains(kw)) hotelScore += 2
        }

        for (kw in PLANS_KEYWORDS) {
            if (lower.contains(kw)) plansScore += 2
        }

        return when {
            flightScore > hotelScore && flightScore > plansScore -> CategoryType.FLIGHTS
            hotelScore > flightScore && hotelScore > plansScore -> CategoryType.ACCOMMODATIONS
            plansScore > 0 -> CategoryType.PLANS
            // Default heuristics based on mild keywords
            flightScore > 0 -> CategoryType.FLIGHTS
            hotelScore > 0 -> CategoryType.ACCOMMODATIONS
            else -> CategoryType.PLANS
        }
    }

    private enum class CategoryType {
        FLIGHTS,
        ACCOMMODATIONS,
        PLANS
    }
}
