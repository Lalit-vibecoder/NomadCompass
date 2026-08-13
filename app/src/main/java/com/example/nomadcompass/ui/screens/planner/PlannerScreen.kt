package com.example.nomadcompass.ui.screens.planner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.nomadcompass.domain.model.Trip
import androidx.compose.ui.platform.LocalContext
import com.example.nomadcompass.ui.components.ClayButton
import com.example.nomadcompass.ui.components.ClayCard
import com.example.nomadcompass.ui.components.TripWorkspaceModal
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

@Composable
fun PlannerScreen(
    viewModel: PlannerViewModel,
    onExploreClick: () -> Unit,
    onProfileClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .background(SurfaceContainerHigh)
                    .border(1.dp, Color.White.copy(alpha = 0.05f))
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.EventNote,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Trip Planner",
                        style = MaterialTheme.typography.titleLarge,
                        color = OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Profile Avatar on Right
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(SecondaryContainer)
                        .border(1.dp, Secondary.copy(alpha = 0.3f), CircleShape)
                        .clickable(onClick = onProfileClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Secondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(SurfaceContainerHigh)
                    .border(1.dp, Color.White.copy(alpha = 0.05f))
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem(
                    icon = Icons.Default.Explore,
                    label = "Explore",
                    isSelected = false,
                    onClick = onExploreClick
                )

                BottomNavItem(
                    icon = Icons.Default.EventNote,
                    label = "Planner",
                    isSelected = true,
                    onClick = { }
                )
            }
        },
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(PrimaryContainer)
                    .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                    .clickable { viewModel.openAddDialog() }
                    .padding(horizontal = 24.dp, vertical = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = OnPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PLAN NEW TRIP",
                        style = MaterialTheme.typography.labelLarge,
                        color = OnPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        containerColor = Background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header Description
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                Text(
                    text = "Your Nomad Work Legs",
                    style = MaterialTheme.typography.headlineMedium,
                    color = OnSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Organize upcoming destinations, monthly travel budgets, and remote work arrangements.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
            }

            if (uiState.trips.isEmpty()) {
                // Empty State View
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ClayCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 28.dp,
                        backgroundColor = SurfaceContainer
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlightTakeoff,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No Trips Planned Yet",
                                style = MaterialTheme.typography.titleLarge,
                                color = OnSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Map out your next destination, dates, and budget to keep your nomad travel organized.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            ClayButton(
                                onClick = { viewModel.openAddDialog() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = OnPrimary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Plan First Trip", color = OnPrimary, style = MaterialTheme.typography.labelLarge)
                                }
                            }
                        }
                    }
                }
            } else {
                // Trips List
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.trips, key = { it.id }) { trip ->
                        TripCardItem(
                            trip = trip,
                            currencyCode = uiState.userCurrencyCode,
                            onClick = { viewModel.openTripWorkspace(trip) },
                            onDelete = { viewModel.deleteTrip(trip.id) }
                        )
                    }
                }
            }
        }
    }

    // Add Trip Dialog Modal
    if (uiState.isAddDialogOpen) {
        AddTripDialog(
            uiState = uiState,
            onDismiss = viewModel::closeAddDialog,
            onCountryChanged = viewModel::onCountryChanged,
            onStartDateChanged = viewModel::onStartDateChanged,
            onEndDateChanged = viewModel::onEndDateChanged,
            onBudgetChanged = viewModel::onBudgetChanged,
            onNotesChanged = viewModel::onNotesChanged,
            onSave = viewModel::saveTrip
        )
    }

    // Trip Workspace Modal (PDFs, Images, Notes)
    if (uiState.activeWorkspaceTrip != null) {
        val context = LocalContext.current
        TripWorkspaceModal(
            trip = uiState.activeWorkspaceTrip!!,
            attachments = uiState.workspaceAttachments,
            onAddFileAttachment = { uri, type -> viewModel.addFileAttachment(context, uri, type) },
            onAddNoteAttachment = { title, text -> viewModel.addNoteAttachment(title, text) },
            onDeleteAttachment = viewModel::deleteAttachment,
            onDismiss = viewModel::closeTripWorkspace
        )
    }
}

