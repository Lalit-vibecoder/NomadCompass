package com.example.nomadcompass.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.nomadcompass.domain.model.DraftItineraryEvent
import com.example.nomadcompass.domain.model.ItineraryCategory
import com.example.nomadcompass.domain.model.ItineraryEvent
import com.example.nomadcompass.ui.theme.LocalAppBackground
import com.example.nomadcompass.ui.theme.LocalThemeController
import com.example.nomadcompass.ui.theme.OnPrimary
import com.example.nomadcompass.ui.theme.OnSurface
import com.example.nomadcompass.ui.theme.OnSurfaceVariant
import com.example.nomadcompass.ui.theme.Primary
import com.example.nomadcompass.ui.theme.PrimaryContainer
import com.example.nomadcompass.ui.theme.Secondary
import com.example.nomadcompass.ui.theme.SurfaceContainer
import com.example.nomadcompass.ui.theme.SurfaceContainerHigh
import com.example.nomadcompass.ui.theme.SurfaceContainerLow
import com.example.nomadcompass.util.extractor.CategorizedItineraryText
import com.example.nomadcompass.util.extractor.ItineraryCategorizer
import com.example.nomadcompass.util.extractor.OfflineDocumentExtractor
import com.example.nomadcompass.util.extractor.OfflineTravelHeuristicParser
import kotlinx.coroutines.launch

private enum class ExtractorSourceTab(val title: String, val emoji: String) {
    SCREENSHOT("Screenshot / Photo", "🖼️"),
    PDF_DOC("PDF Document", "📄"),
    PASTE_TEXT("Paste Text", "📝"),
    HTML_FILE("Saved Web / HTML", "🌐")
}

private enum class ReviewModeTab(val title: String, val emoji: String) {
    ALL("All Sections", "📑"),
    FLIGHTS("Flights", "✈️"),
    ACCOMMODATIONS("Accommodations", "🏨"),
    PLANS("Plans", "🎯")
}

private enum class ReviewDisplayFormat {
    EDITABLE_TEXT,
    STRUCTURED_CARDS
}

