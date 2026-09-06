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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.layout.ContentScale
import java.io.File
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
import com.example.nomadcompass.ui.components.GlassPillButton
import com.example.nomadcompass.ui.components.clayShadow
import com.example.nomadcompass.ui.components.NomadBottomNavigationBar
import com.example.nomadcompass.ui.components.NomadNavTab
import com.example.nomadcompass.ui.components.TripWorkspaceModal
import com.example.nomadcompass.ui.theme.LocalThemeController
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

import androidx.compose.foundation.layout.statusBarsPadding

import com.example.nomadcompass.ui.components.bounceClick
import com.example.nomadcompass.ui.components.bounceOnState

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
                    .background(SurfaceContainerHigh)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.EventNote,
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
                val photoUri = uiState.userProfile?.photoUri
                val photoFile = if (!photoUri.isNullOrBlank()) File(photoUri) else null

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(SecondaryContainer)
                        .border(1.5.dp, if (photoFile != null && photoFile.exists()) Primary else Secondary.copy(alpha = 0.3f), CircleShape)
                        .bounceClick(onClick = onProfileClick),
                    contentAlignment = Alignment.Center
                ) {
                    if (photoFile != null && photoFile.exists()) {
                        AsyncImage(
                            model = photoFile,
                            contentDescription = "Profile",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Profile & Settings",
                            tint = OnSurface,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            NomadBottomNavigationBar(
                currentTab = NomadNavTab.PLANNER,
                onExploreClick = onExploreClick,
                onPlannerClick = { }
            )
        },
        floatingActionButton = {
            val isDark = LocalThemeController.current.isDarkMode
            // Floating Action Button - Glass Pill matching bottom navigation bar
            GlassPillButton(
                onClick = { viewModel.openAddDialog() },
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Primary.copy(alpha = if (isDark) 0.25f else 0.15f))
                        .border(1.dp, Primary.copy(alpha = if (isDark) 0.45f else 0.30f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "PLAN NEW TRIP",
                    style = MaterialTheme.typography.labelLarge,
                    color = Primary,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
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
                            val isDarkEmpty = LocalThemeController.current.isDarkMode
                            GlassPillButton(
                                onClick = { viewModel.openAddDialog() },
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(vertical = 14.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(Primary.copy(alpha = if (isDarkEmpty) 0.25f else 0.15f))
                                        .border(1.dp, Primary.copy(alpha = if (isDarkEmpty) 0.45f else 0.30f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        tint = Primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Plan First Trip",
                                    color = Primary,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            } else {
                // Trips List
                LazyColumn(
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = uiState.trips,
                        key = { it.id },
                        contentType = { "trip_card" }
                    ) { trip ->
                        TripCardItem(
                            trip = trip,
                            onClick = { viewModel.openTripWorkspace(trip) }
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

    // Trip Workspace Modal (Expenses, Docs, Itinerary, Packing)
    if (uiState.activeWorkspaceTrip != null) {
        val context = LocalContext.current
        TripWorkspaceModal(
            trip = uiState.activeWorkspaceTrip!!,
            attachments = uiState.workspaceAttachments,
            expenses = uiState.workspaceExpenses,
            packingItems = uiState.workspacePackingItems,
            itineraryEvents = uiState.workspaceItineraryEvents,
            totalSpentHome = uiState.totalSpentHome,
            activeTab = uiState.activeWorkspaceTab,
            currencyCode = uiState.userCurrencyCode,
            tripDestinationCurrencyCode = uiState.tripDestinationCurrencyCode,
            onSelectTab = viewModel::selectWorkspaceTab,
            onAddExpense = viewModel::addExpense,
            onDeleteExpense = viewModel::deleteExpense,
            onCalculateLivePreview = viewModel::calculateLiveConversion,
            onTogglePackingItem = viewModel::togglePackingItem,
            onAddPackingItem = viewModel::addPackingItem,
            onDeletePackingItem = viewModel::deletePackingItem,
            onSaveItineraryEvents = viewModel::saveItineraryEvents,
            onAddItineraryEvent = viewModel::addItineraryEvent,
            onUpdateItineraryEvent = viewModel::updateItineraryEvent,
            onDeleteItineraryEvent = viewModel::deleteItineraryEvent,
            onAddFileAttachment = { uri, type, customTitle, category -> viewModel.addFileAttachment(context, uri, type, customTitle, category) },
            onAddNoteAttachment = { title, text, category -> viewModel.addNoteAttachment(title, text, category) },
            onUpdateNoteAttachment = { id, title, text -> viewModel.updateNoteAttachment(id, title, text) },
            onMoveAttachment = viewModel::moveAttachment,
            onDeleteAttachment = viewModel::deleteAttachment,
            onDeleteTrip = { viewModel.deleteTrip(uiState.activeWorkspaceTrip!!.id) },
            onDismiss = viewModel::closeTripWorkspace
        )
    }
}

@Composable
private fun TripCardItem(
    trip: Trip,
    onClick: () -> Unit,
) {
    val isDark = LocalThemeController.current.isDarkMode

    // Color-coded status badge styling
    val (statusColor, statusBg) = when (trip.status.lowercase()) {
        "upcoming" -> Pair(Color(0xFF2E7D32), if (isDark) Color(0xFF1B5E20).copy(alpha = 0.35f) else Color(0xFFE8F5E9))
        "completed" -> Pair(Color(0xFF1976D2), if (isDark) Color(0xFF0D47A1).copy(alpha = 0.35f) else Color(0xFFE3F2FD))
        else -> Pair(Color(0xFFEF6C00), if (isDark) Color(0xFFE65100).copy(alpha = 0.35f) else Color(0xFFFFF3E0)) // In Progress / Active
    }

    // Trip Readiness calculation: 75% for Upcoming, 90% for In Progress, 100% for Completed
    val readinessPercent = when (trip.status.lowercase()) {
        "completed" -> 100
        "in progress", "active" -> 90
        else -> 75
    }
    val readinessFraction = readinessPercent / 100f

    ClayCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        cornerRadius = 24.dp,
        backgroundColor = SurfaceContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row: Destination with Left Circular Flag + Color-Coded Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Destination Code & Name with Left Circular Flag
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Primary.copy(alpha = 0.15f))
                            .border(1.dp, Primary.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (trip.flagUrl.isNotBlank()) {
                            AsyncImage(
                                model = trip.flagUrl,
                                contentDescription = trip.countryName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        } else {
                            Text(text = trip.flagEmoji, fontSize = 18.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "${trip.destinationCca3.uppercase()} • ${trip.countryName}",
                            style = MaterialTheme.typography.titleMedium,
                            color = OnSurface,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Visual Status Badge Tag
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(statusBg)
                        .border(1.dp, statusColor.copy(alpha = 0.3f), CircleShape)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = trip.status,
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Trip Duration / Dates Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "${trip.startDate} — ${trip.endDate}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = OnSurface
                )
            }

            // Trip Preparation / Planning Readiness Indicator
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Trip Preparation",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "$readinessPercent% Ready",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Primary,
                        fontSize = 11.sp
                    )
                }

                // Subtle visual progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(SurfaceContainerHigh)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(readinessFraction)
                            .clip(CircleShape)
                            .background(
                                brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                    colors = listOf(Primary, Primary.copy(alpha = 0.8f))
                                )
                            )
                    )
                }
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
                                .clip(RoundedCornerShape(14.dp))
                                .background(SurfaceContainerLow)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                                .clickable { countryDropdownExpanded = true }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Public,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = displayLabel,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = OnSurface,
                                    fontWeight = FontWeight.Medium
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

                // Dates Row (Start & End) - Clean RoundedCornerShape(14.dp)
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
                            placeholder = { Text("YYYY-MM-DD", fontSize = 12.sp, color = OnSurfaceVariant.copy(alpha = 0.5f)) },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth(),
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
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "END DATE", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant, letterSpacing = 1.sp)
                        OutlinedTextField(
                            value = uiState.endDate,
                            onValueChange = onEndDateChanged,
                            placeholder = { Text("YYYY-MM-DD", fontSize = 12.sp, color = OnSurfaceVariant.copy(alpha = 0.5f)) },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth(),
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
                }

                // Budget Field
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
                        placeholder = { Text("e.g. 2500", color = OnSurfaceVariant.copy(alpha = 0.5f)) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        leadingIcon = {
                            Text(
                                text = uiState.userCurrencyCode,
                                style = MaterialTheme.typography.labelMedium,
                                color = Primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
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

                // Notes Field
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "REMOTE WORK NOTES", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant, letterSpacing = 1.sp)
                    OutlinedTextField(
                        value = uiState.notes,
                        onValueChange = onNotesChanged,
                        placeholder = { Text("e.g. Co-working, e-SIM setup...", color = OnSurfaceVariant.copy(alpha = 0.5f)) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth(),
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
            }
        },
        confirmButton = {
            GlassPillButton(
                onClick = onSave,
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Save Trip",
                    color = Primary,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
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
    val animatedBgColor by androidx.compose.animation.animateColorAsState(
        targetValue = if (isSelected) SecondaryContainer else Color.Transparent,
        animationSpec = androidx.compose.animation.core.tween(200),
        label = "planner_nav_bg"
    )
    val animatedContentColor by androidx.compose.animation.animateColorAsState(
        targetValue = if (isSelected) Secondary else OnSurfaceVariant,
        animationSpec = androidx.compose.animation.core.tween(200),
        label = "planner_nav_color"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(animatedBgColor)
            .bounceClick(scaleDown = 0.92f, onClick = onClick)
            .bounceOnState(state = isSelected, maxScale = 1.08f)
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
                tint = animatedContentColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = animatedContentColor,
                fontSize = 11.sp
            )
        }
    }
}