@Composable
private fun TripCardItem(
    trip: Trip,
    currencyCode: String = "USD",
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    ClayCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        cornerRadius = 24.dp,
        backgroundColor = SurfaceContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row: Flag + Name + Status Pill + Delete
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
                                .size(28.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                    } else {
                        Text(text = trip.flagEmoji, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                    Text(
                        text = trip.countryName,
                        style = MaterialTheme.typography.titleMedium,
                        color = OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(SecondaryContainer)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = trip.status,
                            style = MaterialTheme.typography.labelSmall,
                            color = Secondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = OnSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier
                            .size(20.dp)
                            .clickable(onClick = onDelete)
                    )
                }
            }

            // Trip Details (Dates & Budget)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${trip.startDate} - ${trip.endDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachMoney,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${trip.budgetUsd.toInt()} $currencyCode / mo",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (trip.notes.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceContainerLow)
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.Note,
                            contentDescription = null,
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = trip.notes,
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                    }
                }
            }

            // Workspace Hint Pill
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CircleShape)
                    .background(SecondaryContainer.copy(alpha = 0.5f))
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📂 TAP TO OPEN WORKSPACE (PDFs, IMAGES, NOTES)",
                    style = MaterialTheme.typography.labelSmall,
                    color = Secondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun AddTripDialog(
    uiState: PlannerUiState,
    onDismiss: () -> Unit,
    onCountryChanged: (String) -> Unit,
    onStartDateChanged: (String) -> Unit,
    onEndDateChanged: (String) -> Unit,
    onBudgetChanged: (String) -> Unit,
    onNotesChanged: (String) -> Unit,
    onSave: () -> Unit,
) {
    var countryDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Plan New Trip",
                style = MaterialTheme.typography.titleLarge,
                color = OnSurface,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Destination Picker Dropdown
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "DESTINATION",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant,
                        letterSpacing = 1.sp
                    )
                    Box {
                        val selectedCountry = uiState.availableCountries.find { it.cca3 == uiState.selectedCca3 }
                        val displayLabel = if (selectedCountry != null) {
                            "${selectedCountry.flagEmoji}  ${selectedCountry.commonName}"
                        } else "Select destination"

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .clip(CircleShape)
                                .background(SurfaceContainerLow)
                                .border(1.dp, OnSurface.copy(alpha = 0.1f), CircleShape)
                                .clickable { countryDropdownExpanded = true }
                                .padding(horizontal = 20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Public,
                                    contentDescription = null,
                                    tint = OnSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = displayLabel,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = OnSurface
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = OnSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = countryDropdownExpanded,
                            onDismissRequest = { countryDropdownExpanded = false }
                        ) {
                            val favs = uiState.availableCountries.filter { it.isFavorite }
                            val others = uiState.availableCountries.filter { !it.isFavorite }

                            if (favs.isNotEmpty()) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "MY FAVS",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Secondary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    },
                                    onClick = {},
                                    enabled = false
                                )
                                favs.forEach { country ->
                                    DropdownMenuItem(
                                        text = { Text("❤️  ${country.flagEmoji}  ${country.commonName}") },
                                        onClick = {
                                            onCountryChanged(country.cca3)
                                            countryDropdownExpanded = false
                                        }
                                    )
                                }
                                if (others.isNotEmpty()) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 4.dp),
                                        color = OnSurface.copy(alpha = 0.1f)
                                    )
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "ALL DESTINATIONS",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = OnSurfaceVariant,
                                                fontWeight = FontWeight.Bold
                                            )
                                        },
                                        onClick = {},
                                        enabled = false
                                    )
                                }
                            }

                            others.forEach { country ->
                                DropdownMenuItem(
                                    text = { Text("${country.flagEmoji}  ${country.commonName}") },
                                    onClick = {
                                        onCountryChanged(country.cca3)
                                        countryDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Dates Row (Start & End) - Aligned horizontally & Oval Shaped
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "START DATE", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant, letterSpacing = 1.sp)
                        OutlinedTextField(
                            value = uiState.startDate,
                            onValueChange = onStartDateChanged,
                            singleLine = true,
                            shape = CircleShape,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary.copy(alpha = 0.5f),
                                unfocusedBorderColor = OnSurface.copy(alpha = 0.1f),
                                focusedContainerColor = SurfaceContainerLow,
                                unfocusedContainerColor = SurfaceContainerLow,
                                focusedTextColor = OnSurface,
                                unfocusedTextColor = OnSurface,
                            )
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "END DATE", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant, letterSpacing = 1.sp)
                        OutlinedTextField(
                            value = uiState.endDate,
                            onValueChange = onEndDateChanged,
                            singleLine = true,
                            shape = CircleShape,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary.copy(alpha = 0.5f),
                                unfocusedBorderColor = OnSurface.copy(alpha = 0.1f),
                                focusedContainerColor = SurfaceContainerLow,
                                unfocusedContainerColor = SurfaceContainerLow,
                                focusedTextColor = OnSurface,
                                unfocusedTextColor = OnSurface,
                            )
                        )
                    }
                }

                // Budget Field - Oval Shaped with user currency denomination
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "MONTHLY BUDGET (${uiState.userCurrencyCode})",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant,
                        letterSpacing = 1.sp
                    )
                    OutlinedTextField(
                        value = uiState.budgetUsd,
                        onValueChange = onBudgetChanged,
                        singleLine = true,
                        shape = CircleShape,
                        leadingIcon = {
                            Text(
                                text = uiState.userCurrencyCode,
                                style = MaterialTheme.typography.labelMedium,
                                color = Primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary.copy(alpha = 0.5f),
                            unfocusedBorderColor = OnSurface.copy(alpha = 0.1f),
                            focusedContainerColor = SurfaceContainerLow,
                            unfocusedContainerColor = SurfaceContainerLow,
                            focusedTextColor = OnSurface,
                            unfocusedTextColor = OnSurface,
                        )
                    )
                }

                // Notes Field - Oval Shaped
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "REMOTE WORK NOTES", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant, letterSpacing = 1.sp)
                    OutlinedTextField(
                        value = uiState.notes,
                        onValueChange = onNotesChanged,
                        placeholder = { Text("e.g. Co-working, e-SIM setup...", color = OnSurfaceVariant.copy(alpha = 0.5f)) },
                        singleLine = true,
                        shape = CircleShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary.copy(alpha = 0.5f),
                            unfocusedBorderColor = OnSurface.copy(alpha = 0.1f),
                            focusedContainerColor = SurfaceContainerLow,
                            unfocusedContainerColor = SurfaceContainerLow,
                            focusedTextColor = OnSurface,
                            unfocusedTextColor = OnSurface,
                        )
                    )
                }
            }
        },
        confirmButton = {
            ClayButton(onClick = onSave) {
                Text(text = "Save Trip", color = OnPrimary, modifier = Modifier.padding(horizontal = 16.dp))
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
private fun BottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) SecondaryContainer else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) Secondary else OnSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) Secondary else OnSurfaceVariant,
                fontSize = 11.sp
            )
        }
    }
}