@Composable
fun ItineraryExtractorModal(
    tripId: Long,
    destinationName: String,
    bgPhotoUri: String? = LocalAppBackground.current.bgPhotoUri,
    bgBlurRadius: Float = LocalAppBackground.current.bgBlurRadius,
    onDismiss: () -> Unit,
    onSaveEvents: (List<ItineraryEvent>) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isDark = LocalThemeController.current.isDarkMode

    var activeSourceTab by remember { mutableStateOf(ExtractorSourceTab.SCREENSHOT) }
    var pastedText by remember { mutableStateOf("") }
    var isExtracting by remember { mutableStateOf(false) }
    var extractionError by remember { mutableStateOf<String?>(null) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    // Categorized Itinerary text state
    var categorizedText by remember { mutableStateOf(CategorizedItineraryText()) }
    var fullEditableText by remember { mutableStateOf("") }
    var activeReviewSection by remember { mutableStateOf(ReviewModeTab.ALL) }
    var reviewFormat by remember { mutableStateOf(ReviewDisplayFormat.EDITABLE_TEXT) }

    // List of parsed draft events for structured cards review
    val draftEvents = remember { mutableStateListOf<DraftItineraryEvent>() }
    var isReviewMode by remember { mutableStateOf(false) }

    // Helper to process extracted raw text into categorized sections and draft events
    fun processExtractedText(rawExtracted: String) {
        scope.launch {
            statusMessage = "Categorizing into Flights, Accommodations & Plans..."
            val categorized = ItineraryCategorizer.categorizeRawText(rawExtracted)
            categorizedText = categorized
            fullEditableText = categorized.toFormattedString()

            statusMessage = "Structuring itinerary draft events..."
            val parsed = ItineraryCategorizer.parseToDraftEvents(categorized, destinationName)
            draftEvents.clear()
            draftEvents.addAll(parsed)

            isReviewMode = true
            isExtracting = false
            statusMessage = null
        }
    }

    // PDF Document Picker using PDFBox Android
    val pdfPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                isExtracting = true
                extractionError = null
                statusMessage = "Extracting text from digital PDF on-device (PDFBox)..."
                val result = OfflineDocumentExtractor.extractFromPdf(context, uri)
                result.onSuccess { text ->
                    processExtractedText(text)
                }.onFailure { err ->
                    isExtracting = false
                    extractionError = err.message ?: "Failed to extract PDF"
                }
            }
        }
    }

    // Image Picker using Google ML Kit OCR (screenshots and photos)
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                isExtracting = true
                extractionError = null
                statusMessage = "Running Google ML Kit On-Device OCR on image..."
                val result = OfflineDocumentExtractor.extractFromImage(context, uri)
                result.onSuccess { text ->
                    processExtractedText(text)
                }.onFailure { err ->
                    isExtracting = false
                    extractionError = err.message ?: "Failed to recognize text in image"
                }
            }
        }
    }

    // HTML Picker using Jsoup
    val htmlPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                isExtracting = true
                extractionError = null
                statusMessage = "Parsing HTML document nodes on-device..."
                val result = OfflineDocumentExtractor.extractFromHtml(context, uri)
                result.onSuccess { text ->
                    processExtractedText(text)
                }.onFailure { err ->
                    isExtracting = false
                    extractionError = err.message ?: "Failed to parse HTML"
                }
            }
        }
    }

    // Helper to refresh parsed events when user edits text
    fun syncDraftEventsFromText() {
        scope.launch {
            val updated = ItineraryCategorizer.parseToDraftEvents(categorizedText, destinationName)
            draftEvents.clear()
            draftEvents.addAll(updated)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        AppBackground(
            bgPhotoUri = bgPhotoUri,
            blurRadius = bgBlurRadius
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                ClayCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.96f),
                    cornerRadius = 28.dp,
                    backgroundColor = SurfaceContainer
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                    ) {
                        // ── HEADER BAR ──
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Primary.copy(alpha = 0.15f))
                                        .border(1.dp, Primary.copy(alpha = 0.35f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = Primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = if (isReviewMode) "Review & Adjust Itinerary" else "Offline Itinerary Extractor",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = OnSurface,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Google ML Kit OCR & PDFBox • 100% On-Device",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Primary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            IconButton(onClick = onDismiss) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = OnSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (!isReviewMode) {
                            // ── STEP 1: EXTRACTION SOURCE SELECTION ──
                            Text(
                                text = "Select Document Source:",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariant,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Source Tabs
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(ExtractorSourceTab.entries) { tab ->
                                    val isSelected = tab == activeSourceTab
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(9999.dp))
                                            .background(
                                                if (isSelected) PrimaryContainer else Color.White.copy(alpha = if (isDark) 0.10f else 0.18f)
                                            )
                                            .border(
                                                width = 1.dp,
                                                color = if (isSelected) Primary else Color.White.copy(alpha = if (isDark) 0.20f else 0.35f),
                                                shape = RoundedCornerShape(9999.dp)
                                            )
                                            .clickable {
                                                activeSourceTab = tab
                                                extractionError = null
                                            }
                                            .padding(horizontal = 14.dp, vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${tab.emoji} ${tab.title}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isSelected) OnPrimary else OnSurface,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Tab Ingestion Content
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            ) {
                                when (activeSourceTab) {
                                    ExtractorSourceTab.SCREENSHOT -> {
                                        SourcePickerCard(
                                            icon = Icons.Default.Image,
                                            title = "Screenshots & Photos (ML Kit)",
                                            subtitle = "Extracts flight tickets, boarding passes, and hotel vouchers using Google ML Kit on-device text recognition.",
                                            buttonText = "Select Screenshot / Photo",
                                            onSelect = { imagePicker.launch("image/*") }
                                        )
                                    }
                                    ExtractorSourceTab.PDF_DOC -> {
                                        SourcePickerCard(
                                            icon = Icons.Default.Description,
                                            title = "PDF Travel Documents (PDFBox)",
                                            subtitle = "Extracts text from digital PDF flight itineraries, e-visas, or travel vouchers on-device without cloud uploads.",
                                            buttonText = "Select PDF File",
                                            onSelect = { pdfPicker.launch("application/pdf") }
                                        )
                                    }
                                    ExtractorSourceTab.PASTE_TEXT -> {
                                        Column(modifier = Modifier.fillMaxSize()) {
                                            Text(
                                                text = "Paste email confirmations, flight details, or hotel booking text below:",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = OnSurfaceVariant
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            OutlinedTextField(
                                                value = pastedText,
                                                onValueChange = { pastedText = it },
                                                placeholder = {
                                                    Text(
                                                        "Example:\nFlight SQ 321 from SIN to LHR on 15 Oct 2026 at 09:00, PNR: 7J9KL2\nStay at Grand Hyatt Bali, Check-in: 2026-10-18, Check-out: 2026-10-25\nTemple Tour Uluwatu on 2026-10-19 at 14:00...",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = OnSurfaceVariant.copy(alpha = 0.5f)
                                                    )
                                                },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(16.dp))
                                                    .background(SurfaceContainerLow),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = Primary,
                                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                                    focusedTextColor = OnSurface,
                                                    unfocusedTextColor = OnSurface,
                                                    cursorColor = Primary
                                                )
                                            )
                                            Spacer(modifier = Modifier.height(14.dp))
                                            GlassPillButton(
                                                onClick = {
                                                    scope.launch {
                                                        isExtracting = true
                                                        extractionError = null
                                                        statusMessage = "Parsing travel text on-device..."
                                                        val result = OfflineDocumentExtractor.extractFromRawText(pastedText)
                                                        result.onSuccess { text ->
                                                            processExtractedText(text)
                                                        }.onFailure { err ->
                                                            isExtracting = false
                                                            extractionError = err.message ?: "Failed to parse text"
                                                        }
                                                    }
                                                },
                                                modifier = Modifier.fillMaxWidth(),
                                                contentPadding = PaddingValues(vertical = 14.dp)
                                            ) {
                                                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("Parse & Extract Itinerary", color = Primary, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                    ExtractorSourceTab.HTML_FILE -> {
                                        SourcePickerCard(
                                            icon = Icons.Default.Language,
                                            title = "Saved Web / HTML File",
                                            subtitle = "Parses web itineraries, saved booking pages, or local HTML documents via Jsoup.",
                                            buttonText = "Select HTML File",
                                            onSelect = { htmlPicker.launch("text/html") }
                                        )
                                    }
                                }
                            }
                        } else {
                            // ── STEP 2: CATEGORIZED REVIEW & EDITABLE TEXT FIELD ──

                            // Top Info Bar: Category stats + View Mode Switcher
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val flightsCount = draftEvents.count { it.category == ItineraryCategory.FLIGHT }
                                    val hotelCount = draftEvents.count { it.category == ItineraryCategory.ACCOMMODATION }
                                    val plansCount = draftEvents.count { it.category != ItineraryCategory.FLIGHT && it.category != ItineraryCategory.ACCOMMODATION }

                                    Text(
                                        text = "✈️ $flightsCount  •  🏨 $hotelCount  •  🎯 $plansCount",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                // Toggle: Text Editor vs Cards
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(9999.dp))
                                        .background(Color.White.copy(alpha = if (isDark) 0.10f else 0.20f))
                                        .border(1.dp, Primary.copy(alpha = 0.35f), RoundedCornerShape(9999.dp))
                                        .padding(2.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(9999.dp))
                                            .background(if (reviewFormat == ReviewDisplayFormat.EDITABLE_TEXT) PrimaryContainer else Color.Transparent)
                                            .clickable { reviewFormat = ReviewDisplayFormat.EDITABLE_TEXT }
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.EditNote,
                                                contentDescription = null,
                                                tint = if (reviewFormat == ReviewDisplayFormat.EDITABLE_TEXT) OnPrimary else OnSurface,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Text Editor",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontSize = 11.sp,
                                                color = if (reviewFormat == ReviewDisplayFormat.EDITABLE_TEXT) OnPrimary else OnSurface,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(9999.dp))
                                            .background(if (reviewFormat == ReviewDisplayFormat.STRUCTURED_CARDS) PrimaryContainer else Color.Transparent)
                                            .clickable {
                                                syncDraftEventsFromText()
                                                reviewFormat = ReviewDisplayFormat.STRUCTURED_CARDS
                                            }
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.ViewAgenda,
                                                contentDescription = null,
                                                tint = if (reviewFormat == ReviewDisplayFormat.STRUCTURED_CARDS) OnPrimary else OnSurface,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Cards (${draftEvents.size})",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontSize = 11.sp,
                                                color = if (reviewFormat == ReviewDisplayFormat.STRUCTURED_CARDS) OnPrimary else OnSurface,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Categorized Section Selector Tabs (Flights, Accommodations, Plans, All)
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(ReviewModeTab.entries) { tab ->
                                    val isSelected = tab == activeReviewSection
                                    val badgeCount = when (tab) {
                                        ReviewModeTab.ALL -> draftEvents.size
                                        ReviewModeTab.FLIGHTS -> draftEvents.count { it.category == ItineraryCategory.FLIGHT }
                                        ReviewModeTab.ACCOMMODATIONS -> draftEvents.count { it.category == ItineraryCategory.ACCOMMODATION }
                                        ReviewModeTab.PLANS -> draftEvents.count { it.category != ItineraryCategory.FLIGHT && it.category != ItineraryCategory.ACCOMMODATION }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(9999.dp))
                                            .background(
                                                if (isSelected) PrimaryContainer else Color.White.copy(alpha = if (isDark) 0.10f else 0.18f)
                                            )
                                            .border(
                                                width = 1.dp,
                                                color = if (isSelected) Primary else Color.White.copy(alpha = if (isDark) 0.20f else 0.35f),
                                                shape = RoundedCornerShape(9999.dp)
                                            )
                                            .clickable {
                                                activeReviewSection = tab
                                            }
                                            .padding(horizontal = 12.dp, vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${tab.emoji} ${tab.title} ($badgeCount)",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isSelected) OnPrimary else OnSurface,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Main Workspace: Editable Text Field OR Structured Cards
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            ) {
                                if (reviewFormat == ReviewDisplayFormat.EDITABLE_TEXT) {
                                    // ── EDITABLE TEXT FIELD VIEW ──
                                    Column(modifier = Modifier.fillMaxSize()) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = when (activeReviewSection) {
                                                    ReviewModeTab.ALL -> "Review and adjust all categorized sections below:"
                                                    ReviewModeTab.FLIGHTS -> "Review and adjust extracted Flights:"
                                                    ReviewModeTab.ACCOMMODATIONS -> "Review and adjust extracted Accommodations:"
                                                    ReviewModeTab.PLANS -> "Review and adjust extracted Plans & Activities:"
                                                },
                                                style = MaterialTheme.typography.bodySmall,
                                                color = OnSurfaceVariant
                                            )

                                            // Re-parse button
                                            TextButton(
                                                onClick = { syncDraftEventsFromText() },
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = Primary, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Update Cards", color = Primary, style = MaterialTheme.typography.labelSmall)
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        // Determine which text is being edited in this view
                                        val currentTextToEdit = when (activeReviewSection) {
                                            ReviewModeTab.ALL -> fullEditableText
                                            ReviewModeTab.FLIGHTS -> categorizedText.flightsText
                                            ReviewModeTab.ACCOMMODATIONS -> categorizedText.accommodationsText
                                            ReviewModeTab.PLANS -> categorizedText.plansText
                                        }

                                        val placeholderText = when (activeReviewSection) {
                                            ReviewModeTab.ALL -> "=== FLIGHTS ===\nFlight SQ 321, 2026-10-15 09:00, PNR: 7J9KL2\n\n=== ACCOMMODATIONS ===\nGrand Hyatt Bali, Check-in: 2026-10-18\n\n=== PLANS ===\nTemple tour at 14:00..."
                                            ReviewModeTab.FLIGHTS -> "Enter or adjust flight legs:\nFlight SQ 321 from SIN to LHR on 15 Oct 2026 at 09:00, PNR: 7J9KL2"
                                            ReviewModeTab.ACCOMMODATIONS -> "Enter or adjust hotel details:\nGrand Hyatt Bali, Check-in: 2026-10-18, Check-out: 2026-10-25"
                                            ReviewModeTab.PLANS -> "Enter or adjust tours, activities, or trains:\nUluwatu Temple Tour on 2026-10-19 at 14:00"
                                        }

                                        OutlinedTextField(
                                            value = currentTextToEdit,
                                            onValueChange = { newText ->
                                                when (activeReviewSection) {
                                                    ReviewModeTab.ALL -> {
                                                        fullEditableText = newText
                                                        categorizedText = ItineraryCategorizer.parseFromFormattedString(newText)
                                                    }
                                                    ReviewModeTab.FLIGHTS -> {
                                                        categorizedText = categorizedText.copy(flightsText = newText)
                                                        fullEditableText = categorizedText.toFormattedString()
                                                    }
                                                    ReviewModeTab.ACCOMMODATIONS -> {
                                                        categorizedText = categorizedText.copy(accommodationsText = newText)
                                                        fullEditableText = categorizedText.toFormattedString()
                                                    }
                                                    ReviewModeTab.PLANS -> {
                                                        categorizedText = categorizedText.copy(plansText = newText)
                                                        fullEditableText = categorizedText.toFormattedString()
                                                    }
                                                }
                                            },
                                            placeholder = {
                                                Text(
                                                    text = placeholderText,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = OnSurfaceVariant.copy(alpha = 0.5f)
                                                )
                                            },
                                            textStyle = TextStyle(
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 13.sp,
                                                lineHeight = 18.sp,
                                                color = OnSurface
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f)
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(SurfaceContainerLow),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = Primary,
                                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                                focusedTextColor = OnSurface,
                                                unfocusedTextColor = OnSurface,
                                                cursorColor = Primary
                                            )
                                        )
                                    }
                                } else {
                                    // ── STRUCTURED CARDS VIEW ──
                                    val filteredCards = when (activeReviewSection) {
                                        ReviewModeTab.ALL -> draftEvents
                                        ReviewModeTab.FLIGHTS -> draftEvents.filter { it.category == ItineraryCategory.FLIGHT }
                                        ReviewModeTab.ACCOMMODATIONS -> draftEvents.filter { it.category == ItineraryCategory.ACCOMMODATION }
                                        ReviewModeTab.PLANS -> draftEvents.filter { it.category != ItineraryCategory.FLIGHT && it.category != ItineraryCategory.ACCOMMODATION }
                                    }

                                    if (filteredCards.isEmpty()) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text = "No events in ${activeReviewSection.title}",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = OnSurfaceVariant
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                TextButton(onClick = { reviewFormat = ReviewDisplayFormat.EDITABLE_TEXT }) {
                                                    Text("Switch to Text Editor to type or paste", color = Primary)
                                                }
                                            }
                                        }
                                    } else {
                                        LazyColumn(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            itemsIndexed(filteredCards, key = { _, item -> item.id }) { index, item ->
                                                DraftEventItemCard(
                                                    draft = item,
                                                    onUpdate = { updated ->
                                                        val mainIdx = draftEvents.indexOfFirst { it.id == item.id }
                                                        if (mainIdx != -1) draftEvents[mainIdx] = updated
                                                    },
                                                    onDelete = {
                                                        draftEvents.removeAll { it.id == item.id }
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // ── ACTION BUTTONS: Back to Ingest & Save ──
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                TextButton(
                                    onClick = { isReviewMode = false },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Back to Ingest", color = OnSurfaceVariant)
                                }

                                GlassPillButton(
                                    onClick = {
                                        scope.launch {
                                            // Always sync and save from latest adjusted state
                                            val confirmedDrafts = if (reviewFormat == ReviewDisplayFormat.EDITABLE_TEXT) {
                                                ItineraryCategorizer.parseToDraftEvents(categorizedText, destinationName)
                                            } else {
                                                draftEvents.filter { it.isSelected }
                                            }

                                            val confirmedEvents = confirmedDrafts.map { draft ->
                                                ItineraryEvent(
                                                    tripId = tripId,
                                                    title = draft.title.ifBlank { "Travel Event" },
                                                    category = draft.category,
                                                    location = draft.location,
                                                    startDateTime = draft.startDateTime,
                                                    endDateTime = draft.endDateTime,
                                                    confirmationCode = draft.confirmationCode,
                                                    notes = draft.notes
                                                )
                                            }

                                            onSaveEvents(confirmedEvents)
                                            onDismiss()
                                        }
                                    },
                                    modifier = Modifier.weight(2f),
                                    contentPadding = PaddingValues(vertical = 12.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Save Itinerary", color = Primary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Progress overlay during extraction
                        AnimatedVisibility(visible = isExtracting) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceContainerHigh)
                                    .border(1.dp, Primary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                    .padding(14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Primary)
                                    Text(
                                        text = statusMessage ?: "Extracting on-device...",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = OnSurface
                                    )
                                }
                            }
                        }

                        // Error feedback banner
                        AnimatedVisibility(visible = extractionError != null) {
                            extractionError?.let { err ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFE53935).copy(alpha = 0.15f))
                                        .border(1.dp, Color(0xFFE53935).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = "⚠️ $err",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFFF8A80)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SourcePickerCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    buttonText: String,
    onSelect: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceContainerLow)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Primary.copy(alpha = 0.15f))
                    .border(1.5.dp, Primary.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = Primary, modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = OnSurface,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            GlassPillButton(
                onClick = onSelect,
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(buttonText, color = Primary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun DraftEventItemCard(
    draft: DraftItineraryEvent,
    onUpdate: (DraftItineraryEvent) -> Unit,
    onDelete: () -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }

    ClayCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 16.dp,
        backgroundColor = if (draft.isSelected) SurfaceContainerLow else SurfaceContainerLow.copy(alpha = 0.4f)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Selection checkbox + Title + Category Pill + Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onUpdate(draft.copy(isSelected = !draft.isSelected)) }
                ) {
                    Icon(
                        imageVector = if (draft.isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (draft.isSelected) Primary else OnSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = draft.title.ifBlank { "Untitled Event" },
                        style = MaterialTheme.typography.titleSmall,
                        color = OnSurface,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Category Badge
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(draft.category.defaultColor.copy(alpha = 0.18f))
                            .border(1.dp, draft.category.defaultColor.copy(alpha = 0.35f), CircleShape)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${draft.category.emoji} ${draft.category.displayName}",
                            style = MaterialTheme.typography.labelSmall,
                            color = draft.category.defaultColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = OnSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sub-details summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (draft.startDateTime.isNotBlank()) {
                    Text(
                        text = "📅 ${draft.startDateTime}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Primary,
                        fontWeight = FontWeight.Medium
                    )
                }
                if (draft.confirmationCode.isNotBlank()) {
                    Text(
                        text = "🔖 ${draft.confirmationCode}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Secondary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (draft.location.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "📍 ${draft.location}",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
            }

            // Expandable Edit Fields
            TextButton(
                onClick = { isExpanded = !isExpanded },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = if (isExpanded) "Hide Edit Fields ▲" else "Edit Event Details ▼",
                    style = MaterialTheme.typography.labelSmall,
                    color = Primary
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Title Edit Field
                    OutlinedTextField(
                        value = draft.title,
                        onValueChange = { onUpdate(draft.copy(title = it)) },
                        label = { Text("Event Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )

                    // Category Selector
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(ItineraryCategory.entries) { cat ->
                            val isCatSelected = cat == draft.category
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (isCatSelected) cat.defaultColor else Color.White.copy(alpha = 0.08f))
                                    .border(1.dp, cat.defaultColor.copy(alpha = 0.4f), CircleShape)
                                    .clickable { onUpdate(draft.copy(category = cat)) }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "${cat.emoji} ${cat.displayName}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isCatSelected) OnPrimary else OnSurface,
                                    fontSize = 10.sp,
                                    fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    // Location Field
                    OutlinedTextField(
                        value = draft.location,
                        onValueChange = { onUpdate(draft.copy(location = it)) },
                        label = { Text("Location / Route") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )

                    // Date & Time Fields
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = draft.startDateTime,
                            onValueChange = { onUpdate(draft.copy(startDateTime = it)) },
                            label = { Text("Start Date/Time") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            )
                        )
                        OutlinedTextField(
                            value = draft.endDateTime,
                            onValueChange = { onUpdate(draft.copy(endDateTime = it)) },
                            label = { Text("End Date/Time") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            )
                        )
                    }

                    // Confirmation Code Field
                    OutlinedTextField(
                        value = draft.confirmationCode,
                        onValueChange = { onUpdate(draft.copy(confirmationCode = it)) },
                        label = { Text("Confirmation Code / PNR") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                }
            }
        }
    }
}
