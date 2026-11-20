package com.example.nomadcompass.util.extractor

import com.example.nomadcompass.domain.model.ParsedDayPlan
import com.example.nomadcompass.domain.model.ParsedItineraryResult
import com.google.mlkit.nl.entityextraction.Entity
import com.google.mlkit.nl.entityextraction.EntityAnnotation
import com.google.mlkit.nl.entityextraction.EntityExtraction
import com.google.mlkit.nl.entityextraction.EntityExtractionParams
import com.google.mlkit.nl.entityextraction.EntityExtractorOptions
import com.google.mlkit.vision.text.Text
import java.text.BreakIterator
import java.util.Locale
import java.util.regex.Pattern

/**
 * Geometric bounding box representation for spatial text sorting.
 */
data class SpatialLine(
    val text: String,
    val top: Int,
    val bottom: Int,
    val left: Int,
    val right: Int
)

/**
 * Discrete chunk representing exactly one day's scope in an itinerary.
 */
data class DayChunk(
    val anchorText: String,
    val chunkText: String,
    val lines: List<String>
)

/**
 * Hierarchical "Spatial Chunk & Extract" engine that preserves parent-child structures
 * (Day -> Location/City -> Places to Visit) 100% on-device without cloud APIs.
 */
object SpatialItineraryParser {

    // ── Regex Pattern Library ──

    /**
     * Day boundary anchor pattern:
     * Matches "Day 1", "Day - 2", "Monday, Oct 14", "Oct 14th", etc. at line starts.
     * Allows optional leading spaces or tabs.
     */
    val DAY_ANCHOR_PATTERN: Pattern = Pattern.compile(
        "(?i)(?:^|\\n)[ \\t]*(day\\s*\\d+|day\\s*-\\s*\\d+|(?:monday|tuesday|wednesday|thursday|friday|saturday|sunday)?\\s*,?\\s*(?:jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)[a-z]*\\s+\\d{1,2}(?:st|nd|rd|th)?)"
    )

    val LOCATION_TRIGGER_REGEX: Pattern = Pattern.compile(
        "(?i)^\\s*(?:in|at|stay\\s+(?:at|in)|arrive\\s+(?:in|at)|transfer\\s+to|exploring)\\s+([\\w\\s,.'-]+)",
        Pattern.CASE_INSENSITIVE
    )

    private val TIME_PATTERN = Pattern.compile(
        "^(?:\\d{1,2}:\\d{2}|morning:|afternoon:|evening:|night:)",
        Pattern.CASE_INSENSITIVE
    )

    private val LIST_MARKER_REGEX = Pattern.compile(
        "^[•\\-*▪▫–—✦·✓✔☑√\\s]*(\\d+[.)]|[•\\-*▪▫–—✦·✓✔☑√])\\s*"
    )

    private val POI_INDICATORS = listOf(
        "visit", "temple", "museum", "beach", "park", "tower", "palace", "market", "street",
        "shrine", "castle", "cathedral", "church", "monument", "garden", "aquarium", "zoo",
        "gallery", "waterfall", "canyon", "resort", "island", "bridge", "square", "pier",
        "skytree", "promenade", "statue", "ruins", "basilica", "sanctuary",
        "tour", "explore", "cruise", "hike", "walk", "dinner", "lunch", "breakfast"
    )

    private val POI_REGEX = Pattern.compile(
        "\\b(" + POI_INDICATORS.joinToString("|") + ")\\b",
        Pattern.CASE_INSENSITIVE
    )

    private val DATE_EXTRACT_REGEX = Pattern.compile(
        "(?i)\\b(?:(?:20[2-3][0-9][-/.](?:0?[1-9]|1[0-2])[-/.](?:0?[1-9]|[12][0-9]|3[01]))|" +
        "(?:(?:0?[1-9]|[12][0-9]|3[01])[-/.](?:0?[1-9]|1[0-2])[-/.](?:20[2-3][0-9]))|" +
        "(?:(?:0?[1-9]|[12][0-9]|3[01])(?:st|nd|rd|th)?\\s+(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)[a-z]*(?:[\\s,]+(?:20[2-3][0-9]))?)|" +
        "(?:(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)[a-z]*\\s+(?:0?[1-9]|[12][0-9]|3[01])(?:st|nd|rd|th)?(?:[\\s,]+(?:20[2-3][0-9]))?))\\b"
    )

    // ── 1. Spatial Sorting (Handling OCR Geometry) ──

