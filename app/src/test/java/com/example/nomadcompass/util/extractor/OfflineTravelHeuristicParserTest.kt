package com.example.nomadcompass.util.extractor

import com.example.nomadcompass.domain.model.ItineraryCategory
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OfflineTravelHeuristicParserTest {

    @Test
    fun parseFlightConfirmation_extractsFlightCodeRoutePnrAndDates() = runBlocking {
        val rawText = """
            Singapore Airlines Electronic Ticket Confirmation
            Passenger: John Doe
            Flight: SQ 321
            Route: SIN -> LHR
            Departure: 2026-10-14 09:30
            Arrival: 2026-10-14 15:45
            Terminal: Terminal 3
            Booking Reference / PNR: 7J9KL2
            Status: Confirmed
        """.trimIndent()

        val events = OfflineTravelHeuristicParser.parse(rawText, "London")
        assertTrue("Should extract at least 1 event", events.isNotEmpty())

        val flightEvent = events.first()
        assertEquals(ItineraryCategory.FLIGHT, flightEvent.category)
        assertTrue("Title should contain flight code SQ 321", flightEvent.title.contains("SQ 321"))
        assertEquals("7J9KL2", flightEvent.confirmationCode)
        assertTrue("Start date time should contain date", flightEvent.startDateTime.contains("2026-10-14"))
    }

    @Test
    fun parseHotelReservation_extractsAccommodationDetailsAndConfirmationCode() = runBlocking {
        val rawText = """
            Booking.com Reservation Confirmation
            Property: Grand Hyatt Bali Resort
            Address: Kawasan Wisata Nusa Dua BTDC, Bali, Indonesia
            Check-in: 2026-10-18 14:00
            Check-out: 2026-10-25 11:00
            Confirmation Number: 8493021
            Room: Deluxe King Ocean View
        """.trimIndent()

        val events = OfflineTravelHeuristicParser.parse(rawText, "Bali")
        assertTrue("Should extract accommodation event", events.isNotEmpty())

        val hotelEvent = events.first()
        assertEquals(ItineraryCategory.ACCOMMODATION, hotelEvent.category)
        assertEquals("8493021", hotelEvent.confirmationCode)
    }

    @Test
    fun parseMultiSegmentItinerary_extractsMultipleDiscreteEvents() = runBlocking {
        val multiSegmentText = """
            Day 1: Flight AA 100 JFK -> LHR
            Departure: 2026-11-01 18:00
            PNR: AB12CD
            
            Day 2: Stay at The Ritz London Hotel
            Check-in: 2026-11-02 15:00
            Confirmation: 994821
            
            Day 4: Tour Thames River Sightseeing Cruise
            Date: 2026-11-04 10:00
            Location: Westminster Pier
        """.trimIndent()

        val events = OfflineTravelHeuristicParser.parse(multiSegmentText, "London")
        assertTrue("Should extract multiple discrete events", events.size >= 2)
    }
}
