package com.example.nomadcompass.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import com.example.nomadcompass.data.local.entity.ExpenseCategory
import com.example.nomadcompass.domain.model.AttachmentType
import com.example.nomadcompass.domain.model.Expense
import com.example.nomadcompass.domain.model.ItineraryCategory
import com.example.nomadcompass.domain.model.ItineraryEvent
import com.example.nomadcompass.domain.model.PackingItem
import com.example.nomadcompass.domain.model.Trip
import com.example.nomadcompass.domain.model.TripAttachment
import com.example.nomadcompass.ui.screens.planner.WorkspaceTab
import com.example.nomadcompass.ui.theme.Background
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
import com.example.nomadcompass.util.FileStorageHelper
import java.io.File
import java.util.Locale

data class PendingFileAttachment(
    val uri: Uri,
    val type: AttachmentType,
    val initialTitle: String,
)

@Composable
fun TripWorkspaceModal(
    trip: Trip,
    attachments: List<TripAttachment>,
    expenses: List<Expense> = emptyList(),
    packingItems: List<PackingItem> = emptyList(),
    itineraryEvents: List<ItineraryEvent> = emptyList(),
    totalSpentHome: Double = 0.0,
    activeTab: WorkspaceTab = WorkspaceTab.EXPENSES,
    currencyCode: String = "USD",
    tripDestinationCurrencyCode: String = "",
    onSelectTab: (WorkspaceTab) -> Unit = {},
    onAddExpense: (title: String, amountLocal: Double, currencyCode: String, category: ExpenseCategory, notes: String) -> Unit = { _, _, _, _, _ -> },
    onDeleteExpense: (Long) -> Unit = {},
    onCalculateLivePreview: (amountLocal: Double, currencyCode: String, callback: (Double, Boolean) -> Unit) -> Unit = { _, _, _ -> },
    onTogglePackingItem: (PackingItem) -> Unit = {},
    onAddPackingItem: (String) -> Unit = {},
    onDeletePackingItem: (Long) -> Unit = {},
    onSaveItineraryEvents: (List<ItineraryEvent>) -> Unit = {},
    onAddItineraryEvent: (ItineraryEvent) -> Unit = {},
    onUpdateItineraryEvent: (ItineraryEvent) -> Unit = {},
    onDeleteItineraryEvent: (Long) -> Unit = {},
    onAddFileAttachment: (Uri, AttachmentType, String) -> Unit,
    onAddNoteAttachment: (title: String, text: String) -> Unit,
    onUpdateNoteAttachment: (id: Long, title: String, text: String) -> Unit,
    onMoveAttachment: (fromIndex: Int, toIndex: Int) -> Unit,
    onDeleteAttachment: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    var isAddChoiceMenuOpen by remember { mutableStateOf(false) }
    var isAddNoteDialogOpen by remember { mutableStateOf(false) }
    var isAddPackingDialogOpen by remember { mutableStateOf(false) }
    var isExtractorModalOpen by remember { mutableStateOf(false) }
    var isAddEventDialogOpen by remember { mutableStateOf(false) }
    var editingItineraryEvent by remember { mutableStateOf<ItineraryEvent?>(null) }
    var editingNote by remember { mutableStateOf<TripAttachment?>(null) }
    var pendingFileAttachment by remember { mutableStateOf<PendingFileAttachment?>(null) }

    // Pickers for PDF and Image
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val defaultTitle = FileStorageHelper.getFileNameFromUri(context, uri)
            pendingFileAttachment = PendingFileAttachment(uri, AttachmentType.PDF, defaultTitle)
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val defaultTitle = FileStorageHelper.getFileNameFromUri(context, uri)
            pendingFileAttachment = PendingFileAttachment(uri, AttachmentType.IMAGE, defaultTitle)
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
                    .fillMaxHeight(0.94f),
                cornerRadius = 28.dp,
                backgroundColor = SurfaceContainer
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
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
                                if (trip.flagUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = trip.flagUrl,
                                        contentDescription = trip.countryName,
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                } else {
                                    Text(text = trip.flagEmoji, fontSize = 28.sp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                }
                                Column {
                                    Text(
                                        text = trip.countryName,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = OnSurface,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Trip Workspace & Planning Hub",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = OnSurfaceVariant
                                    )
                                }
                            }

                            IconButton(onClick = onDismiss) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close workspace",
                                    tint = OnSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Sub-Tab Navigation Header Row
                        WorkspaceTabSelectorRow(
                            activeTab = activeTab,
                            onTabSelected = onSelectTab
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Tab Content Area
                        when (activeTab) {
                            WorkspaceTab.EXPENSES -> {
                                ExpensesTab(
                                    expenses = expenses,
                                    totalSpentHome = totalSpentHome,
                                    tripBudgetHome = trip.budgetUsd,
                                    homeCurrencyCode = currencyCode,
                                    tripDestinationCurrencyCode = tripDestinationCurrencyCode,
                                    onAddExpense = onAddExpense,
                                    onDeleteExpense = onDeleteExpense,
                                    onCalculateLivePreview = onCalculateLivePreview
                                )
                            }

                            WorkspaceTab.DOCS -> {
                                DocsTabContent(
                                    trip = trip,
                                    attachments = attachments,
                                    currencyCode = currencyCode,
                                    onAddChoiceClick = { isAddChoiceMenuOpen = true },
                                    onMoveAttachment = onMoveAttachment,
                                    onEditNote = { editingNote = it },
                                    onDeleteAttachment = onDeleteAttachment,
                                    onOpenFile = { path, mime -> FileStorageHelper.openFile(context, path, mime) }
                                )
                            }

                            WorkspaceTab.ITINERARY -> {
                                ItineraryTabContent(
                                    trip = trip,
                                    itineraryEvents = itineraryEvents,
                                    onOpenExtractor = { isExtractorModalOpen = true },
                                    onAddEventClick = {
                                        editingItineraryEvent = null
                                        isAddEventDialogOpen = true
                                    },
                                    onEditEvent = { event ->
                                        editingItineraryEvent = event
                                        isAddEventDialogOpen = true
                                    },
                                    onDeleteEvent = onDeleteItineraryEvent
                                )
                            }

                            WorkspaceTab.PACKING -> {
                                PackingTabContent(
                                    packingItems = packingItems,
                                    onTogglePackingItem = onTogglePackingItem,
                                    onAddPackingItemClick = { isAddPackingDialogOpen = true },
                                    onDeletePackingItem = onDeletePackingItem
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Offline Itinerary Extractor Modal
    if (isExtractorModalOpen) {
        ItineraryExtractorModal(
            tripId = trip.id.toLong(),
            destinationName = trip.countryName,
            onDismiss = { isExtractorModalOpen = false },
            onSaveEvents = { events ->
                onSaveItineraryEvents(events)
                isExtractorModalOpen = false
            }
        )
    }

    // Add / Edit Manual Itinerary Event Dialog
    if (isAddEventDialogOpen) {
        AddEditItineraryEventDialog(
            tripId = trip.id.toLong(),
            defaultLocation = trip.countryName,
            existingEvent = editingItineraryEvent,
            onDismiss = {
                isAddEventDialogOpen = false
                editingItineraryEvent = null
            },
            onSave = { event ->
                if (editingItineraryEvent != null) {
                    onUpdateItineraryEvent(event)
                } else {
                    onAddItineraryEvent(event)
                }
                isAddEventDialogOpen = false
                editingItineraryEvent = null
            }
        )
    }

    // Add Custom Packing Item Dialog
    if (isAddPackingDialogOpen) {
        AddPackingItemDialog(
            onDismiss = { isAddPackingDialogOpen = false },
            onSave = { name ->
                onAddPackingItem(name)
                isAddPackingDialogOpen = false
            }
        )
    }

    // Add Options Selection Dialog (Pops up when clicking '+' in Docs tab)
    if (isAddChoiceMenuOpen) {
        AddOptionsChoiceDialog(
            onDismiss = { isAddChoiceMenuOpen = false },
            onSelectPdf = {
                isAddChoiceMenuOpen = false
                pdfPickerLauncher.launch("application/pdf")
            },
            onSelectImage = {
                isAddChoiceMenuOpen = false
                imagePickerLauncher.launch("image/*")
            },
            onSelectNote = {
                isAddChoiceMenuOpen = false
                isAddNoteDialogOpen = true
            }
        )
    }

    // Add File Title Prompt Dialog (for PDF and Image)
    if (pendingFileAttachment != null) {
        val pending = pendingFileAttachment!!
        AddFileTitleDialog(
            initialTitle = pending.initialTitle,
            type = pending.type,
            onDismiss = { pendingFileAttachment = null },
            onSave = { customTitle ->
                onAddFileAttachment(pending.uri, pending.type, customTitle)
                pendingFileAttachment = null
            }
        )
    }

    // Add Note Modal Dialog
    if (isAddNoteDialogOpen) {
        AddNoteDialog(
            onDismiss = { isAddNoteDialogOpen = false },
            onSave = { title, text ->
                onAddNoteAttachment(title, text)
                isAddNoteDialogOpen = false
            }
        )
    }

    // Edit Note Modal Dialog
    if (editingNote != null) {
        val note = editingNote!!
        EditNoteDialog(
            initialTitle = note.title,
            initialContent = note.content ?: "",
            onDismiss = { editingNote = null },
            onSave = { newTitle, newContent ->
                onUpdateNoteAttachment(note.id, newTitle, newContent)
                editingNote = null
            }
        )
    }
}

@Composable
private fun WorkspaceTabSelectorRow(
    activeTab: WorkspaceTab,
    onTabSelected: (WorkspaceTab) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(9999.dp))
            .background(SurfaceContainerLow)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        WorkspaceTab.entries.forEach { tab ->
            val isSelected = tab == activeTab
            val (icon, label) = when (tab) {
                WorkspaceTab.EXPENSES -> Pair(Icons.AutoMirrored.Filled.ReceiptLong, "Expenses")
                WorkspaceTab.DOCS -> Pair(Icons.Default.FolderZip, "Docs")
                WorkspaceTab.ITINERARY -> Pair(Icons.Default.Map, "Itinerary")
                WorkspaceTab.PACKING -> Pair(Icons.Default.Luggage, "Packing")
            }

            val animatedBgColor by androidx.compose.animation.animateColorAsState(
                targetValue = if (isSelected) com.example.nomadcompass.ui.theme.PillActiveBackground else Color.Transparent,
                animationSpec = androidx.compose.animation.core.tween(200),
                label = "workspace_tab_bg"
            )
            val animatedContentColor by androidx.compose.animation.animateColorAsState(
                targetValue = if (isSelected) com.example.nomadcompass.ui.theme.PillActiveText else com.example.nomadcompass.ui.theme.PillInactiveText,
                animationSpec = androidx.compose.animation.core.tween(200),
                label = "workspace_tab_content"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(9999.dp))
                    .background(animatedBgColor)
                    .bounceClick(scaleDown = 0.94f) { onTabSelected(tab) }
                    .bounceOnState(state = isSelected, maxScale = 1.04f)
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = animatedContentColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        color = animatedContentColor,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun DocsTabContent(
    trip: Trip,
    attachments: List<TripAttachment>,
    currencyCode: String,
    onAddChoiceClick: () -> Unit,
    onMoveAttachment: (Int, Int) -> Unit,
    onEditNote: (TripAttachment) -> Unit,
    onDeleteAttachment: (Long) -> Unit,
    onOpenFile: (String, String) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Trip Meta Overview
            item(key = "overview_meta_card") {
                ClayCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 20.dp,
                    backgroundColor = SurfaceContainerLow
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${trip.startDate} to ${trip.endDate}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = OnSurface
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AttachMoney,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${formatCurrencyAmount(trip.budgetUsd, currencyCode)} / mo",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (trip.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Remote Work Arrangement:",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = trip.notes,
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurface
                            )
                        }
                    }
                }
            }

            // Section 2: Summary Stats Row
            item(key = "stats_row") {
                val pdfCount = attachments.count { it.type == AttachmentType.PDF }
                val imageCount = attachments.count { it.type == AttachmentType.IMAGE }
                val noteCount = attachments.count { it.type == AttachmentType.NOTE }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AttachmentStatBox(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.PictureAsPdf,
                        count = pdfCount,
                        label = "PDF Docs",
                        tint = Color(0xFFEF5350)
                    )
                    AttachmentStatBox(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Image,
                        count = imageCount,
                        label = "Images",
                        tint = Color(0xFF42A5F5)
                    )
                    AttachmentStatBox(
                        modifier = Modifier.weight(1f),
                        icon = Icons.AutoMirrored.Filled.Notes,
                        count = noteCount,
                        label = "Trip Notes",
                        tint = Color(0xFFFFCA28)
                    )
                }
            }

            // Section 3: Workspace Items List Header
            item(key = "list_header") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "WORKSPACE ITEMS (${attachments.size})",
                            style = MaterialTheme.typography.labelLarge,
                            color = Primary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        if (attachments.isNotEmpty()) {
                            Text(
                                text = "Use ▲ ▼ to reorder items",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Empty State if no attachments
            if (attachments.isEmpty()) {
                item(key = "empty_state") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.FolderZip,
                                contentDescription = null,
                                tint = OnSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(52.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Workspace is Empty",
                                style = MaterialTheme.typography.titleMedium,
                                color = OnSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap the '+' button at bottom right to add PDFs, save images, or write trip notes.",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                // Sequenced Items List
                itemsIndexed(
                    items = attachments,
                    key = { _, item -> item.id }
                ) { index, item ->
                    UnifiedAttachmentCard(
                        attachment = item,
                        index = index,
                        totalCount = attachments.size,
                        onMoveUp = { onMoveAttachment(index, index - 1) },
                        onMoveDown = { onMoveAttachment(index, index + 1) },
                        onEditNote = { onEditNote(item) },
                        onDelete = { onDeleteAttachment(item.id) },
                        onOpenFile = onOpenFile
                    )
                }
            }
        }

        // Floating Circular '+' Button at Bottom-Right for Docs
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 12.dp, end = 12.dp)
                .size(58.dp)
                .clip(CircleShape)
                .background(PrimaryContainer)
                .border(1.5.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                .clickable(onClick = onAddChoiceClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Item to Workspace",
                tint = OnPrimary,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
private fun ItineraryTabContent(
    trip: Trip,
    itineraryEvents: List<ItineraryEvent>,
    onOpenExtractor: () -> Unit,
    onAddEventClick: () -> Unit,
    onEditEvent: (ItineraryEvent) -> Unit,
    onDeleteEvent: (Long) -> Unit,
) {
    val context = LocalContext.current
    val isDark = LocalThemeController.current.isDarkMode

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Header Overview Card
            item(key = "itinerary_overview") {
                ClayCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 20.dp,
                    backgroundColor = SurfaceContainerLow
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "🗺️ ${trip.countryName} Schedule",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = OnSurface,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${trip.startDate} to ${trip.endDate}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(9999.dp))
                                    .background(Primary.copy(alpha = 0.15f))
                                    .border(1.dp, Primary.copy(alpha = 0.35f), RoundedCornerShape(9999.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${itineraryEvents.size} Events",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (trip.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = trip.notes,
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Action Buttons Row: Offline Extractor + Add Event
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Offline Extractor Button (Highlighted)
                            Box(
                                modifier = Modifier
                                    .weight(1.3f)
                                    .bounceClick(scaleDown = 0.96f, onClick = onOpenExtractor)
                                    .clip(RoundedCornerShape(9999.dp))
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                PrimaryContainer,
                                                Primary.copy(alpha = 0.85f)
                                            )
                                        )
                                    )
                                    .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(9999.dp))
                                    .padding(vertical = 10.dp, horizontal = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = OnPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Offline Extractor",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = OnPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Manual Add Button
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .bounceClick(scaleDown = 0.96f, onClick = onAddEventClick)
                                    .clip(RoundedCornerShape(9999.dp))
                                    .background(Color.White.copy(alpha = if (isDark) 0.12f else 0.22f))
                                    .border(1.dp, Color.White.copy(alpha = if (isDark) 0.25f else 0.40f), RoundedCornerShape(9999.dp))
                                    .padding(vertical = 10.dp, horizontal = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        tint = OnSurface,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Add Event",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = OnSurface,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Timeline Items
            if (itineraryEvents.isEmpty()) {
                item(key = "empty_itinerary") {
                    ClayCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 20.dp,
                        backgroundColor = SurfaceContainerLow
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(Primary.copy(alpha = 0.15f))
                                    .border(1.dp, Primary.copy(alpha = 0.35f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No Itinerary Events Yet",
                                style = MaterialTheme.typography.titleMedium,
                                color = OnSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Ingest unformatted text, PDFs, HTML, or screenshots to extract flights, hotels, and activities on-device.",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 12.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(18.dp))

                            GlassPillButton(
                                onClick = onOpenExtractor,
                                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Launch Offline Extractor", color = Primary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                itemsIndexed(
                    items = itineraryEvents,
                    key = { _, item -> item.id }
                ) { _, event ->
                    ItineraryTimelineCard(
                        event = event,
                        onEdit = { onEditEvent(event) },
                        onDelete = { onDeleteEvent(event.id) },
                        onCopyCode = { code ->
                            val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as? ClipboardManager
                            val clip = ClipData.newPlainText("Confirmation Code", code)
                            clipboard?.setPrimaryClip(clip)
                            Toast.makeText(context, "Copied code: $code", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ItineraryTimelineCard(
    event: ItineraryEvent,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCopyCode: (String) -> Unit,
) {
    val isDark = LocalThemeController.current.isDarkMode

    ClayCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 16.dp,
        backgroundColor = SurfaceContainerLow
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Category Badge + Title + Action Icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(event.category.defaultColor.copy(alpha = 0.20f))
                            .border(1.dp, event.category.defaultColor.copy(alpha = 0.40f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = event.category.icon,
                            contentDescription = event.category.displayName,
                            tint = event.category.defaultColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                            color = OnSurface,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${event.category.emoji} ${event.category.displayName}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = event.category.defaultColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = OnSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(2.dp))
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

            Spacer(modifier = Modifier.height(10.dp))

            // Dates & Times Row
            if (event.startDateTime.isNotBlank() || event.endDateTime.isNotBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (event.endDateTime.isNotBlank()) "${event.startDateTime} → ${event.endDateTime}" else event.startDateTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurface,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Location Row
            if (event.location.isNotBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFFFF5252),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = event.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Bottom Confirmation Code & Notes Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (event.confirmationCode.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(9999.dp))
                            .background(Color.White.copy(alpha = if (isDark) 0.12f else 0.25f))
                            .border(1.dp, Primary.copy(alpha = 0.35f), RoundedCornerShape(9999.dp))
                            .clickable { onCopyCode(event.confirmationCode) }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Code: ${event.confirmationCode}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Primary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy code",
                                tint = Primary,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                if (event.notes.isNotBlank()) {
                    Text(
                        text = event.notes,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = OnSurfaceVariant.copy(alpha = 0.75f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false).padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddEditItineraryEventDialog(
    tripId: Long,
    defaultLocation: String,
    existingEvent: ItineraryEvent?,
    onDismiss: () -> Unit,
    onSave: (ItineraryEvent) -> Unit,
) {
    var title by remember { mutableStateOf(existingEvent?.title ?: "") }
    var selectedCategory by remember { mutableStateOf(existingEvent?.category ?: ItineraryCategory.ACTIVITY) }
    var location by remember { mutableStateOf(existingEvent?.location ?: defaultLocation) }
    var startDateTime by remember { mutableStateOf(existingEvent?.startDateTime ?: "") }
    var endDateTime by remember { mutableStateOf(existingEvent?.endDateTime ?: "") }
    var confirmationCode by remember { mutableStateOf(existingEvent?.confirmationCode ?: "") }
    var notes by remember { mutableStateOf(existingEvent?.notes ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (existingEvent != null) "Edit Itinerary Event" else "Add Itinerary Event",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Event Title *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary)
                )

                // Category Selector
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(ItineraryCategory.entries) { cat ->
                        val isSelected = cat == selectedCategory
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isSelected) cat.defaultColor else Color.White.copy(alpha = 0.10f))
                                .border(1.dp, cat.defaultColor.copy(alpha = 0.40f), CircleShape)
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "${cat.emoji} ${cat.displayName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) OnPrimary else OnSurface,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location / Airport / Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = startDateTime,
                        onValueChange = { startDateTime = it },
                        label = { Text("Start Date/Time") },
                        placeholder = { Text("YYYY-MM-DD HH:mm") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary)
                    )
                    OutlinedTextField(
                        value = endDateTime,
                        onValueChange = { endDateTime = it },
                        label = { Text("End Date/Time") },
                        placeholder = { Text("YYYY-MM-DD HH:mm") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary)
                    )
                }

                OutlinedTextField(
                    value = confirmationCode,
                    onValueChange = { confirmationCode = it },
                    label = { Text("Confirmation Code / PNR") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary)
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / Details") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        val event = ItineraryEvent(
                            id = existingEvent?.id ?: 0,
                            tripId = tripId,
                            title = title.trim(),
                            category = selectedCategory,
                            location = location.trim(),
                            startDateTime = startDateTime.trim(),
                            endDateTime = endDateTime.trim(),
                            confirmationCode = confirmationCode.trim(),
                            notes = notes.trim()
                        )
                        onSave(event)
                    }
                }
            ) {
                Text("Save", color = Primary, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = OnSurfaceVariant)
            }
        }
    )
}

@Composable
private fun PackingTabContent(
    packingItems: List<PackingItem>,
    onTogglePackingItem: (PackingItem) -> Unit,
    onAddPackingItemClick: () -> Unit,
    onDeletePackingItem: (Long) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item(key = "packing_header") {
                Text(
                    text = "🧳 Nomad Packing Essentials",
                    style = MaterialTheme.typography.titleMedium,
                    color = OnSurface,
                    fontWeight = FontWeight.Bold
                )
            }

            if (packingItems.isEmpty()) {
                item(key = "empty_packing") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Luggage,
                                contentDescription = null,
                                tint = OnSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(52.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Packing List is Empty",
                                style = MaterialTheme.typography.titleMedium,
                                color = OnSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap the '+' button at bottom right to add custom items.",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                itemsIndexed(
                    items = packingItems,
                    key = { _, item -> item.id }
                ) { _, item ->
                    ClayCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onTogglePackingItem(item) },
                        cornerRadius = 14.dp,
                        backgroundColor = SurfaceContainerLow
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = if (item.isPacked) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = if (item.isPacked) "Packed" else "Not Packed",
                                    tint = if (item.isPacked) Primary else OnSurfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier
                                        .size(22.dp)
                                        .bounceOnState(state = item.isPacked, maxScale = 1.35f)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (item.isPacked) OnSurfaceVariant else OnSurface,
                                    fontWeight = if (item.isPacked) FontWeight.Normal else FontWeight.Medium
                                )
                            }

                            IconButton(
                                onClick = { onDeletePackingItem(item.id) },
                                modifier = Modifier
                                    .size(28.dp)
                                    .bounceClick(scaleDown = 0.82f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove Item",
                                    tint = OnSurfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Floating Circular '+' Button at Bottom-Right for Packing
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 12.dp, end = 12.dp)
                .size(58.dp)
                .clip(CircleShape)
                .background(PrimaryContainer)
                .border(1.5.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                .bounceClick(scaleDown = 0.92f, onClick = onAddPackingItemClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Custom Packing Item",
                tint = OnPrimary,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun AddPackingItemDialog(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var itemName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "🧳 Add Packing Item",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "Enter custom item to add to your packing list:",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    placeholder = { Text("e.g. Swimsuit, Drone, Passports", color = OnSurfaceVariant.copy(alpha = 0.6f)) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedContainerColor = SurfaceContainerLow,
                        unfocusedContainerColor = SurfaceContainerLow,
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface,
                        cursorColor = Primary,
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val trimmed = itemName.trim()
                    if (trimmed.isNotBlank()) {
                        onSave(trimmed)
                    }
                },
                enabled = itemName.isNotBlank()
            ) {
                Text("Save", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun UnifiedAttachmentCard(
    attachment: TripAttachment,
    index: Int,
    totalCount: Int,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onEditNote: () -> Unit,
    onDelete: () -> Unit,
    onOpenFile: (String, String) -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }

    ClayCard(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (attachment.type == AttachmentType.NOTE) {
                    Modifier.clickable { isExpanded = !isExpanded }
                } else {
                    Modifier
                }
            ),
        cornerRadius = 16.dp,
        backgroundColor = SurfaceContainerLow
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Reordering Handle & Move Controls
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = onMoveUp,
                        enabled = index > 0,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "Move Up",
                            tint = if (index > 0) Primary else OnSurfaceVariant.copy(alpha = 0.2f)
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.DragHandle,
                        contentDescription = "Reorder handle",
                        tint = OnSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )

                    IconButton(
                        onClick = onMoveDown,
                        enabled = index < totalCount - 1,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Move Down",
                            tint = if (index < totalCount - 1) Primary else OnSurfaceVariant.copy(alpha = 0.2f)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Icon / Thumbnail representation
                when (attachment.type) {
                    AttachmentType.PDF -> {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFEF5350).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                tint = Color(0xFFEF5350),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    AttachmentType.IMAGE -> {
                        if (attachment.filePath != null) {
                            AsyncImage(
                                model = File(attachment.filePath),
                                contentDescription = attachment.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF42A5F5).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = null,
                                    tint = Color(0xFF42A5F5),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                    AttachmentType.NOTE -> {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFFCA28).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Notes,
                                contentDescription = null,
                                tint = Color(0xFFFFB300),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Main Details
                Column(modifier = Modifier.weight(1f)) {
                    // Type Pill Badge & Title
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val (badgeText, badgeBg, badgeTextColor) = when (attachment.type) {
                            AttachmentType.PDF -> Triple("PDF", Color(0xFFEF5350).copy(alpha = 0.15f), Color(0xFFEF5350))
                            AttachmentType.IMAGE -> Triple("IMAGE", Color(0xFF42A5F5).copy(alpha = 0.15f), Color(0xFF1E88E5))
                            AttachmentType.NOTE -> Triple("NOTE", Color(0xFFFFCA28).copy(alpha = 0.2f), Color(0xFFF57F17))
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(badgeBg)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badgeText,
                                style = MaterialTheme.typography.labelSmall,
                                color = badgeTextColor,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = attachment.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = OnSurface,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    if (attachment.type == AttachmentType.NOTE) {
                        Text(
                            text = if (isExpanded) "Tap to collapse note" else "Tap to view full note",
                            style = MaterialTheme.typography.labelSmall,
                            color = Primary.copy(alpha = 0.8f)
                        )
                    } else {
                        Text(
                            text = FileStorageHelper.formatFileSize(attachment.fileSize),
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Action Buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (attachment.type == AttachmentType.NOTE) {
                        IconButton(
                            onClick = { isExpanded = !isExpanded },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (isExpanded) "Collapse Note" else "Expand Note",
                                tint = OnSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        IconButton(
                            onClick = onEditNote,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Note",
                                tint = Primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else if (attachment.filePath != null) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(PrimaryContainer)
                                .clickable { onOpenFile(attachment.filePath, attachment.mimeType) }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                    contentDescription = null,
                                    tint = com.example.nomadcompass.ui.theme.OnPrimaryContainer,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "VIEW",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = com.example.nomadcompass.ui.theme.OnPrimaryContainer,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = OnSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Expanded Full Note Text Area
            if (attachment.type == AttachmentType.NOTE) {
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceContainer)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "NOTE CONTENT",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        if (!attachment.content.isNullOrBlank()) {
                            Text(
                                text = attachment.content,
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurface
                            )
                        } else {
                            Text(
                                text = "Empty Note",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AttachmentStatBox(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: Int,
    label: String,
    tint: Color,
) {
    ClayCard(
        modifier = modifier,
        cornerRadius = 14.dp,
        backgroundColor = SurfaceContainerLow
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = OnSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun AddOptionsChoiceDialog(
    onDismiss: () -> Unit,
    onSelectPdf: () -> Unit,
    onSelectImage: () -> Unit,
    onSelectNote: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add to Workspace",
                style = MaterialTheme.typography.titleLarge,
                color = OnSurface,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Select what type of content you want to upload or create:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))

                ChoiceRowItem(
                    icon = Icons.Default.PictureAsPdf,
                    title = "Attach PDF Document",
                    subtitle = "Boarding passes, hotel vouchers, visas",
                    tint = Color(0xFFEF5350),
                    onClick = onSelectPdf
                )
                ChoiceRowItem(
                    icon = Icons.Default.Image,
                    title = "Save Photo / Image",
                    subtitle = "Maps, receipts, location photos",
                    tint = Color(0xFF42A5F5),
                    onClick = onSelectImage
                )
                ChoiceRowItem(
                    icon = Icons.AutoMirrored.Filled.NoteAdd,
                    title = "Write Trip Note",
                    subtitle = "Co-working passwords, places to visit, plans",
                    tint = Color(0xFFFFCA28),
                    onClick = onSelectNote
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = OnSurfaceVariant)
            }
        },
        containerColor = SurfaceContainer
    )
}

@Composable
private fun ChoiceRowItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    tint: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceContainerLow)
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(tint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = OnSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun AddFileTitleDialog(
    initialTitle: String,
    type: AttachmentType,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
) {
    var title by remember { mutableStateOf(initialTitle) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (type == AttachmentType.PDF) "Save PDF Attachment" else "Save Image Attachment",
                style = MaterialTheme.typography.titleLarge,
                color = OnSurface,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Enter a title for this file in your workspace:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Attachment Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedContainerColor = SurfaceContainerLow,
                        unfocusedContainerColor = SurfaceContainerLow,
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface,
                        focusedLabelColor = Primary,
                        unfocusedLabelColor = OnSurfaceVariant,
                        cursorColor = Primary,
                    )
                )
            }
        },
        confirmButton = {
            ClayButton(onClick = { onSave(title) }) {
                Text("Save to Workspace", color = com.example.nomadcompass.ui.theme.OnPrimaryContainer, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = OnSurfaceVariant)
            }
        },
        containerColor = SurfaceContainer
    )
}

@Composable
private fun AddNoteDialog(
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "New Trip Note",
                style = MaterialTheme.typography.titleLarge,
                color = OnSurface,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Note Title (e.g., Wi-Fi Details, Hotel Booking)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedContainerColor = SurfaceContainerLow,
                        unfocusedContainerColor = SurfaceContainerLow,
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface,
                        focusedLabelColor = Primary,
                        unfocusedLabelColor = OnSurfaceVariant,
                        cursorColor = Primary,
                    )
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Note Content") },
                    minLines = 4,
                    maxLines = 8,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedContainerColor = SurfaceContainerLow,
                        unfocusedContainerColor = SurfaceContainerLow,
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface,
                        focusedLabelColor = Primary,
                        unfocusedLabelColor = OnSurfaceVariant,
                        cursorColor = Primary,
                    )
                )
            }
        },
        confirmButton = {
            ClayButton(onClick = { onSave(title, content) }) {
                Text("Save Note", color = com.example.nomadcompass.ui.theme.OnPrimaryContainer, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = OnSurfaceVariant)
            }
        },
        containerColor = SurfaceContainer
    )
}

@Composable
private fun EditNoteDialog(
    initialTitle: String,
    initialContent: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit,
) {
    var title by remember { mutableStateOf(initialTitle) }
    var content by remember { mutableStateOf(initialContent) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit Trip Note",
                style = MaterialTheme.typography.titleLarge,
                color = OnSurface,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Note Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedContainerColor = SurfaceContainerLow,
                        unfocusedContainerColor = SurfaceContainerLow,
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface,
                        focusedLabelColor = Primary,
                        unfocusedLabelColor = OnSurfaceVariant,
                        cursorColor = Primary,
                    )
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Note Content") },
                    minLines = 4,
                    maxLines = 8,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedContainerColor = SurfaceContainerLow,
                        unfocusedContainerColor = SurfaceContainerLow,
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface,
                        focusedLabelColor = Primary,
                        unfocusedLabelColor = OnSurfaceVariant,
                        cursorColor = Primary,
                    )
                )
            }
        },
        confirmButton = {
            ClayButton(onClick = { onSave(title, content) }) {
                Text("Update Note", color = com.example.nomadcompass.ui.theme.OnPrimaryContainer, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = OnSurfaceVariant)
            }
        },
        containerColor = SurfaceContainer
    )
}

private fun formatCurrencyAmount(amountUsd: Double, currencyCode: String): String {
    val symbol = when (currencyCode.uppercase()) {
        "USD" -> "$"
        "EUR" -> "€"
        "GBP" -> "£"
        "JPY" -> "¥"
        "INR" -> "₹"
        else -> "$currencyCode "
    }
    return "$symbol${String.format(Locale.US, "%,.0f", amountUsd)}"
}
