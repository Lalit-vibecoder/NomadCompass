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
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.RadioButtonUnchecked
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.nomadcompass.domain.model.DraftItineraryEvent
import com.example.nomadcompass.domain.model.ItineraryCategory
import com.example.nomadcompass.domain.model.ItineraryEvent
import com.example.nomadcompass.ui.theme.Background
import com.example.nomadcompass.ui.theme.LocalThemeController
import com.example.nomadcompass.ui.theme.OnPrimary
import com.example.nomadcompass.ui.theme.OnSurface
import com.example.nomadcompass.ui.theme.OnSurfaceVariant
import com.example.nomadcompass.ui.theme.Primary
import com.example.nomadcompass.ui.theme.PrimaryContainer
import com.example.nomadcompass.ui.theme.SafetyGreen
import com.example.nomadcompass.ui.theme.Secondary
import com.example.nomadcompass.ui.theme.SecondaryContainer
import com.example.nomadcompass.ui.theme.SurfaceContainer
import com.example.nomadcompass.ui.theme.SurfaceContainerHigh
import com.example.nomadcompass.ui.theme.SurfaceContainerLow
import com.example.nomadcompass.util.extractor.OfflineDocumentExtractor
import com.example.nomadcompass.util.extractor.OfflineTravelHeuristicParser
import kotlinx.coroutines.launch

private enum class ExtractorSourceTab(val title: String, val emoji: String) {
    PASTE_TEXT("Paste Text", "📝"),
    PDF_DOC("PDF / Doc", "📄"),
    SCREENSHOT("Screenshot", "🖼️"),
    HTML_FILE("Saved Web / HTML", "🌐")
}

