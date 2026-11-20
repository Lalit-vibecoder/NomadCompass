package com.example.nomadcompass.util.extractor

import com.example.nomadcompass.domain.model.ItineraryCategory
import com.example.nomadcompass.domain.model.toDraftEvents
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SpatialItineraryParserTest {

    @Test
    fun spatialSorting_groupsOverlappingYCoordinatesAndSortsByLeft() {
        // Line 1 & Line 2 are on the same row (overlapping Y: 100-120 and 105-125)
        // Line 2 is to the right (left = 250), Line 1 is to the left (left = 50)
        // Line 3 is on the next row (Y: 140-160)
        val lines = listOf(
            SpatialLine(text = "Tokyo", top = 105, bottom = 125, left = 250, right = 400),
            SpatialLine(text = "Day 1:", top = 100, bottom = 120, left = 50, right = 150),
            SpatialLine(text = "• Visit Senso-ji Temple", top = 140, bottom = 160, left = 50, right = 300)
        )

        val spatiallySorted = SpatialItineraryParser.sortSpatialLines(lines)
        val resultLines = spatiallySorted.lines()

        assertEquals("Should have 2 horizontal rows", 2, resultLines.size)
        assertEquals("Day 1: Tokyo", resultLines[0])
        assertEquals("• Visit Senso-ji Temple", resultLines[1])
    }

    @Test
    fun spatialSorting_preservesVerticalReadingOrder() {
        val lines = listOf(
            SpatialLine(text = "Day 3: Osaka", top = 300, bottom = 320, left = 50, right = 200),
            SpatialLine(text = "Day 1: Tokyo", top = 100, bottom = 120, left = 50, right = 200),
            SpatialLine(text = "Day 2: Kyoto", top = 200, bottom = 220, left = 50, right = 200)
        )

        val spatiallySorted = SpatialItineraryParser.sortSpatialLines(lines)
        val resultLines = spatiallySorted.lines()

        assertEquals("Day 1: Tokyo", resultLines[0])
        assertEquals("Day 2: Kyoto", resultLines[1])
        assertEquals("Day 3: Osaka", resultLines[2])
    }

    @Test
    fun dayAnchorChunking_identifiesDiverseDayBoundariesAndPreamble() {
        val itineraryText = """
            Tokyo & Kyoto 7-Day Adventure
            
            Day 1: Tokyo
            - Senso-ji Temple
            
            Day - 2: Kyoto
            - Fushimi Inari Shrine
            
            Monday, Oct 14
            - Osaka Castle
            
            Oct 15th
            - Nara Deer Park
        """.trimIndent()

        val (tripTitle, chunks) = SpatialItineraryParser.chunkByDays(itineraryText)

        assertEquals("Tokyo & Kyoto 7-Day Adventure", tripTitle)
        assertEquals("Should extract 4 day chunks", 4, chunks.size)
        assertEquals("Day 1", chunks[0].anchorText)
        assertEquals("Day - 2", chunks[1].anchorText)
        assertEquals("Monday, Oct 14", chunks[2].anchorText)
        assertEquals("Oct 15th", chunks[3].anchorText)
    }

    @Test
    fun intraDayClassification_extractsHierarchyDayLocationAndPlaces() {
        val rawItinerary = """
            Japan Autumn Odyssey

            Day 1: Tokyo
            • Visit Senso-ji Temple
            • Tokyo Skytree observation deck
            • Tsukiji Outer Market
            Pack comfortable walking shoes

            Day 2
            Kyoto, Japan
            1. Fushimi Inari Shrine
            2. Kinkaku-ji Golden Pavilion
            3. Arashiyama Bamboo Grove
            Hotel check-in at 3 PM

            Friday, October 24th
            Osaka
            - Dotonbori Street Food
            - Osaka Castle Park
        """.trimIndent()

        val result = SpatialItineraryParser.parse(rawItinerary)

        assertEquals("Japan Autumn Odyssey", result.tripTitle)
        assertEquals(3, result.days.size)

        // Day 1 Verification
        val day1 = result.days[0]
        assertEquals("Day 1", day1.dayLabel)
        assertEquals("Tokyo", day1.location)
        assertEquals(3, day1.placesToVisit.size)
        assertTrue(day1.placesToVisit.contains("Visit Senso-ji Temple"))
        assertTrue(day1.placesToVisit.contains("Tokyo Skytree observation deck"))
        assertTrue(day1.placesToVisit.contains("Tsukiji Outer Market"))
        assertEquals("Pack comfortable walking shoes", day1.notes)

        // Day 2 Verification
        val day2 = result.days[1]
        assertEquals("Day 2", day2.dayLabel)
        assertEquals("Kyoto, Japan", day2.location)
        assertEquals(3, day2.placesToVisit.size)
        assertTrue(day2.placesToVisit.contains("Fushimi Inari Shrine"))
        assertTrue(day2.placesToVisit.contains("Kinkaku-ji Golden Pavilion"))
        assertTrue(day2.placesToVisit.contains("Arashiyama Bamboo Grove"))
        assertEquals("Hotel check-in at 3 PM", day2.notes)

        // Day 3 Verification
        val day3 = result.days[2]
        assertEquals("Friday, October 24th", day3.dayLabel)
        assertEquals("Osaka", day3.location)
        assertNotNull(day3.date)
        assertTrue(day3.date!!.contains("October 24"))
        assertEquals(2, day3.placesToVisit.size)
        assertTrue(day3.placesToVisit.contains("Dotonbori Street Food"))
        assertTrue(day3.placesToVisit.contains("Osaka Castle Park"))
    }

    @Test
    fun intraDayClassification_poiIndicatorsClassifyPlacesEvenWithoutListMarkers() {
        val chunkText = """
            Day 3: Paris
            Visit the Eiffel Tower
            Louvre Museum exhibition
            Walk along Champs-Elysees Street
            Montmartre hill
            Remember metro pass
        """.trimIndent()

        val result = SpatialItineraryParser.parse(chunkText)
        val day = result.days.first()

        assertEquals("Day 3", day.dayLabel)
        assertEquals("Paris", day.location)
        assertTrue("Eiffel Tower recognized via POI", day.placesToVisit.any { it.contains("Tower") })
        assertTrue("Louvre Museum recognized via POI", day.placesToVisit.any { it.contains("Museum") })
        assertTrue("Street recognized via POI", day.placesToVisit.any { it.contains("Street") })
        assertTrue("Notes captured non-place line", day.notes?.contains("Remember metro pass") == true)
    }

    @Test
    fun toDraftEvents_mapsHierarchyToDraftItineraryEvents() {
        val itinerary = """
            Day 1: Bali
            • Kuta Beach
            • Uluwatu Temple
        """.trimIndent()

        val parsed = SpatialItineraryParser.parse(itinerary)
        val draftEvents = parsed.toDraftEvents(defaultDestination = "Indonesia")

        assertEquals(2, draftEvents.size)
        assertEquals("Kuta Beach", draftEvents[0].title)
        assertEquals("Bali", draftEvents[0].location)
        assertEquals(ItineraryCategory.ACTIVITY, draftEvents[0].category)

        assertEquals("Uluwatu Temple", draftEvents[1].title)
        assertEquals("Bali", draftEvents[1].location)
        assertEquals(ItineraryCategory.ACTIVITY, draftEvents[1].category)
    }

    @Test
    fun fallback_handlesUnanchoredSingleScopeItinerary() {
        val rawText = """
            Weekend in Rome
            • Colosseum
            • Trevi Fountain
            • Vatican Museum
        """.trimIndent()

        val parsed = SpatialItineraryParser.parse(rawText)

        assertEquals("Weekend in Rome", parsed.tripTitle)
        assertEquals(1, parsed.days.size)
        val day = parsed.days.first()
        assertEquals("Day 1", day.dayLabel)
        assertEquals(3, day.placesToVisit.size)
        assertTrue(day.placesToVisit.contains("Colosseum"))
        assertTrue(day.placesToVisit.contains("Trevi Fountain"))
        assertTrue(day.placesToVisit.contains("Vatican Museum"))
    }

    @Test
    fun diverseListMarkers_stripCorrectly() {
        val rawText = """
            Day 1: Seoul
            ✦ N Seoul Tower
            ▪ Gyeongbokgung Palace
            1) Bukchon Hanok Village
            ✓ Myeongdong Night Market
        """.trimIndent()

        val parsed = SpatialItineraryParser.parse(rawText)
        val day = parsed.days.first()

        assertEquals("Seoul", day.location)
        assertEquals(4, day.placesToVisit.size)
        assertEquals("N Seoul Tower", day.placesToVisit[0])
        assertEquals("Gyeongbokgung Palace", day.placesToVisit[1])
        assertEquals("Bukchon Hanok Village", day.placesToVisit[2])
        assertEquals("Myeongdong Night Market", day.placesToVisit[3])
    }

    @Test
    fun emptyInput_returnsEmptyResultSafely() {
        val result = SpatialItineraryParser.parse("   ")
        assertEquals(null, result.tripTitle)
        assertTrue(result.days.isEmpty())
    }

    @Test
    fun spatialSorting_avoidsVerticalRowChainingAcrossConsecutiveLines() {
        // Consecutive lines with slight baseline touches (1-2 pixels) must NOT collapse into a single row
        val lines = listOf(
            SpatialLine(text = "Day 1: Tokyo", top = 100, bottom = 120, left = 50, right = 200),
            SpatialLine(text = "• Senso-ji Temple", top = 119, bottom = 139, left = 50, right = 250),
            SpatialLine(text = "• Tokyo Skytree", top = 138, bottom = 158, left = 50, right = 250)
        )

        val spatiallySorted = SpatialItineraryParser.sortSpatialLines(lines)
        val resultLines = spatiallySorted.lines().filter { it.isNotBlank() }

        assertEquals("Must produce 3 distinct vertical lines, not chain them", 3, resultLines.size)
        assertEquals("Day 1: Tokyo", resultLines[0])
        assertEquals("• Senso-ji Temple", resultLines[1])
        assertEquals("• Tokyo Skytree", resultLines[2])
    }

    @Test
    fun intraDayClassification_preservesMixedHotelsAndPlacesWithinDays() {
        val itinerary = """
            European Highlights

            Day 1: Paris
            Stay at Hotel Le Marais
            • Eiffel Tower
            • Louvre Museum
            Evening stroll by Seine

            Day 2: Rome
            Hotel: Albergo Roma
            1. Colosseum
            2. Roman Forum
        """.trimIndent()

        val parsed = SpatialItineraryParser.parse(itinerary)
        assertEquals("European Highlights", parsed.tripTitle)
        assertEquals(2, parsed.days.size)

        val day1 = parsed.days[0]
        assertEquals("Day 1", day1.dayLabel)
        assertEquals("Paris", day1.location)
        assertEquals(2, day1.placesToVisit.size)
        assertTrue(day1.placesToVisit.contains("Eiffel Tower"))
        assertTrue(day1.placesToVisit.contains("Louvre Museum"))
        assertTrue(day1.notes?.contains("Hotel Le Marais") == true)

        val day2 = parsed.days[1]
        assertEquals("Day 2", day2.dayLabel)
        assertEquals("Rome", day2.location)
        assertEquals(2, day2.placesToVisit.size)
        assertTrue(day2.placesToVisit.contains("Colosseum"))
        assertTrue(day2.placesToVisit.contains("Roman Forum"))
    }

    @Test
    fun multiLineBullets_unwrappedIntoSingleIntactPlace() {
        val raw = """
            Day 1: Kyoto
            • Explore the magnificent Fushimi Inari Taisha
              with its famous thousands of vermilion torii gates.
            • Visit Kinkaku-ji, also known as the Golden Pavilion,
              and stroll through the classical Zen garden.
        """.trimIndent()

        val parsed = SpatialItineraryParser.parse(raw)
        assertEquals(1, parsed.days.size)
        val day = parsed.days.first()
        assertEquals("Kyoto", day.location)
        assertEquals(2, day.placesToVisit.size)
        assertTrue(day.placesToVisit[0].contains("thousands of vermilion torii gates"))
        assertTrue(day.placesToVisit[1].contains("classical Zen garden"))
        assertTrue("Notes should not contain broken bullet tails", day.notes.isNullOrBlank())
    }

    @Test
    fun multiLineSentences_unwrappedWithoutFragmenting() {
        val raw = """
            Day 1: Tokyo
            Arrive at Narita International Airport and transfer to
            our hotel in Shinjuku. After checking in, take an
            evening walking tour of Omoide Yokocho and Kabukicho.
        """.trimIndent()

        val parsed = SpatialItineraryParser.parse(raw)
        assertEquals(1, parsed.days.size)
        val day = parsed.days.first()
        assertEquals("Tokyo", day.location)
        assertTrue("Walking tour captured intact", day.placesToVisit.any { it.contains("evening walking tour") && it.contains("Kabukicho") })
    }

    @Test
    fun intraParagraphSentences_properlySegmented() {
        val raw = """
            Day 1: Tokyo
            Start your day at Senso-ji Temple. Afterwards, explore Ueno Park. In the evening, visit Tokyo Skytree.
        """.trimIndent()

        val parsed = SpatialItineraryParser.parse(raw)
        assertEquals(1, parsed.days.size)
        val day = parsed.days.first()
        assertEquals("Tokyo", day.location)
        assertEquals(3, day.placesToVisit.size)
        assertTrue(day.placesToVisit.any { it.contains("Senso-ji") })
        assertTrue(day.placesToVisit.any { it.contains("Ueno Park") })
        assertTrue(day.placesToVisit.any { it.contains("Tokyo Skytree") })
    }

    @Test
    fun nonLocationActionSentences_notTreatedAsLocation() {
        val raw = """
            Day 1
            In Paris
            Wear comfortable walking shoes.
            • Visit Eiffel Tower
        """.trimIndent()

        val parsed = SpatialItineraryParser.parse(raw)
        val day = parsed.days.first()
        assertEquals("Paris", day.location)
        assertEquals(1, day.placesToVisit.size)
        assertTrue("Action sentence stored in notes", day.notes?.contains("Wear comfortable walking shoes") == true)
    }
}
