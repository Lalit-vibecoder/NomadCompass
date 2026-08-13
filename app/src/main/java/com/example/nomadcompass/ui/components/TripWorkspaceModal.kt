package com.example.nomadcompass.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Image
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.nomadcompass.ui.theme.Secondary
import com.example.nomadcompass.ui.theme.SecondaryContainer
import com.example.nomadcompass.ui.theme.SurfaceContainer
import com.example.nomadcompass.ui.theme.SurfaceContainerHigh
import com.example.nomadcompass.ui.theme.SurfaceContainerLow
import com.example.nomadcompass.util.FileStorageHelper
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TripWorkspaceModal(
    trip: Trip,
    attachments: List<TripAttachment>,
    onAddFileAttachment: (Uri, AttachmentType) -> Unit,
    onAddNoteAttachment: (title: String, text: String) -> Unit,
    onDeleteAttachment: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var isAddNoteDialogOpen by remember { mutableStateOf(false) }

    // Pickers for PDF and Image
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onAddFileAttachment(uri, AttachmentType.PDF)
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onAddFileAttachment(uri, AttachmentType.IMAGE)
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
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            ClayCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.92f),
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
                            if (trip.flagUrl.isNotBlank()) {
                                AsyncImage(
                                    model = trip.flagUrl,
                                    contentDescription = trip.countryName,
                                    modifier = Modifier
                                        .size(32.dp)
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
                                    text = "Trip Workspace & Documents",
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

                    // Tab Navigation Bar
                    val tabs = listOf("Overview", "Documents (${attachments.count { it.type != AttachmentType.NOTE }})", "Notes (${attachments.count { it.type == AttachmentType.NOTE }})")
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = SurfaceContainerHigh,
                        contentColor = Primary,
                        indicator = { tabPositions ->
                            if (selectedTabIndex < tabPositions.size) {
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                    color = Primary
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (selectedTabIndex == index) Primary else OnSurfaceVariant,
                                        fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tab Contents
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        when (selectedTabIndex) {
                            0 -> OverviewTabContent(
                                trip = trip,
                                attachments = attachments,
                                onAddPdfClick = { pdfPickerLauncher.launch("application/pdf") },
                                onAddImageClick = { imagePickerLauncher.launch("image/*") },
                                onAddNoteClick = { isAddNoteDialogOpen = true }
                            )
                            1 -> DocumentsTabContent(
                                attachments = attachments.filter { it.type != AttachmentType.NOTE },
                                onAddPdfClick = { pdfPickerLauncher.launch("application/pdf") },
                                onAddImageClick = { imagePickerLauncher.launch("image/*") },
                                onDeleteAttachment = onDeleteAttachment,
                                onOpenFile = { path, mime -> FileStorageHelper.openFile(context, path, mime) }
                            )
                            2 -> NotesTabContent(
                                notes = attachments.filter { it.type == AttachmentType.NOTE },
                                onAddNoteClick = { isAddNoteDialogOpen = true },
                                onDeleteAttachment = onDeleteAttachment
                            )
                        }
                    }
                }
            }
        }
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
}

