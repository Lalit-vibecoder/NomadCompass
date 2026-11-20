package com.example.nomadcompass.util.extractor

import com.example.nomadcompass.BuildConfig
import com.example.nomadcompass.domain.model.DraftItineraryEvent
import com.example.nomadcompass.domain.model.ItineraryCategory
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

/**
 * AI-powered Itinerary Extractor using Google Gemini API.
 * Uses BuildConfig.GEMINI_API_KEY loaded securely from local.properties.
 */
object GeminiItineraryParser {

    /**
     * Checks if the Gemini API key has been configured in local.properties.
     */
    fun isApiKeyConfigured(): Boolean {
        return BuildConfig.GEMINI_API_KEY.isNotBlank() && 
               !BuildConfig.GEMINI_API_KEY.contains("YOUR_GEMINI_API_KEY", ignoreCase = true)
    }

    /**
     * Parses raw itinerary text, emails, or booking receipts using Gemini AI into structured draft events.
     *
     * @param rawText Extracted text from PDF, HTML, screenshot, or user paste.
     * @param tripContext Optional country/destination or dates context to improve extraction accuracy.
     * @return Result containing list of [DraftItineraryEvent].
     */
    suspend fun parseItinerary(
        rawText: String,
        tripContext: String = ""
    ): Result<List<DraftItineraryEvent>> = withContext(Dispatchers.IO) {
        if (!isApiKeyConfigured()) {
            return@withContext Result.failure(
                IllegalStateException("Gemini API key is not configured. Please add GEMINI_API_KEY to your local.properties file.")
            )
        }

        if (rawText.isBlank()) {
            return@withContext Result.failure(
                IllegalArgumentException("Input text for itinerary parsing is empty.")
            )
        }

        try {
            val prompt = """
                You are an expert travel assistant. Extract all travel reservations, bookings, flights, hotels, tours, activities, transit, and dining from the following document or email.
                ${if (tripContext.isNotBlank()) "Trip destination/context: $tripContext" else ""}
                
                Strictly output a JSON array of objects with the following schema:
                [
                  {
                    "title": "Flight to Tokyo (JL 001)",
                    "category": "FLIGHT", // strictly one of: FLIGHT, ACCOMMODATION, ACTIVITY, TRANSPORT, RESTAURANT, OTHER
                    "location": "Haneda Airport, Tokyo",
                    "startDateTime": "2026-11-20T10:00:00", // ISO-8601 or best readable date/time (e.g. 2026-11-20 10:00)
                    "endDateTime": "2026-11-20T14:30:00",
                    "confirmationCode": "ABC123XYZ",
                    "notes": "Terminal 3, Seat 18K"
                  }
                ]

                Document Text:
                \"\"\"
                $rawText
                \"\"\"
            """.trimIndent()

            val jsonText = generateWithFallback(prompt, temperature = 0.2f)

            if (jsonText.isBlank()) {
                return@withContext Result.failure(
                    IllegalStateException("Gemini returned an empty response.")
                )
            }

            val parsedEvents = parseJsonToDraftEvents(jsonText, rawText)
            Result.success(parsedEvents)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Generates a curated, sequential travel itinerary for a specific country with optional place/activity references.
     *
     * @param countryName Destination country (e.g. Japan, Italy, Thailand).
     * @param placesOrPreferences Optional comma-separated places, cities, or travel preferences (e.g. "Tokyo, Kyoto, Mount Fuji").
     * @param durationDays Number of days for the itinerary (e.g. 3, 5, 7, 10).
     * @return Result containing list of [DraftItineraryEvent].
     */
    suspend fun generateItineraryForCountry(
        countryName: String,
        placesOrPreferences: String = "",
        durationDays: Int = 5
    ): Result<List<DraftItineraryEvent>> = withContext(Dispatchers.IO) {
        if (!isApiKeyConfigured()) {
            return@withContext Result.failure(
                IllegalStateException("Gemini API key is not configured. Please add GEMINI_API_KEY to your local.properties file.")
            )
        }

        try {
            val prompt = """
                You are an expert travel planner. Create a realistic, highly enjoyable, day-by-day travel itinerary for a $durationDays-day trip to $countryName.
                ${if (placesOrPreferences.isNotBlank()) "Include these specific cities, attractions, or travel preferences: $placesOrPreferences" else ""}
                
                For each day, generate 2-4 well-paced events (e.g., Morning Activity, Lunch/Dining, Afternoon Highlight, Evening Event or Accommodation check-in).
                
                Strictly output a JSON array of objects with the following schema:
                [
                  {
                    "title": "Visit Fushimi Inari Shrine (Day 1)",
                    "category": "ACTIVITY", // strictly one of: FLIGHT, ACCOMMODATION, ACTIVITY, TRANSPORT, RESTAURANT, OTHER
                    "location": "Kyoto, Japan",
                    "startDateTime": "Day 1 09:00 AM",
                    "endDateTime": "Day 1 12:00 PM",
                    "confirmationCode": "",
                    "notes": "Hike through thousands of vermilion torii gates. Best visited early morning."
                  }
                ]
            """.trimIndent()

            val jsonText = generateWithFallback(prompt, temperature = 0.5f)

            if (jsonText.isBlank()) {
                return@withContext Result.failure(
                    IllegalStateException("Gemini returned an empty itinerary response.")
                )
            }

            val parsedEvents = parseJsonToDraftEvents(jsonText, "Generated for $countryName")
            Result.success(parsedEvents)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun generateWithFallback(
        prompt: String,
        temperature: Float
    ): String {
        val modelCandidates = listOf(
            "gemini-3.5-flash-lite",
            "gemini-flash-latest",
            "gemini-3.8-flash"
        )

        var lastException: Exception? = null

        for (modelName in modelCandidates) {
            try {
                val generativeModel = GenerativeModel(
                    modelName = modelName,
                    apiKey = BuildConfig.GEMINI_API_KEY,
                    generationConfig = generationConfig {
                        responseMimeType = "application/json"
                        this.temperature = temperature
                    }
                )
                val response = generativeModel.generateContent(prompt)
                val text = response.text?.trim()
                if (!text.isNullOrBlank()) {
                    return text
                }
            } catch (e: Exception) {
                lastException = e
            }
        }
        throw lastException ?: IllegalStateException("Unable to generate response from Gemini AI models.")
    }

    private fun parseJsonToDraftEvents(jsonText: String, rawText: String): List<DraftItineraryEvent> {
        val events = mutableListOf<DraftItineraryEvent>()
        try {
            // Clean markdown code blocks if present (e.g. ```json ... ```)
            val cleanedJson = jsonText
                .replace(Regex("^```(?:json)?\\s*"), "")
                .replace(Regex("\\s*```$"), "")
                .trim()

            val jsonArray = if (cleanedJson.startsWith("[")) {
                JSONArray(cleanedJson)
            } else if (cleanedJson.startsWith("{")) {
                val obj = JSONObject(cleanedJson)
                obj.optJSONArray("events") ?: obj.optJSONArray("itinerary") ?: JSONArray().put(obj)
            } else {
                JSONArray()
            }

            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.optJSONObject(i) ?: continue
                val title = item.optString("title", "").trim()
                if (title.isBlank()) continue

                val categoryStr = item.optString("category", "OTHER").trim()
                val category = ItineraryCategory.fromString(categoryStr)
                val location = item.optString("location", "").trim()
                val startDateTime = item.optString("startDateTime", "").trim()
                val endDateTime = item.optString("endDateTime", "").trim()
                val confirmationCode = item.optString("confirmationCode", "").trim()
                val notes = item.optString("notes", "").trim()

                events.add(
                    DraftItineraryEvent(
                        title = title,
                        category = category,
                        location = location,
                        startDateTime = startDateTime,
                        endDateTime = endDateTime,
                        confirmationCode = confirmationCode,
                        notes = notes,
                        rawSnippet = rawText.take(120),
                        confidenceScore = 0.95f,
                        isSelected = true
                    )
                )
            }
        } catch (e: Exception) {
            // Fallback: Return empty list if parsing JSON failed
        }
        return events
    }
}