    /**
     * Extracts all text lines from ML Kit Text Recognition with normalized geometric coordinates.
     */
    fun extractSpatialLines(visionText: Text): List<SpatialLine> {
        val lines = mutableListOf<SpatialLine>()
        for (block in visionText.textBlocks) {
            for (line in block.lines) {
                val text = line.text.trim()
                if (text.isNotEmpty()) {
                    val box = line.boundingBox
                    val rawTop = box?.top ?: 0
                    val rawBottom = box?.bottom ?: (rawTop + 20)
                    val rawLeft = box?.left ?: 0
                    val rawRight = box?.right ?: rawLeft
                    val top = minOf(rawTop, rawBottom)
                    val bottom = maxOf(rawTop, rawBottom).coerceAtLeast(top + 1)
                    val left = minOf(rawLeft, rawRight)
                    val right = maxOf(rawLeft, rawRight).coerceAtLeast(left + 1)
                    lines.add(SpatialLine(text, top, bottom, left, right))
                }
            }
        }
        return lines
    }

    /**
     * Sorts all text lines vertically by bounding box top coordinate,
     * grouping lines with overlapping Y-coordinates into the same horizontal row,
     * sorted by boundingBox.left.
     */
    fun sortSpatialLines(lines: List<SpatialLine>): String {
        if (lines.isEmpty()) return ""

        // 1. Sort all lines vertically by top, then left
        val sortedByTop = lines.sortedWith(compareBy({ it.top }, { it.left }))

        // 2. Group lines into horizontal rows using anchor line overlap
        val rows = mutableListOf<MutableList<SpatialLine>>()
        val rowAnchors = mutableListOf<SpatialLine>()

        for (line in sortedByTop) {
            var placed = false
            for (i in rows.indices) {
                val anchor = rowAnchors[i]
                val hAnchor = anchor.bottom - anchor.top
                val hLine = line.bottom - line.top
                val minH = minOf(hAnchor, hLine).coerceAtLeast(1)
                val overlap = minOf(anchor.bottom, line.bottom) - maxOf(anchor.top, line.top)

                // Substantial vertical overlap (at least 40% of line height) indicates same horizontal row
                if (overlap > 0 && (overlap.toFloat() / minH) >= 0.4f) {
                    rows[i].add(line)
                    placed = true
                    break
                }
            }
            if (!placed) {
                rows.add(mutableListOf(line))
                rowAnchors.add(line)
            }
        }

        // 3. Sort each row horizontally by left coordinate and join
        return rows.indices
            .sortedBy { rowAnchors[it].top }
            .map { idx ->
                rows[idx].sortedBy { it.left }.joinToString(" ") { it.text.trim() }
            }
            .filter { it.isNotBlank() }
            .joinToString("\n")
    }

    /**
     * Converts ML Kit Text blocks directly into a spatially-sorted text stream.
     */
    fun sortSpatially(visionText: Text): String {
        val lines = extractSpatialLines(visionText)
        return sortSpatialLines(lines)
    }

    // ── 2. Day Anchor Chunking ──

    /**
     * Splits spatially sorted text into a trip title (if present) and discrete [DayChunk]s.
     */
    fun chunkByDays(spatiallySortedText: String): Pair<String?, List<DayChunk>> {
        val text = spatiallySortedText.trim()
        if (text.isEmpty()) return Pair(null, emptyList())

        val matcher = DAY_ANCHOR_PATTERN.matcher(text)
        val matchIndices = mutableListOf<Triple<Int, Int, String>>() // start, end, anchorText

        while (matcher.find()) {
            val anchor = matcher.group(1)?.trim() ?: ""
            val start = matcher.start(1)
            val end = matcher.end(1)
            matchIndices.add(Triple(start, end, anchor))
        }

        // If no day anchors detected, treat the entire document as a single general chunk
        if (matchIndices.isEmpty()) {
            val lines = text.lines().map { it.trim() }.filter { it.isNotEmpty() }
            val firstLine = lines.firstOrNull()
            val chunk = DayChunk(
                anchorText = "Day 1",
                chunkText = text,
                lines = lines
            )
            return Pair(firstLine, listOf(chunk))
        }

        // Preamble before first day anchor contains trip title if present
        val firstMatchStart = matchIndices.first().first
        val preamble = if (firstMatchStart > 0) text.substring(0, firstMatchStart).trim() else ""
        val tripTitle = preamble.lines().firstOrNull { it.isNotBlank() }?.trim()

        val chunks = mutableListOf<DayChunk>()
        for (i in matchIndices.indices) {
            val current = matchIndices[i]
            val chunkStart = current.first
            val chunkEnd = if (i + 1 < matchIndices.size) matchIndices[i + 1].first else text.length
            val chunkContent = text.substring(chunkStart, chunkEnd).trim()
            val lines = chunkContent.lines().map { it.trim() }.filter { it.isNotEmpty() }

            chunks.add(
                DayChunk(
                    anchorText = current.third,
                    chunkText = chunkContent,
                    lines = lines
                )
            )
        }

        return Pair(tripTitle, chunks)
    }