@Composable
fun ItineraryExtractorModal(
    tripId: Long,
    destinationName: String,
    onDismiss: () -> Unit,
    onSaveEvents: (List<ItineraryEvent>) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isDark = LocalThemeController.current.isDarkMode

    var activeTab by remember { mutableStateOf(ExtractorSourceTab.PASTE_TEXT) }
    var pastedText by remember { mutableStateOf("") }
    var isExtracting by remember { mutableStateOf(false) }
    var extractionError by remember { mutableStateOf<String?>(null) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    // List of parsed draft events during review step
    val draftEvents = remember { mutableStateListOf<DraftItineraryEvent>() }
    var isReviewMode by remember { mutableStateOf(false) }

    // File pickers
    val pdfPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                isExtracting = true
                extractionError = null
                statusMessage = "Extracting text from digital PDF on-device..."
                val result = OfflineDocumentExtractor.extractFromPdf(context, uri)
                result.onSuccess { text ->
                    statusMessage = "Parsing travel entities..."
                    val parsed = OfflineTravelHeuristicParser.parse(text, destinationName)
                    draftEvents.clear()
                    draftEvents.addAll(parsed)
                    isReviewMode = true
                    isExtracting = false
                }.onFailure { err ->
                    isExtracting = false
                    extractionError = err.message ?: "Failed to extract PDF"
                }
            }
        }
    }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                isExtracting = true
                extractionError = null
                statusMessage = "Running On-Device OCR on image screenshot..."
                val result = OfflineDocumentExtractor.extractFromImage(context, uri)
                result.onSuccess { text ->
                    statusMessage = "Recognizing flights, hotels & travel dates..."
                    val parsed = OfflineTravelHeuristicParser.parse(text, destinationName)
                    draftEvents.clear()
                    draftEvents.addAll(parsed)
                    isReviewMode = true
                    isExtracting = false
                }.onFailure { err ->
                    isExtracting = false
                    extractionError = err.message ?: "Failed to run OCR on image"
                }
            }
        }
    }

    val htmlPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                isExtracting = true
                extractionError = null
                statusMessage = "Parsing local HTML file nodes on-device..."
                val result = OfflineDocumentExtractor.extractFromHtml(context, uri)
                result.onSuccess { text ->
                    statusMessage = "Parsing travel entities..."
                    val parsed = OfflineTravelHeuristicParser.parse(text, destinationName)
                    draftEvents.clear()
                    draftEvents.addAll(parsed)
                    isReviewMode = true
                    isExtracting = false
                }.onFailure { err ->
                    isExtracting = false
                    extractionError = err.message ?: "Failed to parse HTML"
                }
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background.copy(alpha = 0.95f))
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
                    // Header Bar
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
                                    text = if (isReviewMode) "Review Extracted Itinerary" else "Offline Itinerary Extractor",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = OnSurface,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "100% On-Device • Private & Local",
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
                        // ── STEP 1: Multi-Source Input Selection ──
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
                                val isSelected = tab == activeTab
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
                                            activeTab = tab
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
                            when (activeTab) {
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
                                                    "Example:\nFlight SQ 321 from SIN to LHR on 15 Oct 2026 at 09:00, PNR: 7J9KL2\nStay at Grand Hyatt Bali, Check-in: 2026-10-18...",
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
                                                        val parsed = OfflineTravelHeuristicParser.parse(text, destinationName)
                                                        draftEvents.clear()
                                                        draftEvents.addAll(parsed)
                                                        isReviewMode = true
                                                        isExtracting = false
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
                                ExtractorSourceTab.PDF_DOC -> {
                                    SourcePickerCard(
                                        icon = Icons.Default.Description,
                                        title = "Pick Digital PDF Document",
                                        subtitle = "Extracts flight tickets, e-visas, or travel vouchers on-device without cloud uploads.",
                                        buttonText = "Select PDF File",
                                        onSelect = { pdfPicker.launch("application/pdf") }
                                    )
                                }
                                ExtractorSourceTab.SCREENSHOT -> {
                                    SourcePickerCard(
                                        icon = Icons.Default.Image,
                                        title = "Pick Screenshot / Photo",
                                        subtitle = "Runs Google ML Kit on-device OCR to extract text from airline apps, booking passes, or screenshots.",
                                        buttonText = "Select Image Screenshot",
                                        onSelect = { imagePicker.launch("image/*") }
                                    )
                                }
                                ExtractorSourceTab.HTML_FILE -> {
                                    SourcePickerCard(
                                        icon = Icons.Default.Language,
                                        title = "Pick Saved HTML File",
                                        subtitle = "Parses web itineraries, saved booking pages, or local HTML documents via Jsoup.",
                                        buttonText = "Select HTML File",
                                        onSelect = { htmlPicker.launch("text/html") }
                                    )
                                }
                            }
                        }
                    } else {
                        // ── STEP 2: Draft Review & Confirmation Screen ──
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Found ${draftEvents.size} Itinerary Items (${draftEvents.count { it.isSelected }} selected)",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant
                            )

                            TextButton(
                                onClick = {
                                    draftEvents.add(
                                        DraftItineraryEvent(
                                            title = "New Activity",
                                            category = ItineraryCategory.ACTIVITY,
                                            location = destinationName,
                                            startDateTime = "",
                                            endDateTime = "",
                                            confirmationCode = ""
                                        )
                                    )
                                }
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Primary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Item", color = Primary, style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            itemsIndexed(draftEvents, key = { _, item -> item.id }) { index, item ->
                                DraftEventItemCard(
                                    draft = item,
                                    onUpdate = { updated -> draftEvents[index] = updated },
                                    onDelete = { draftEvents.removeAt(index) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Action Buttons
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
                                    val confirmed = draftEvents.filter { it.isSelected }.map { draft ->
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
                                    onSaveEvents(confirmed)
                                    onDismiss()
                                },
                                modifier = Modifier.weight(2f),
                                contentPadding = PaddingValues(vertical = 12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Save Confirmed Events (${draftEvents.count { it.isSelected }})", color = Primary, fontWeight = FontWeight.Bold)
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

@Composable
private fun SourcePickerCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    buttonText: String,
    onSelect: () -> Unit,
) {
    val isDark = LocalThemeController.current.isDarkMode

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
                modifier = Modifier.padding(horizontal = 16.dp)
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
    val isDark = LocalThemeController.current.isDarkMode
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
