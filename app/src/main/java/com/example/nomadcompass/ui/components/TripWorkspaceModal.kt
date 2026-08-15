package com.example.nomadcompass.ui.components

import android.net.Uri
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PictureAsPdf
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import com.example.nomadcompass.domain.model.AttachmentType
import com.example.nomadcompass.domain.model.Trip
import com.example.nomadcompass.domain.model.TripAttachment
import com.example.nomadcompass.ui.theme.Background
import com.example.nomadcompass.ui.theme.OnPrimary
import com.example.nomadcompass.ui.theme.OnSurface
import com.example.nomadcompass.ui.theme.OnSurfaceVariant
import com.example.nomadcompass.ui.theme.Primary
import com.example.nomadcompass.ui.theme.PrimaryContainer
import com.example.nomadcompass.ui.theme.SurfaceContainer
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
    currencyCode: String = "USD",
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

                        Spacer(modifier = Modifier.height(16.dp))

                        // Unified Scrollable Content List
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
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
                                        icon = Icons.Default.Notes,
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
                                        onEditNote = { editingNote = item },
                                        onDelete = { onDeleteAttachment(item.id) },
                                        onOpenFile = { path, mime -> FileStorageHelper.openFile(context, path, mime) }
                                    )
                                }
                            }
                        }
                    }

                    // Floating Circular '+' Button at Bottom-Right
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(20.dp)
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(PrimaryContainer)
                            .border(1.5.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                            .clickable { isAddChoiceMenuOpen = true },
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
        }
    }

    // Add Options Selection Dialog (Pops up when clicking '+')
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
                                imageVector = Icons.Default.Notes,
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
                                    imageVector = Icons.Default.OpenInNew,
                                    contentDescription = null,
                                    tint = OnPrimary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "VIEW",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OnPrimary,
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

                // Option 1: PDF
                AddChoiceOptionCard(
                    icon = Icons.Default.PictureAsPdf,
                    title = "Upload PDF Document",
                    subtitle = "Attach tickets, vouchers, or passports",
                    tint = Color(0xFFEF5350),
                    onClick = onSelectPdf
                )

                // Option 2: Image
                AddChoiceOptionCard(
                    icon = Icons.Default.AddPhotoAlternate,
                    title = "Upload Saved Image",
                    subtitle = "Attach photos, receipts, or screenshots",
                    tint = Color(0xFF42A5F5),
                    onClick = onSelectImage
                )

                // Option 3: Note
                AddChoiceOptionCard(
                    icon = Icons.Default.NoteAdd,
                    title = "Create Trip Note",
                    subtitle = "Save Wi-Fi codes, addresses, or tips",
                    tint = Color(0xFFFFB300),
                    onClick = onSelectNote
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel", color = OnSurfaceVariant)
            }
        },
        containerColor = SurfaceContainer
    )
}

@Composable
private fun AddChoiceOptionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    tint: Color,
    onClick: () -> Unit,
) {
    ClayCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        cornerRadius = 14.dp,
        backgroundColor = SurfaceContainerLow
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = OnSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
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
    onSave: (title: String) -> Unit,
) {
    var title by remember { mutableStateOf(initialTitle) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (type == AttachmentType.PDF) "Title for PDF Upload" else "Title for Image Upload",
                style = MaterialTheme.typography.titleLarge,
                color = OnSurface,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Give this document a title so you know what it contains (e.g., Passport Scan, Tokyo Flight Ticket, Hotel Confirmation):",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title / Document Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface
                    )
                )
            }
        },
        confirmButton = {
            ClayButton(
                onClick = {
                    onSave(title.ifBlank { initialTitle })
                }
            ) {
                Text(text = "Save to Workspace", color = OnPrimary, modifier = Modifier.padding(horizontal = 12.dp))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel", color = OnSurfaceVariant)
            }
        },
        containerColor = SurfaceContainer
    )
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
        cornerRadius = 16.dp,
        backgroundColor = SurfaceContainerLow
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "$count", style = MaterialTheme.typography.titleMedium, color = OnSurface, fontWeight = FontWeight.Bold)
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant, fontSize = 10.sp)
        }
    }
}

@Composable
private fun AddNoteDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, text: String) -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Add Trip Note", style = MaterialTheme.typography.titleLarge, color = OnSurface, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Note Title (e.g. Wi-Fi & Co-working info)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = OnSurface, unfocusedTextColor = OnSurface)
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Note Details") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = OnSurface, unfocusedTextColor = OnSurface)
                )
            }
        },
        confirmButton = {
            ClayButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title, content)
                    }
                }
            ) {
                Text(text = "Save Note", color = OnPrimary, modifier = Modifier.padding(horizontal = 12.dp))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel", color = OnSurfaceVariant)
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
    onSave: (title: String, text: String) -> Unit,
) {
    var title by remember { mutableStateOf(initialTitle) }
    var content by remember { mutableStateOf(initialContent) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Edit Trip Note", style = MaterialTheme.typography.titleLarge, color = OnSurface, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Note Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = OnSurface, unfocusedTextColor = OnSurface)
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Note Details") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = OnSurface, unfocusedTextColor = OnSurface)
                )
            }
        },
        confirmButton = {
            ClayButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title, content)
                    }
                }
            ) {
                Text(text = "Save Changes", color = OnPrimary, modifier = Modifier.padding(horizontal = 12.dp))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel", color = OnSurfaceVariant)
            }
        },
        containerColor = SurfaceContainer
    )
}

private fun formatCurrencyAmount(amount: Double, currencyCode: String): String {
    val formattedNumber = String.format(Locale.US, "%,d", amount.toInt())
    val symbol = when (currencyCode.uppercase()) {
        "USD" -> "$"
        "EUR" -> "€"
        "GBP" -> "£"
        "INR" -> "₹"
        "JPY" -> "¥"
        "CAD" -> "CA$"
        "AUD" -> "A$"
        "BRL" -> "R$"
        "CHF" -> "CHF "
        else -> "$currencyCode "
    }
    return "$symbol$formattedNumber"
}