    // ── 3. Intra-Day Field Classification ──

    /**
     * Parses fields sequentially for a given [DayChunk]:
     * - dayLabel / date
     * - primaryLocation
     * - placesToVisit
     * - notes
     */
    private val TIME_EXPRESSION_REGEX = Pattern.compile(
        "(?i)^in the (?:morning|afternoon|evening|night)\\b"
    )

    private val CONTINUATION_ENDINGS = setOf(
        "and", "or", "with", "in", "at", "to", "for", "of", "the", "a", "an", "by", "from", "on", "into", "onto"
    )

    private fun endsWithContinuationWord(text: String): Boolean {
        val lastWord = text.trimEnd().split("\\s+".toRegex()).lastOrNull()?.lowercase(Locale.US)?.trimEnd('.', ',') ?: ""
        return lastWord in CONTINUATION_ENDINGS
    }

    fun classifyDayChunk(
        chunk: DayChunk,
        entityAnnotations: List<EntityAnnotation> = emptyList()
    ): ParsedDayPlan {
        val rawAnchor = chunk.anchorText.trim()
        val dayLabel = normalizeDayLabel(rawAnchor)

        // Date extraction from anchor or chunk lines
        var date = extractDate(rawAnchor) ?: extractDate(chunk.chunkText)

        // Check if ML Kit Entity Extraction found an address inside this chunk's text
        val addressAnnotation = entityAnnotations.firstOrNull { annotation ->
            annotation.entities.any { it.type == Entity.TYPE_ADDRESS } &&
            chunk.chunkText.contains(annotation.annotatedText, ignoreCase = true)
        }

        var primaryLocation: String? = addressAnnotation?.annotatedText?.trim()
        val placesToVisit = mutableListOf<String>()
        val notesLines = mutableListOf<String>()

        val lines = chunk.lines
        if (lines.isEmpty()) {
            return ParsedDayPlan(dayLabel, date, primaryLocation, emptyList(), null)
        }

        // 1. Process anchor line (Line 0)
        val firstLine = lines.first()
        var afterAnchorRemainder = removeAnchorPrefix(firstLine, rawAnchor)

        // If afterAnchorRemainder starts with a date, extract it
        if (afterAnchorRemainder.isNotBlank()) {
            val dateMatcher = DATE_EXTRACT_REGEX.matcher(afterAnchorRemainder)
            if (dateMatcher.find() && dateMatcher.start() == 0) {
                if (date == null) {
                    date = dateMatcher.group().trim()
                }
                afterAnchorRemainder = afterAnchorRemainder.substring(dateMatcher.end())
                    .trimStart(' ', ':', '-', '–', '—', '|', ',', '.')
                    .trim()
            }
        }

        // Check if the remainder on the anchor line provides location or places
        if (afterAnchorRemainder.isNotBlank()) {
            val locMatch = LOCATION_TRIGGER_REGEX.matcher(afterAnchorRemainder)
            if (primaryLocation == null && locMatch.find() && locMatch.start() == 0 && !TIME_EXPRESSION_REGEX.matcher(afterAnchorRemainder).find()) {
                primaryLocation = locMatch.group(1)?.trim()?.trimEnd('.', ',')
            } else if (isPlaceToVisit(afterAnchorRemainder)) {
                placesToVisit.add(cleanPlaceName(afterAnchorRemainder))
            } else if (primaryLocation == null && isCandidateLocation(afterAnchorRemainder)) {
                primaryLocation = afterAnchorRemainder.trimEnd('.', ',')
            } else if (afterAnchorRemainder.length > 2) {
                notesLines.add(afterAnchorRemainder)
            }
        }

        // 2. Unwrap lines for subsequent lines in chunk (skipping anchor line 0)
        val remainingLines = if (lines.size > 1) lines.subList(1, lines.size) else emptyList()
        val unwrappedItems = unwrapLines(remainingLines)
        var checkedLocationFallback = primaryLocation != null

        for (item in unwrappedItems) {
            if (LIST_MARKER_REGEX.matcher(item).find()) {
                // Direct bullet/list item: intact sentence
                placesToVisit.add(cleanPlaceName(item))
            } else {
                // Freeform text: segment into complete sentences
                val sentences = segmentSentences(item)
                for (sentence in sentences) {
                    val locMatch = LOCATION_TRIGGER_REGEX.matcher(sentence)
                    if (primaryLocation == null && locMatch.find() && locMatch.start() == 0 && !TIME_EXPRESSION_REGEX.matcher(sentence).find()) {
                        primaryLocation = locMatch.group(1)?.trim()?.trimEnd('.', ',')
                        checkedLocationFallback = true
                    } else if (isPlaceToVisit(sentence)) {
                        placesToVisit.add(cleanPlaceName(sentence))
                    } else if (!checkedLocationFallback && isCandidateLocation(sentence)) {
                        primaryLocation = sentence.trimEnd('.', ',')
                        checkedLocationFallback = true
                    } else {
                        val isLoc = primaryLocation != null && (
                            sentence.equals(primaryLocation, ignoreCase = true) ||
                            sentence.contains(primaryLocation!!, ignoreCase = true) ||
                            primaryLocation!!.contains(sentence, ignoreCase = true)
                        )
                        if (!isLoc) {
                            notesLines.add(sentence)
                        }
                    }
                }
            }
        }

        val notes = if (notesLines.isNotEmpty()) notesLines.joinToString("\n") else null

        return ParsedDayPlan(
            dayLabel = dayLabel,
            date = date,
            location = primaryLocation,
            placesToVisit = placesToVisit.distinct(),
            notes = notes
        )
    }

