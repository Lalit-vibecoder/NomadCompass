package com.example.nomadcompass.util.extractor

import com.example.nomadcompass.domain.model.ItineraryCategory
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ItineraryCategorizerTest {

    @Test
    fun categorizeRawText_splitsIntoFlightsAccommodationsAndPlans() {
        val rawMixedText = """
            Singapore Airlines Electronic Ticket Confirmation
            Passenger: Sarah Connor
            Flight: SQ 321
            Route: SIN -> LHR
            Departure: 2026-10-14 09:30
            PNR: 7J9KL2

            Booking.com Reservation Confirmation
            Property: Grand Hyatt Bali Resort
            Check-in: 2026-10-18 14:00
            Check-out: 2026-10-25 11:00
            Confirmation Number: 8493021

            Uluwatu Temple Sunset Excursion & Kecak Dance
            Date: 2026-10-19 16:00
            Meeting Point: Hotel Lobby
            Tour Guide: Wayan
        """.trimIndent()

        val categorized = ItineraryCategorizer.categorizeRawText(rawMixedText)

        // Flights section
        assertTrue("Flights section should contain flight info", categorized.flightsText.contains("SQ 321"))
        assertTrue("Flights section should contain PNR", categorized.flightsText.contains("7J9KL2"))

        // Accommodations section
        assertTrue("Accommodations section should contain hotel info", categorized.accommodationsText.contains("Grand Hyatt"))
        assertTrue("Accommodations section should contain check-in", categorized.accommodationsText.contains("Check-in"))

        // Plans section
        assertTrue("Plans section should contain temple tour", categorized.plansText.contains("Uluwatu Temple"))

        assertTrue("Has content should be true", categorized.hasAnyContent)
    }

    @Test
    fun parseFromFormattedString_correctlyRestoresSections() {
        val formatted = """
            === FLIGHTS ===
            Flight BA 1452 LHR -> CDG 2026-11-05 08:00
            PNR: GHK881

            === ACCOMMODATIONS ===
            Stay at Hotel Le Marais Paris
            Check-in: 2026-11-05

            === PLANS ===
            Louvre Museum Guided Tour 2026-11-06 10:00
            Dinner at Le Gabriel 2026-11-06 19:30
        """.trimIndent()

        val parsed = ItineraryCategorizer.parseFromFormattedString(formatted)

        assertTrue(parsed.flightsText.contains("BA 1452"))
        assertTrue(parsed.accommodationsText.contains("Hotel Le Marais"))
        assertTrue(parsed.plansText.contains("Louvre Museum"))
        assertTrue(parsed.plansText.contains("Dinner at Le Gabriel"))
    }

    @Test
    fun parseToDraftEvents_createsProperDomainEventsFromAdjustedText() = runBlocking {
        val categorized = CategorizedItineraryText(
            flightsText = "Flight EK 202 DXB -> JFK on 2026-12-01 at 08:30, PNR: EK9921",
            accommodationsText = "The Standard High Line Hotel, Check-in: 2026-12-01, Confirmation: STD441",
            plansText = "Broadway Show Hamilton on 2026-12-02 at 20:00\nSubway Transit to Brooklyn on 2026-12-03"
        )

        val draftEvents = ItineraryCategorizer.parseToDraftEvents(categorized, "New York")

        assertEquals(3, draftEvents.size)

        // Verify Flight
        val flightEvent = draftEvents.find { it.category == ItineraryCategory.FLIGHT }
        assertTrue("Flight event should exist", flightEvent != null)
        assertTrue("Flight title should contain EK 202", flightEvent!!.title.contains("EK 202"))
        assertEquals("EK9921", flightEvent.confirmationCode)

        // Verify Accommodation
        val hotelEvent = draftEvents.find { it.category == ItineraryCategory.ACCOMMODATION }
        assertTrue("Hotel event should exist", hotelEvent != null)
        assertTrue("Hotel confirmation should match", hotelEvent!!.confirmationCode.contains("STD441"))

        // Verify Plan
        val planEvents = draftEvents.filter { it.category != ItineraryCategory.FLIGHT && it.category != ItineraryCategory.ACCOMMODATION }
        assertTrue("Plans should be present", planEvents.isNotEmpty())
    }

    @Test
    fun emptyOrBlankText_returnsEmptyModelGracefully() {
        val emptyResult = ItineraryCategorizer.categorizeRawText("")
        assertFalse(emptyResult.hasAnyContent)
        assertEquals("", emptyResult.flightsText)
        assertEquals("", emptyResult.accommodationsText)
        assertEquals("", emptyResult.plansText)
    }
}