@Composable
private fun OverviewTabContent(
    trip: Trip,
    attachments: List<TripAttachment>,
    onAddPdfClick: () -> Unit,
    onAddImageClick: () -> Unit,
    onAddNoteClick: () -> Unit,
) {
    val pdfCount = attachments.count { it.type == AttachmentType.PDF }
    val imageCount = attachments.count { it.type == AttachmentType.IMAGE }
    val noteCount = attachments.count { it.type == AttachmentType.NOTE }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Quick Meta Card
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
                        Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, tint = Primary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "${trip.startDate} to ${trip.endDate}", style = MaterialTheme.typography.bodyMedium, color = OnSurface)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AttachMoney, contentDescription = null, tint = Primary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "$${trip.budgetUsd.toInt()} / mo", style = MaterialTheme.typography.titleMedium, color = Primary, fontWeight = FontWeight.Bold)
                    }
                }

                if (trip.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Remote Work Plan:", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = trip.notes, style = MaterialTheme.typography.bodyMedium, color = OnSurface)
                }
            }
        }

        // Summary Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AttachmentStatBox(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.PictureAsPdf,
                count = pdfCount,
                label = "PDF Documents",
                tint = Color(0xFFEF5350)
            )
            AttachmentStatBox(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Image,
                count = imageCount,
                label = "Saved Images",
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

        // Action Buttons Row
        Text(text = "QUICK ADD TO WORKSPACE", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant, fontSize = 11.sp)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AddActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.PictureAsPdf,
                label = "+ PDF",
                onClick = onAddPdfClick
            )
            AddActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.AddPhotoAlternate,
                label = "+ Image",
                onClick = onAddImageClick
            )
            AddActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.NoteAdd,
                label = "+ Note",
                onClick = onAddNoteClick
            )
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
private fun AddActionButton(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(SecondaryContainer)
            .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = Secondary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = Secondary, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun DocumentsTabContent(
    attachments: List<TripAttachment>,
    onAddPdfClick: () -> Unit,
    onAddImageClick: () -> Unit,
    onDeleteAttachment: (Long) -> Unit,
    onOpenFile: (String, String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Toolbar Add Actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(CircleShape)
                    .background(PrimaryContainer)
                    .clickable(onClick = onAddPdfClick)
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, tint = OnPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "ADD PDF", style = MaterialTheme.typography.labelMedium, color = OnPrimary, fontWeight = FontWeight.Bold)
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(CircleShape)
                    .background(PrimaryContainer)
                    .clickable(onClick = onAddImageClick)
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, tint = OnPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "ADD IMAGE", style = MaterialTheme.typography.labelMedium, color = OnPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (attachments.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.FolderZip, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "No PDFs or Images Attached", style = MaterialTheme.typography.titleMedium, color = OnSurface)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Tap 'ADD PDF' or 'ADD IMAGE' above to attach tickets, passports, or vouchers.", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(attachments, key = { it.id }) { item ->
                    if (item.type == AttachmentType.PDF) {
                        PdfAttachmentCard(
                            attachment = item,
                            onOpen = { if (item.filePath != null) onOpenFile(item.filePath, item.mimeType) },
                            onDelete = { onDeleteAttachment(item.id) }
                        )
                    } else {
                        ImageAttachmentCard(
                            attachment = item,
                            onOpen = { if (item.filePath != null) onOpenFile(item.filePath, item.mimeType) },
                            onDelete = { onDeleteAttachment(item.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PdfAttachmentCard(
    attachment: TripAttachment,
    onOpen: () -> Unit,
    onDelete: () -> Unit,
) {
    ClayCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 16.dp,
        backgroundColor = SurfaceContainerLow
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEF5350).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, tint = Color(0xFFEF5350), modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = attachment.title, style = MaterialTheme.typography.titleMedium, color = OnSurface, fontWeight = FontWeight.Bold, maxLines = 1)
                    Text(text = FileStorageHelper.formatFileSize(attachment.fileSize), style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(PrimaryContainer)
                        .clickable(onClick = onOpen)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, tint = OnPrimary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "VIEW", style = MaterialTheme.typography.labelSmall, color = OnPrimary, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = OnSurfaceVariant.copy(alpha = 0.6f), modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun ImageAttachmentCard(
    attachment: TripAttachment,
    onOpen: () -> Unit,
    onDelete: () -> Unit,
) {
    ClayCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 16.dp,
        backgroundColor = SurfaceContainerLow
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (attachment.filePath != null) {
                    AsyncImage(
                        model = File(attachment.filePath),
                        contentDescription = attachment.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF42A5F5).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Image, contentDescription = null, tint = Color(0xFF42A5F5))
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = attachment.title, style = MaterialTheme.typography.titleMedium, color = OnSurface, fontWeight = FontWeight.Bold, maxLines = 1)
                    Text(text = FileStorageHelper.formatFileSize(attachment.fileSize), style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(PrimaryContainer)
                        .clickable(onClick = onOpen)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, tint = OnPrimary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "VIEW", style = MaterialTheme.typography.labelSmall, color = OnPrimary, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = OnSurfaceVariant.copy(alpha = 0.6f), modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun NotesTabContent(
    notes: List<TripAttachment>,
    onAddNoteClick: () -> Unit,
    onDeleteAttachment: (Long) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CircleShape)
                .background(PrimaryContainer)
                .clickable(onClick = onAddNoteClick)
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = OnPrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "+ ADD NEW NOTE", style = MaterialTheme.typography.labelLarge, color = OnPrimary, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (notes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.Notes, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "No Trip Notes Yet", style = MaterialTheme.typography.titleMedium, color = OnSurface)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Keep track of Wi-Fi passwords, co-working links, or travel tips.", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notes, key = { it.id }) { noteItem ->
                    ClayCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 16.dp,
                        backgroundColor = SurfaceContainerLow
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = noteItem.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Primary,
                                    fontWeight = FontWeight.Bold
                                )
                                val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date(noteItem.createdAt))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = dateStr, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(onClick = { onDeleteAttachment(noteItem.id) }, modifier = Modifier.size(24.dp)) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = OnSurfaceVariant.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                            if (!noteItem.content.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = noteItem.content, style = MaterialTheme.typography.bodyMedium, color = OnSurface)
                            }
                        }
                    }
                }
            }
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
                    label = { Text("Note Title (e.g. Co-Working Password)") },
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
                        .height(120.dp),
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