    /**
     * Unwraps soft line-wraps across physical lines into unified logical items.
     * Prevents multi-line sentences and bullet points from being fragmented.
     */
    fun unwrapLines(rawLines: List<String>): List<String> {
        val lines = rawLines.map { it.trim() }.filter { it.isNotEmpty() }
        if (lines.isEmpty()) return emptyList()

        val result = mutableListOf<String>()
        var current = StringBuilder(lines[0])

        for (i in 1 until lines.size) {
            val nextLine = lines[i]
            val prevText = current.toString().trimEnd()

            val isNextNewItem = LIST_MARKER_REGEX.matcher(nextLine).find() ||
                    DAY_ANCHOR_PATTERN.matcher(nextLine).find() ||
                    TIME_PATTERN.matcher(nextLine).find() ||
                    nextLine.startsWith("===")

            val isContinuation = !isNextNewItem && (
                    prevText.endsWith("-") ||
                    prevText.endsWith(",") ||
                    endsWithContinuationWord(prevText) ||
                    (nextLine.isNotEmpty() && nextLine.first().isLowerCase())
            )

            if (isContinuation) {
                if (prevText.endsWith("-") && prevText.length > 1 && prevText[prevText.length - 2].isLetter()) {
                    current.setLength(current.length - 1)
                    current.append(nextLine)
                } else {
                    current.append(" ").append(nextLine)
                }
            } else {
                result.add(current.toString().trim())
                current = StringBuilder(nextLine)
            }
        }
        if (current.isNotEmpty()) {
            result.add(current.toString().trim())
        }
        return result
    }

    /**
     * Segments text into grammatical sentences using standard library BreakIterator.
     */
    fun segmentSentences(text: String): List<String> {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return emptyList()

        val iterator = BreakIterator.getSentenceInstance(Locale.US)
        iterator.setText(trimmed)
        val sentences = mutableListOf<String>()
        var start = iterator.first()
        var end = iterator.next()

        while (end != BreakIterator.DONE) {
            val sentence = trimmed.substring(start, end).trim()
            if (sentence.isNotEmpty()) {
                sentences.add(sentence)
            }
            start = end
            end = iterator.next()
        }

        return if (sentences.isNotEmpty()) sentences else listOf(trimmed)
    }

    // ── On-Device ML Kit Entity Extraction Hook ──

