package com.example.nomadcompass.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.ui.draw.blur
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import com.example.nomadcompass.ui.theme.icons.PhosphorIcons
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
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
import com.example.nomadcompass.util.FileStorageHelper
import com.example.nomadcompass.util.PdfThumbnailHelper
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
    bgPhotoUri: String? = LocalAppBackground.current.bgPhotoUri,
    bgBlurRadius: Float = LocalAppBackground.current.bgBlurRadius,
    onSelectTab: (WorkspaceTab) -> Unit = {},
    onAddExpense: (
        title: String,
        amountLocal: Double,
        currencyCode: String,
        category: ExpenseCategory,
        notes: String,
        date: Long,
        paymentMethod: String,
        receiptPath: String?
    ) -> Unit = { _, _, _, _, _, _, _, _ -> },
    onEditExpense: (
        id: Long,
        title: String,
        amountLocal: Double,
        currencyCode: String,
        category: ExpenseCategory,
        notes: String,
        date: Long,
        paymentMethod: String,
        receiptPath: String?
    ) -> Unit = { _, _, _, _, _, _, _, _, _ -> },
    onDeleteExpense: (Long) -> Unit = {},
    onCalculateLivePreview: (amountLocal: Double, currencyCode: String, callback: (Double, Boolean) -> Unit) -> Unit = { _, _, _ -> },
    onTogglePackingItem: (PackingItem) -> Unit = {},
    onAddPackingItem: (String) -> Unit = {},
    onDeletePackingItem: (Long) -> Unit = {},
    onSaveItineraryEvents: (List<ItineraryEvent>) -> Unit = {},
    onAddItineraryEvent: (ItineraryEvent) -> Unit = {},
    onUpdateItineraryEvent: (ItineraryEvent) -> Unit = {},
    onDeleteItineraryEvent: (Long) -> Unit = {},
    onAddFileAttachment: (Uri, AttachmentType, String, String) -> Unit,
    onAddNoteAttachment: (title: String, text: String, String) -> Unit,
    onUpdateNoteAttachment: (id: Long, title: String, text: String) -> Unit,
    onMoveAttachment: (fromIndex: Int, toIndex: Int) -> Unit,
    onDeleteAttachment: (Long) -> Unit,
    onDeleteTrip: () -> Unit = {},
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    var isAddChoiceMenuOpen by remember { mutableStateOf(false) }
    var isAddNoteDialogOpen by remember { mutableStateOf(false) }
    var isAddPackingDialogOpen by remember { mutableStateOf(false) }
    var isExtractorModalOpen by remember { mutableStateOf(false) }
    var isAddEventDialogOpen by remember { mutableStateOf(false) }
    var isDeleteTripConfirmOpen by remember { mutableStateOf(false) }
    var editingItineraryEvent by remember { mutableStateOf<ItineraryEvent?>(null) }
    var editingNote by remember { mutableStateOf<TripAttachment?>(null) }
    var pendingFileAttachment by remember { mutableStateOf<PendingFileAttachment?>(null) }
    var previewingAttachment by remember { mutableStateOf<TripAttachment?>(null) }

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

    val isSubModalOpen = isAddChoiceMenuOpen ||
        isAddNoteDialogOpen ||
        editingNote != null ||
        isAddPackingDialogOpen ||
        isExtractorModalOpen ||
        isAddEventDialogOpen ||
        editingItineraryEvent != null ||
        isDeleteTripConfirmOpen ||
        previewingAttachment != null ||
        pendingFileAttachment != null

    val workspaceBlur by animateDpAsState(
        targetValue = if (isSubModalOpen) 18.dp else 0.dp,
        label = "workspace_submodal_blur"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        DialogBlurBehind(blurRadius = 32)
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
                        .fillMaxHeight(0.94f)
                        .blur(workspaceBlur),
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                if (trip.flagUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = trip.flagUrl,
                                        contentDescription = trip.countryName,
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
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
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "Trip Workspace & Planning Hub",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = OnSurfaceVariant
                                    )
                                }
                            }

                            // Header Actions: Delete Trip & Close Workspace
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                IconButton(onClick = { isDeleteTripConfirmOpen = true }) {
                                    Icon(
                                        imageVector = PhosphorIcons.Trash,
                                        contentDescription = "Delete Trip",
                                        tint = Color(0xFFEF5350)
                                    )
                                }
                                IconButton(onClick = onDismiss) {
                                    Icon(
                                        imageVector = PhosphorIcons.X,
                                        contentDescription = "Close workspace",
                                        tint = OnSurfaceVariant
                                    )
                                }
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
                                    onEditExpense = onEditExpense,
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
                                    onOpenFile = { path, mime -> FileStorageHelper.openFile(context, path, mime) },
                                    onPreviewAttachment = { previewingAttachment = it }
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
}

    // Delete Trip Confirmation Dialog
    if (isDeleteTripConfirmOpen) {
        FrostedGlassAlertDialog(
            onDismissRequest = { isDeleteTripConfirmOpen = false },
            title = {
                Text(
                    text = "Delete Trip to ${trip.countryName}?",
                    style = MaterialTheme.typography.titleLarge,
                    color = OnSurface,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete this trip? All workspace documents, expenses, itinerary, and packing lists will be permanently deleted.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
            },
            confirmButton = {
                ClayButton(
                    onClick = {
                        isDeleteTripConfirmOpen = false
                        onDeleteTrip()
                        onDismiss()
                    }
                ) {
                    Text("Delete Trip", color = Color(0xFFEF5350), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { isDeleteTripConfirmOpen = false }) {
                    Text("Cancel", color = OnSurfaceVariant)
                }
            },
            containerColor = SurfaceContainer
        )
    }

    // In-App Attachment Preview Modal (Image & PDF page renders & Note viewer)
    if (previewingAttachment != null) {
        InAppAttachmentPreviewDialog(
            attachment = previewingAttachment!!,
            onDismiss = { previewingAttachment = null },
            onOpenFileExternally = { path, mime -> FileStorageHelper.openFile(context, path, mime) },
            onEditNote = {
                val item = previewingAttachment!!
                previewingAttachment = null
                editingNote = item
            }
        )
    }

    // Offline Itinerary Extractor Modal
    if (isExtractorModalOpen) {
        ItineraryExtractorModal(
            tripId = trip.id.toLong(),
            destinationName = trip.countryName,
            bgPhotoUri = bgPhotoUri,
            bgBlurRadius = bgBlurRadius,
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

    // Add File Title Prompt Dialog (for PDF and Image with Category Selection)
    if (pendingFileAttachment != null) {
        val pending = pendingFileAttachment!!
        AddFileTitleDialog(
            initialTitle = pending.initialTitle,
            type = pending.type,
            onDismiss = { pendingFileAttachment = null },
            onSave = { customTitle, category ->
                onAddFileAttachment(pending.uri, pending.type, customTitle, category)
                pendingFileAttachment = null
            }
        )
    }

    // Add Note Modal Dialog (with Category Selection)
    if (isAddNoteDialogOpen) {
        AddNoteDialog(
            onDismiss = { isAddNoteDialogOpen = false },
            onSave = { title, text, category ->
                onAddNoteAttachment(title, text, category)
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
    ClaySlidingTabRow(
        tabs = WorkspaceTab.entries,
        selectedTab = activeTab,
        onTabSelected = onTabSelected,
        height = 42.dp,
        cornerRadius = 21.dp,
        fontSize = 12.5.sp,
        labelProvider = { tab ->
            when (tab) {
                WorkspaceTab.EXPENSES -> "Expenses"
                WorkspaceTab.DOCS -> "Docs"
                WorkspaceTab.ITINERARY -> "Itinerary"
                WorkspaceTab.PACKING -> "Packing"
            }
        }
    )
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
    onPreviewAttachment: (TripAttachment) -> Unit,
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Visas", "Tickets", "Lodging", "IDs", "Notes")

    val filteredAttachments = remember(attachments, selectedCategory, searchQuery) {
        attachments.filter { item ->
            val matchesCategory = when (selectedCategory) {
                "All" -> true
                "Notes" -> item.type == AttachmentType.NOTE || item.category.equals("Notes", true)
                else -> item.category.equals(selectedCategory, true)
            }
            val matchesSearch = if (searchQuery.isBlank()) true else {
                item.title.contains(searchQuery, true) ||
                    (item.content?.contains(searchQuery, true) == true) ||
                    item.category.contains(searchQuery, true)
            }
            matchesCategory && matchesSearch
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds(),
            contentPadding = PaddingValues(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Section 1: Trip Meta Overview with clean modern visual hierarchy
            item(key = "overview_meta_card") {
                ClayCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 20.dp,
                    backgroundColor = SurfaceContainerLow
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Date range combined with calendar icon
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceContainerHigh.copy(alpha = 0.6f))
                                    .border(
                                        1.dp,
                                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = PhosphorIcons.CalendarBlank,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${trip.startDate} – ${trip.endDate}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = OnSurface,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            // Monthly Budget with clear label and amount
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Monthly Budget",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OnSurfaceVariant,
                                    fontSize = 11.sp,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "${formatCurrencyAmount(trip.budgetUsd, currencyCode)} / mo",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (trip.notes.isNotBlank()) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                thickness = 0.8.dp
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = "WORKSPACE ARRANGEMENT / NOTES",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OnSurfaceVariant,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    letterSpacing = 0.8.sp
                                )
                                Text(
                                    text = trip.notes,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = OnSurface.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }
                }
            }

            // Section 2: Instant Search Bar
            item(key = "search_bar") {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search docs, visas, tickets, notes...") },
                    leadingIcon = {
                        Icon(
                            imageVector = PhosphorIcons.MagnifyingGlass,
                            contentDescription = "Search",
                            tint = Primary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = PhosphorIcons.XCircle,
                                    contentDescription = "Clear search",
                                    tint = OnSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                        focusedContainerColor = SurfaceContainerLow,
                        unfocusedContainerColor = SurfaceContainerLow,
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface,
                        cursorColor = Primary,
                    )
                )
            }

            // Section 3: Category Filter Pills Row with direct item counts
            item(key = "category_filter_pills") {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        val count = if (cat == "All") attachments.size else {
                            if (cat == "Notes") attachments.count { it.type == AttachmentType.NOTE || it.category.equals("Notes", true) }
                            else attachments.count { it.category.equals(cat, true) }
                        }
                        val isSelected = cat == selectedCategory
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) Primary else SurfaceContainerLow)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) Primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "$cat ($count)",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) OnPrimary else OnSurface,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Section 5: List Header
            item(key = "list_header") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DOCUMENTS & NOTES (${filteredAttachments.size})",
                        style = MaterialTheme.typography.labelLarge,
                        color = Primary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    if (filteredAttachments.isNotEmpty()) {
                        Text(
                            text = "Tap to preview in-app",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant
                        )
                    }
                }
            }

            // Empty State
            if (filteredAttachments.isEmpty()) {
                item(key = "empty_state") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = PhosphorIcons.FolderSimpleDashed,
                                contentDescription = null,
                                tint = OnSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(52.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (searchQuery.isNotBlank() || selectedCategory != "All") "No matching documents" else "Workspace is Empty",
                                style = MaterialTheme.typography.titleMedium,
                                color = OnSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (searchQuery.isNotBlank()) "Try a different search query or category filter." else "Tap '+' below to add visas, tickets, lodging vouchers, or notes.",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                itemsIndexed(
                    items = filteredAttachments,
                    key = { _, item -> item.id }
                ) { index, item ->
                    UnifiedAttachmentCard(
                        attachment = item,
                        index = index,
                        totalCount = filteredAttachments.size,
                        onMoveUp = { onMoveAttachment(index, index - 1) },
                        onMoveDown = { onMoveAttachment(index, index + 1) },
                        onEditNote = { onEditNote(item) },
                        onDelete = { onDeleteAttachment(item.id) },
                        onOpenFile = onOpenFile,
                        onPreview = { onPreviewAttachment(item) }
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
                imageVector = PhosphorIcons.Plus,
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
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
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
                                        imageVector = PhosphorIcons.Sparkle,
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
                                        imageVector = PhosphorIcons.Plus,
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
                                    imageVector = PhosphorIcons.Sparkle,
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
                                    imageVector = PhosphorIcons.Sparkle,
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
                            imageVector = PhosphorIcons.PencilSimple,
                            contentDescription = "Edit",
                            tint = OnSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(2.dp))
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = PhosphorIcons.Trash,
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
                        imageVector = PhosphorIcons.CalendarBlank,
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
                        imageVector = PhosphorIcons.MapPin,
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
                                imageVector = PhosphorIcons.CopySimple,
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

    FrostedGlassAlertDialog(
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
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                        focusedContainerColor = SurfaceContainerLow,
                        unfocusedContainerColor = SurfaceContainerLow,
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface,
                        cursorColor = Primary
                    )
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
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                        focusedContainerColor = SurfaceContainerLow,
                        unfocusedContainerColor = SurfaceContainerLow,
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface,
                        cursorColor = Primary
                    )
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
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                            focusedContainerColor = SurfaceContainerLow,
                            unfocusedContainerColor = SurfaceContainerLow,
                            focusedTextColor = OnSurface,
                            unfocusedTextColor = OnSurface,
                            cursorColor = Primary
                        )
                    )
                    OutlinedTextField(
                        value = endDateTime,
                        onValueChange = { endDateTime = it },
                        label = { Text("End Date/Time") },
                        placeholder = { Text("YYYY-MM-DD HH:mm") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                            focusedContainerColor = SurfaceContainerLow,
                            unfocusedContainerColor = SurfaceContainerLow,
                            focusedTextColor = OnSurface,
                            unfocusedTextColor = OnSurface,
                            cursorColor = Primary
                        )
                    )
                }

                OutlinedTextField(
                    value = confirmationCode,
                    onValueChange = { confirmationCode = it },
                    label = { Text("Confirmation Code / PNR") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                        focusedContainerColor = SurfaceContainerLow,
                        unfocusedContainerColor = SurfaceContainerLow,
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface,
                        cursorColor = Primary
                    )
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / Details") },
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                        focusedContainerColor = SurfaceContainerLow,
                        unfocusedContainerColor = SurfaceContainerLow,
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface,
                        cursorColor = Primary
                    )
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
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
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
                                imageVector = PhosphorIcons.SuitcaseRolling,
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
                                    imageVector = if (item.isPacked) PhosphorIcons.CheckCircle else PhosphorIcons.Circle,
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
                                    imageVector = PhosphorIcons.X,
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
                imageVector = PhosphorIcons.Plus,
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

    FrostedGlassAlertDialog(
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
    onPreview: () -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }
    var pdfThumbnail by remember(attachment.filePath) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(attachment.filePath) {
        if (attachment.type == AttachmentType.PDF && attachment.filePath != null) {
            pdfThumbnail = PdfThumbnailHelper.getPdfThumbnail(attachment.filePath, 140, 140)
        }
    }

    ClayCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (attachment.type == AttachmentType.NOTE) {
                    isExpanded = !isExpanded
                } else {
                    onPreview()
                }
            },
        cornerRadius = 18.dp,
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
                        modifier = Modifier.size(22.dp)
                    ) {
                        Icon(
                            imageVector = PhosphorIcons.CaretUp,
                            contentDescription = "Move Up",
                            tint = if (index > 0) Primary else OnSurfaceVariant.copy(alpha = 0.2f)
                        )
                    }

                    Icon(
                        imageVector = PhosphorIcons.DotsSixVertical,
                        contentDescription = "Reorder handle",
                        tint = OnSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(14.dp)
                    )

                    IconButton(
                        onClick = onMoveDown,
                        enabled = index < totalCount - 1,
                        modifier = Modifier.size(22.dp)
                    ) {
                        Icon(
                            imageVector = PhosphorIcons.CaretDown,
                            contentDescription = "Move Down",
                            tint = if (index < totalCount - 1) Primary else OnSurfaceVariant.copy(alpha = 0.2f)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // In-App Thumbnail representation (PDF native render, Coil Image, or Note Icon)
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            when (attachment.type) {
                                AttachmentType.PDF -> Color(0xFFEF5350).copy(alpha = 0.15f)
                                AttachmentType.IMAGE -> Color(0xFF42A5F5).copy(alpha = 0.15f)
                                AttachmentType.NOTE -> Color(0xFFFFCA28).copy(alpha = 0.2f)
                            }
                        )
                        .clickable(onClick = onPreview),
                    contentAlignment = Alignment.Center
                ) {
                    when (attachment.type) {
                        AttachmentType.PDF -> {
                            if (pdfThumbnail != null) {
                                Image(
                                    bitmap = pdfThumbnail!!.asImageBitmap(),
                                    contentDescription = "PDF Thumbnail",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = PhosphorIcons.FilePdf,
                                    contentDescription = null,
                                    tint = Color(0xFFEF5350),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        AttachmentType.IMAGE -> {
                            if (attachment.filePath != null) {
                                AsyncImage(
                                    model = File(attachment.filePath),
                                    contentDescription = attachment.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(
                                    imageVector = PhosphorIcons.FileImage,
                                    contentDescription = null,
                                    tint = Color(0xFF42A5F5),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        AttachmentType.NOTE -> {
                            Icon(
                                imageVector = PhosphorIcons.FileText,
                                contentDescription = null,
                                tint = Color(0xFFFFB300),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Main Details & Category Badge
                Column(modifier = Modifier.weight(1f)) {
                    // Type & Category Badges Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val (typeBadgeText, typeBadgeBg, typeBadgeColor) = when (attachment.type) {
                            AttachmentType.PDF -> Triple("PDF", Color(0xFFEF5350).copy(alpha = 0.15f), Color(0xFFEF5350))
                            AttachmentType.IMAGE -> Triple("IMAGE", Color(0xFF42A5F5).copy(alpha = 0.15f), Color(0xFF1E88E5))
                            AttachmentType.NOTE -> Triple("NOTE", Color(0xFFFFCA28).copy(alpha = 0.2f), Color(0xFFF57F17))
                        }

                        // Attachment Type Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(typeBadgeBg)
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = typeBadgeText,
                                style = MaterialTheme.typography.labelSmall,
                                color = typeBadgeColor,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Category Badge
                        if (attachment.category.isNotBlank() && !attachment.category.equals("General", true)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Primary.copy(alpha = 0.12f))
                                    .padding(horizontal = 6.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = attachment.category.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Primary,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = attachment.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = OnSurface,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    if (attachment.type == AttachmentType.NOTE) {
                        Text(
                            text = if (isExpanded) "Tap to collapse note" else "Tap to preview or expand note",
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
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = if (isExpanded) PhosphorIcons.CaretUp else PhosphorIcons.CaretDown,
                                contentDescription = if (isExpanded) "Collapse Note" else "Expand Note",
                                tint = OnSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        IconButton(
                            onClick = onEditNote,
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = PhosphorIcons.PencilSimple,
                                contentDescription = "Edit Note",
                                tint = Primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else {
                        // Quick In-App Preview / Open Pill
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(PrimaryContainer)
                                .clickable(onClick = onPreview)
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = PhosphorIcons.Eye,
                                    contentDescription = null,
                                    tint = com.example.nomadcompass.ui.theme.OnPrimaryContainer,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "PREVIEW",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = com.example.nomadcompass.ui.theme.OnPrimaryContainer,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = PhosphorIcons.Trash,
                            contentDescription = "Delete",
                            tint = OnSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
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

/**
 * Full In-App Attachment Preview Dialog (PDF Full Page Bitmaps, Full Image Viewer, and Note Reader)
 */
@Composable
private fun InAppAttachmentPreviewDialog(
    attachment: TripAttachment,
    onDismiss: () -> Unit,
    onOpenFileExternally: (String, String) -> Unit,
    onEditNote: () -> Unit,
) {
    val context = LocalContext.current
    var pdfPages by remember(attachment.filePath) { mutableStateOf<List<Bitmap>>(emptyList()) }
    var isLoadingPages by remember { mutableStateOf(attachment.type == AttachmentType.PDF) }

    LaunchedEffect(attachment.filePath) {
        if (attachment.type == AttachmentType.PDF && attachment.filePath != null) {
            isLoadingPages = true
            pdfPages = PdfThumbnailHelper.getPdfPageBitmaps(attachment.filePath, 900)
            isLoadingPages = false
        }
    }

    FrostedGlassDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth(0.94f)
            .fillMaxHeight(0.92f)
            .statusBarsPadding()
            .padding(vertical = 12.dp),
        cornerRadius = 24.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp)
        ) {
                    // Header Bar with Details & Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Primary.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = attachment.category.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Primary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
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

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (attachment.filePath != null) {
                                IconButton(
                                    onClick = { onOpenFileExternally(attachment.filePath, attachment.mimeType) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = PhosphorIcons.ArrowSquareOut,
                                        contentDescription = "Open Externally",
                                        tint = Primary
                                    )
                                }
                            } else if (attachment.type == AttachmentType.NOTE) {
                                IconButton(
                                    onClick = onEditNote,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = PhosphorIcons.PencilSimple,
                                        contentDescription = "Edit Note",
                                        tint = Primary
                                    )
                                }
                            }

                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = PhosphorIcons.X,
                                    contentDescription = "Close preview",
                                    tint = OnSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Content Viewer
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceContainerLow),
                        contentAlignment = Alignment.Center
                    ) {
                        when (attachment.type) {
                            AttachmentType.PDF -> {
                                if (isLoadingPages) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        CircularProgressIndicator(color = Primary, modifier = Modifier.size(36.dp))
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "Rendering PDF pages...",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = OnSurfaceVariant
                                        )
                                    }
                                } else if (pdfPages.isEmpty()) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = PhosphorIcons.FilePdf,
                                            contentDescription = null,
                                            tint = Color(0xFFEF5350),
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "PDF page preview not available",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = OnSurface
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        TextButton(onClick = {
                                            if (attachment.filePath != null) {
                                                onOpenFileExternally(attachment.filePath, attachment.mimeType)
                                            }
                                        }) {
                                            Text("Open with PDF Reader", color = Primary)
                                        }
                                    }
                                } else {
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(14.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        itemsIndexed(pdfPages) { pageIdx, pageBitmap ->
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = "Page ${pageIdx + 1} of ${pdfPages.size}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = OnSurfaceVariant,
                                                    modifier = Modifier.padding(bottom = 4.dp)
                                                )
                                                Image(
                                                    bitmap = pageBitmap.asImageBitmap(),
                                                    contentDescription = "PDF Page ${pageIdx + 1}",
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp)),
                                                    contentScale = ContentScale.FillWidth
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            AttachmentType.IMAGE -> {
                                if (attachment.filePath != null) {
                                    AsyncImage(
                                        model = File(attachment.filePath),
                                        contentDescription = attachment.title,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(10.dp)
                                            .clip(RoundedCornerShape(12.dp)),
                                        contentScale = ContentScale.Fit
                                    )
                                } else {
                                    Text(
                                        text = "Image preview unavailable",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = OnSurfaceVariant
                                    )
                                }
                            }

                            AttachmentType.NOTE -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "NOTE TEXT",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                        TextButton(onClick = {
                                            val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            clipboard.setPrimaryClip(ClipData.newPlainText("Trip Note", attachment.content ?: ""))
                                            Toast.makeText(context, "Note copied to clipboard", Toast.LENGTH_SHORT).show()
                                        }) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = PhosphorIcons.CopySimple,
                                                    contentDescription = null,
                                                    tint = Primary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Copy", color = Primary, fontSize = 12.sp)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                                        item {
                                            Text(
                                                text = attachment.content ?: "No content",
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = OnSurface,
                                                lineHeight = 24.sp
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
private fun AddOptionsChoiceDialog(
    onDismiss: () -> Unit,
    onSelectPdf: () -> Unit,
    onSelectImage: () -> Unit,
    onSelectNote: () -> Unit,
) {
    FrostedGlassAlertDialog(
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
                    icon = PhosphorIcons.FilePdf,
                    title = "Attach PDF Document",
                    subtitle = "Boarding passes, hotel vouchers, visas",
                    tint = Color(0xFFEF5350),
                    onClick = onSelectPdf
                )
                ChoiceRowItem(
                    icon = PhosphorIcons.FileImage,
                    title = "Save Photo / Image",
                    subtitle = "Maps, receipts, location photos",
                    tint = Color(0xFF42A5F5),
                    onClick = onSelectImage
                )
                ChoiceRowItem(
                    icon = PhosphorIcons.FileText,
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
    onSave: (String, String) -> Unit,
) {
    var title by remember { mutableStateOf(initialTitle) }
    var selectedCategory by remember { mutableStateOf("Visas") }
    val categories = listOf("Visas", "Tickets", "Lodging", "IDs", "General")

    FrostedGlassAlertDialog(
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
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Enter title and select a category:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Attachment Title") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                        focusedContainerColor = SurfaceContainerLow,
                        unfocusedContainerColor = SurfaceContainerLow,
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface,
                        focusedLabelColor = Primary,
                        unfocusedLabelColor = OnSurfaceVariant,
                        cursorColor = Primary,
                    )
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "DOCUMENT CATEGORY",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(categories) { cat ->
                            val isSelected = cat == selectedCategory
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) Primary else SurfaceContainerLow)
                                    .border(1.dp, if (isSelected) Primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                    .clickable { selectedCategory = cat }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = cat,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) OnPrimary else OnSurface,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            ClayButton(onClick = { onSave(title, selectedCategory) }) {
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
    onSave: (String, String, String) -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Notes") }
    val categories = listOf("Notes", "Visas", "Tickets", "Lodging", "IDs", "General")

    FrostedGlassAlertDialog(
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
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                        focusedContainerColor = SurfaceContainerLow,
                        unfocusedContainerColor = SurfaceContainerLow,
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface,
                        focusedLabelColor = Primary,
                        unfocusedLabelColor = OnSurfaceVariant,
                        cursorColor = Primary,
                    )
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "CATEGORY",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(categories) { cat ->
                            val isSelected = cat == selectedCategory
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) Primary else SurfaceContainerLow)
                                    .border(1.dp, if (isSelected) Primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                    .clickable { selectedCategory = cat }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = cat,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) OnPrimary else OnSurface,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Note Content") },
                    minLines = 4,
                    maxLines = 8,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
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
            ClayButton(onClick = { onSave(title, content, selectedCategory) }) {
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

    FrostedGlassAlertDialog(
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
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
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
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
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
    val absFormatted = String.format(Locale.US, "%,.0f", kotlin.math.abs(amountUsd))
    return if (amountUsd < 0) "-$symbol$absFormatted" else "$symbol$absFormatted"
}