    /**
     * Extracts ML Kit Entity Annotations on-device if the model is available.
     */
    suspend fun tryExtractEntitiesWithMlKit(text: String): List<EntityAnnotation> {
        return try {
            val entityExtractor = EntityExtraction.getClient(
                EntityExtractorOptions.Builder(EntityExtractorOptions.ENGLISH).build()
            )
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

    // ── Full Pipeline Entry Points ──

    /**
     * Synchronously parses ML Kit Vision Text recognition output into a hierarchical [ParsedItineraryResult].
     */
    fun parse(
        visionText: Text,
        entityAnnotations: List<EntityAnnotation> = emptyList()
    ): ParsedItineraryResult {
        val sortedText = sortSpatially(visionText)
        return parse(sortedText, entityAnnotations)
    }

    /**
     * Synchronously parses spatially-sorted text stream into a hierarchical [ParsedItineraryResult].
     */
    fun parse(
        spatiallySortedText: String,
        entityAnnotations: List<EntityAnnotation> = emptyList()
    ): ParsedItineraryResult {
        val (tripTitle, dayChunks) = chunkByDays(spatiallySortedText)
        val parsedDays = dayChunks.map { chunk ->
            classifyDayChunk(chunk, entityAnnotations)
        }
        return ParsedItineraryResult(
            tripTitle = tripTitle,
            days = parsedDays
        )
    }

    /**
     * Asynchronously parses text with on-device ML Kit Entity Extraction for addresses.
     */
    suspend fun parseWithEntities(
        spatiallySortedText: String
    ): ParsedItineraryResult {
        val entities = tryExtractEntitiesWithMlKit(spatiallySortedText)
        return parse(spatiallySortedText, entities)
    }

    /**
     * Asynchronously parses ML Kit Vision Text with on-device ML Kit Entity Extraction.
     */
    suspend fun parseWithEntities(
        visionText: Text
    ): ParsedItineraryResult {
        val sortedText = sortSpatially(visionText)
        val entities = tryExtractEntitiesWithMlKit(sortedText)
        return parse(sortedText, entities)
    }

    // ── Helper Methods ──

    private fun normalizeDayLabel(anchor: String): String {
        val trimmed = anchor.trim()
        val dayNumMatcher = Pattern.compile("(?i)^day\\s*-?\\s*(\\d+)").matcher(trimmed)
        return if (dayNumMatcher.find()) {
            "Day ${dayNumMatcher.group(1)}"
        } else {
            trimmed.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
    }

    private fun removeAnchorPrefix(line: String, anchor: String): String {
        // Strip matched anchor and common trailing delimiters like ":", "-", "–", "|"
        var remainder = line
        val anchorIndex = remainder.indexOf(anchor, ignoreCase = true)
        if (anchorIndex != -1) {
            remainder = remainder.substring(anchorIndex + anchor.length)
        }
        return remainder.trimStart(' ', ':', '-', '–', '—', '|', ',', '.')
            .trim()
    }

    private fun isPlaceToVisit(line: String): Boolean {
        // Condition A: Starts with list markers: bullet points (•, -, *), checkmarks, or numbers (1., 2.)
        if (LIST_MARKER_REGEX.matcher(line).find()) return true

        // Condition B: Contains POI indicators (e.g., "Visit", "Temple", "Museum", "Beach", "Park", "Tower", "Palace", "Market", "Street")
        if (POI_REGEX.matcher(line).find()) return true

        return false
    }

    private fun isCandidateLocation(line: String): Boolean {
        val trimmed = line.trim()
        if (trimmed.isBlank() || trimmed.length > 50) return false
        if (LIST_MARKER_REGEX.matcher(trimmed).find()) return false
        if (POI_REGEX.matcher(trimmed).find()) return false

        val actionVerbPrefix = Pattern.compile(
            "(?i)^(?:wear|bring|pack|remember|don't|do not|check|meet|take|enjoy|start|finish|call|book|note|tip|ensure|make sure)\\b"
        )
        if (actionVerbPrefix.matcher(trimmed).find()) return false
        if (trimmed.endsWith(".") && trimmed.count { it == ' ' } > 2) return false
        return true
    }

    private fun cleanPlaceName(line: String): String {
        val cleaned = line.replaceFirst(Regex("^[•\\-*▪▫–—✦·✓✔☑√\\s]*(\\d+[.)]|[•\\-*▪▫–—✦·✓✔☑√])\\s*"), "")
            .trim()
        return cleaned.ifBlank { line.trim() }
    }

    private fun extractDate(text: String): String? {
        val matcher = DATE_EXTRACT_REGEX.matcher(text)
        return if (matcher.find()) {
            matcher.group().trim()
        } else null
    }
}
