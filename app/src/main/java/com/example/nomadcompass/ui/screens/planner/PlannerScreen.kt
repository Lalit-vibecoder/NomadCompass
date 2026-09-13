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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.nomadcompass.domain.model.Trip
import com.example.nomadcompass.ui.components.ClayCard
import com.example.nomadcompass.ui.components.FrostedGlassDialog
import com.example.nomadcompass.ui.components.GlassPillButton
import com.example.nomadcompass.ui.components.NomadBottomNavigationBar
import com.example.nomadcompass.ui.components.NomadNavTab
import com.example.nomadcompass.ui.components.TripWorkspaceModal
import com.example.nomadcompass.ui.components.bounceClick
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.ui.draw.blur
import com.example.nomadcompass.ui.components.clayShadow
import com.example.nomadcompass.ui.theme.LocalThemeController
import com.example.nomadcompass.ui.theme.OnSurface
import com.example.nomadcompass.ui.theme.OnSurfaceVariant
import com.example.nomadcompass.ui.theme.Primary
import com.example.nomadcompass.ui.theme.Secondary
import com.example.nomadcompass.ui.theme.SurfaceContainer
import com.example.nomadcompass.ui.theme.SurfaceContainerHigh
import com.example.nomadcompass.ui.theme.SurfaceContainerLow
import java.io.File

@Composable
fun PlannerScreen(
    viewModel: PlannerViewModel,
    onExploreClick: () -> Unit,
    onProfileClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val isDark = LocalThemeController.current.isDarkMode

    val isModalOpen = uiState.activeWorkspaceTrip != null || uiState.isAddDialogOpen
    val backgroundBlur by animateDpAsState(
        targetValue = if (isModalOpen) 20.dp else 0.dp,
        label = "planner_bg_blur"
    )

    Scaffold(
        modifier = Modifier.blur(backgroundBlur),
        topBar = {
            val photoUri = uiState.userProfile?.photoUri
            val photoFile = if (!photoUri.isNullOrBlank()) File(photoUri) else null

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF132A22).copy(alpha = 0.85f),
                                Color(0xFF132A22).copy(alpha = 0.40f),
                                Color.Transparent
                            )
                        )
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Header Title with Icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clayShadow(
                                cornerRadius = 9999.dp,
                                ambientShadowColor = Color.Black.copy(alpha = if (isDark) 0.35f else 0.12f),
                                spotShadowColor = Color.Black.copy(alpha = if (isDark) 0.45f else 0.18f),
                                blurRadius = 6.dp
                            )
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = if (isDark) 0.14f else 0.22f))
                            .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = if (isDark) 0.28f else 0.45f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.EventNote,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Text(
                        text = "Trip Planner",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = OnSurface
                    )
                }

                // Profile Avatar on Right
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .bounceClick(onClick = onProfileClick)
                        .clayShadow(
                            cornerRadius = 9999.dp,
                            ambientShadowColor = Color.Black.copy(alpha = if (isDark) 0.40f else 0.15f),
                            spotShadowColor = Color.Black.copy(alpha = if (isDark) 0.50f else 0.20f),
                            blurRadius = 6.dp
                        )
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = if (isDark) 0.15f else 0.25f))
                        .border(1.2.dp, Color.White.copy(alpha = if (isDark) 0.35f else 0.50f), CircleShape),
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
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = OnSurface,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            // Floating Action Button - Glass Pill elevated above bottom navigation bar
            GlassPillButton(
                onClick = { viewModel.openAddDialog() },
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(bottom = 88.dp),
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds(),
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding(),
                bottom = 180.dp
            )
        ) {
            // Hero Title matching Explore screen typography
            item(key = "hero_title") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Your Nomad\nWork Legs",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 38.sp
                        ),
                        color = OnSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Organize upcoming destinations, monthly travel budgets, and remote work arrangements.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp
                        ),
                        color = OnSurfaceVariant
                    )
                }
            }

            // Section Header: "Your Planned Trips" + "+ New Trip"
            item(key = "section_header") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Planned Trips",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = OnSurface
                    )
                    Text(
                        text = "+ New Trip",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Primary,
                        modifier = Modifier
                            .clickable { viewModel.openAddDialog() }
                            .padding(4.dp)
                    )
                }
            }

            if (uiState.trips.isEmpty()) {
                item(key = "empty_trips") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlightTakeoff,
                                contentDescription = null,
                                tint = OnSurfaceVariant,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No Trips Planned Yet",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = OnSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Map out your next destination, dates, and budget to keep your nomad travel organized.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            GlassPillButton(
                                onClick = { viewModel.openAddDialog() },
                                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp)
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
                items(
                    items = uiState.trips,
                    key = { it.id }
                ) { trip ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        SageTripCard(
                            trip = trip,
                            currencyCode = uiState.userCurrencyCode,
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
            bgPhotoUri = uiState.userProfile?.bgPhotoUri,
            bgBlurRadius = uiState.userProfile?.bgBlurRadius ?: 24f,
            onSelectTab = viewModel::selectWorkspaceTab,
            onAddExpense = viewModel::addExpense,
            onEditExpense = viewModel::updateExpense,
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

/**
 * Compact Sage Green Trip Card matching Explore screen aesthetics without oversized height or thick outer framing.
 * Features frosted sage glass frame, subtle specular highlight, circular flag icon, clean status badge,
 * compact date and budget chips, and preparation readiness progress bar.
 */
private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
private fun SageTripCard(
    trip: Trip,
    currencyCode: String,
    onClick: () -> Unit,
) {
    val isDark = LocalThemeController.current.isDarkMode
    val cardShape = remember { RoundedCornerShape(24.dp) }

    val sageGradientStart = if (isDark) Color(0xFF537E76) else Color(0xFF7AA59D)
    val sageGradientEnd = if (isDark) Color(0xFF385E56) else Color(0xFF59857D)

    val cardBgBrush = remember(isDark) {
        Brush.verticalGradient(
            colors = listOf(
                sageGradientStart.copy(alpha = if (isDark) 0.55f else 0.70f),
                sageGradientEnd.copy(alpha = if (isDark) 0.40f else 0.55f)
            )
        )
    }
    val cardSheenBrush = remember(isDark) {
        Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = if (isDark) 0.12f else 0.20f),
                Color.White.copy(alpha = if (isDark) 0.02f else 0.05f),
                Color.Transparent
            )
        )
    }
    val cardBorderBrush = remember(isDark) {
        Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = if (isDark) 0.35f else 0.50f),
                Color.White.copy(alpha = if (isDark) 0.08f else 0.18f)
            )
        )
    }
    val specularBrush = remember(isDark) {
        Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,
                Color.White.copy(alpha = if (isDark) 0.35f else 0.60f),
                Color.Transparent
            )
        )
    }
    val primaryColor = Primary
    val readinessBrush = remember(primaryColor) {
        Brush.horizontalGradient(
            colors = listOf(
                primaryColor,
                primaryColor.copy(alpha = 0.85f)
            )
        )
    }

    // High-contrast vibrant status styling for dark/sage card surfaces
    val (statusLabel, statusTextColor, statusBg, statusBorder) = when (trip.status.lowercase()) {
        "upcoming" -> Quadruple(
            "Upcoming",
            Color(0xFF81C784),
            Color(0xFF1B5E20).copy(alpha = 0.55f),
            Color(0xFF81C784).copy(alpha = 0.60f)
        )
        "completed" -> Quadruple(
            "Completed",
            Color(0xFF90CAF9),
            Color(0xFF0D47A1).copy(alpha = 0.55f),
            Color(0xFF90CAF9).copy(alpha = 0.60f)
        )
        else -> Quadruple(
            "In Progress",
            Color(0xFFFFB74D),
            Color(0xFFE65100).copy(alpha = 0.55f),
            Color(0xFFFFB74D).copy(alpha = 0.60f)
        )
    }

    val readinessPercent = when (trip.status.lowercase()) {
        "completed" -> 100
        "in progress", "active" -> 90
        else -> 75
    }
    val readinessFraction = readinessPercent / 100f

    // Compact Sage Card Frame matching Explore Visuals with standard padding
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .bounceClick(scaleDown = 0.98f, onClick = onClick)
            .clayShadow(
                cornerRadius = 24.dp,
                ambientShadowColor = Color.Black.copy(alpha = if (isDark) 0.35f else 0.12f),
                spotShadowColor = Color.Black.copy(alpha = if (isDark) 0.45f else 0.18f),
                blurRadius = 12.dp
            )
            .clip(cardShape)
            .background(
                brush = cardBgBrush,
                shape = cardShape
            )
            .background(
                brush = cardSheenBrush,
                shape = cardShape
            )
            .border(
                width = 1.2.dp,
                brush = cardBorderBrush,
                shape = cardShape
            )
            .drawBehind {
                // Specular top edge ambient highlight reflection
                drawRect(
                    brush = specularBrush,
                    size = size.copy(height = 1.5.dp.toPx())
                )
            }
            .padding(18.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row: Circular Flag + Destination Title + Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Circular flag with glass border
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clayShadow(
                                cornerRadius = 9999.dp,
                                ambientShadowColor = Color.Black.copy(alpha = 0.25f),
                                spotShadowColor = Color.Black.copy(alpha = 0.30f),
                                blurRadius = 4.dp
                            )
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                            .border(1.dp, Color.White.copy(alpha = 0.40f), CircleShape),
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

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "${trip.destinationCca3.uppercase()} • ${trip.countryName.ifBlank { trip.destinationCca3 }}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Visual Status Badge Tag
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(statusBg)
                        .border(1.dp, statusBorder, CircleShape)
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = statusLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = statusTextColor
                    )
                }
            }

            // Info Chips: Dates & Budget Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Dates Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(Color.Black.copy(alpha = if (isDark) 0.30f else 0.18f))
                        .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(9999.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${trip.startDate}  ➔  ${trip.endDate}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = Color.White
                        )
                    }
                }

                // Budget Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(Color.Black.copy(alpha = if (isDark) 0.30f else 0.18f))
                        .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(9999.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = currencyCode,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Primary
                        )
                        Text(
                            text = "${trip.budgetUsd} / mo",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = Color.White.copy(alpha = 0.92f)
                        )
                    }
                }
            }

            // Readiness Indicator with Progress Bar
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
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Text(
                        text = "$readinessPercent% Ready",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                }

                // Sleek progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.30f))
                        .border(0.5.dp, Color.White.copy(alpha = 0.20f), CircleShape)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(readinessFraction)
                            .clip(CircleShape)
                            .background(
                                brush = readinessBrush
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
    val selectedCountry = remember(uiState.selectedCca3, uiState.availableCountries) {
        uiState.availableCountries.find { it.cca3 == uiState.selectedCca3 }
    }
    var isDestinationEditing by remember { mutableStateOf(false) }
    var destinationSearchText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val matchingSuggestions = remember(destinationSearchText, uiState.availableCountries, isDestinationEditing) {
        if (!isDestinationEditing || destinationSearchText.isBlank()) {
            emptyList()
        } else {
            val q = destinationSearchText.trim()
            uiState.availableCountries.filter {
                it.commonName.contains(q, ignoreCase = true) ||
                it.cca3.contains(q, ignoreCase = true) ||
                it.region.contains(q, ignoreCase = true)
            }.take(5)
        }
    }

    FrostedGlassDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .wrapContentHeight()
            .padding(vertical = 16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
                // Header with frosted icon and close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Primary.copy(alpha = 0.15f))
                                .border(1.dp, Primary.copy(alpha = 0.35f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Public,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Plan New Trip",
                                style = MaterialTheme.typography.titleLarge,
                                color = OnSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Set up your destination & workspace",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainerHigh.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Destination Search & Autocomplete
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "DESTINATION",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant,
                        letterSpacing = 1.sp
                    )

                    OutlinedTextField(
                        value = if (isDestinationEditing) {
                            destinationSearchText
                        } else {
                            selectedCountry?.let { "${it.flagEmoji}  ${it.commonName}" } ?: ""
                        },
                        onValueChange = { newText ->
                            isDestinationEditing = true
                            destinationSearchText = newText
                        },
                        placeholder = {
                            Text(
                                text = "Search country (e.g. Japan, Spain)...",
                                color = OnSurfaceVariant.copy(alpha = 0.5f),
                                fontSize = 14.sp
                            )
                        },
                        leadingIcon = {
                            if (!isDestinationEditing && selectedCountry != null) {
                                Text(
                                    text = selectedCountry.flagEmoji,
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(start = 12.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Public,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier
                                        .padding(start = 12.dp)
                                        .size(20.dp)
                                )
                            }
                        },
                        trailingIcon = {
                            if (isDestinationEditing && destinationSearchText.isNotEmpty()) {
                                IconButton(onClick = {
                                    destinationSearchText = ""
                                    focusRequester.requestFocus()
                                    keyboardController?.show()
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear input",
                                        tint = OnSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            } else {
                                IconButton(onClick = {
                                    isDestinationEditing = true
                                    destinationSearchText = ""
                                    focusRequester.requestFocus()
                                    keyboardController?.show()
                                }) {
                                    Icon(
                                        imageVector = if (isDestinationEditing) Icons.Default.Search else Icons.Default.ExpandMore,
                                        contentDescription = "Search destination",
                                        tint = Primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused) {
                                    if (!isDestinationEditing) {
                                        isDestinationEditing = true
                                        destinationSearchText = ""
                                    }
                                    keyboardController?.show()
                                }
                            },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                            focusedContainerColor = SurfaceContainerLow.copy(alpha = 0.85f),
                            unfocusedContainerColor = SurfaceContainerLow.copy(alpha = 0.85f),
                            focusedTextColor = OnSurface,
                            unfocusedTextColor = OnSurface,
                            cursorColor = Primary,
                        )
                    )

                    // Autocomplete Suggestions List (Frosted Glass Container)
                    AnimatedVisibility(
                        visible = isDestinationEditing,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(SurfaceContainerHigh.copy(alpha = 0.95f))
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (destinationSearchText.isBlank()) {
                                val favs = remember(uiState.availableCountries) {
                                    uiState.availableCountries.filter { it.isFavorite }.take(3)
                                }
                                Text(
                                    text = if (favs.isNotEmpty()) "FAVORITE DESTINATIONS (TYPE TO SEARCH)" else "TYPE COUNTRY NAME TO SEARCH",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OnSurfaceVariant,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    letterSpacing = 1.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                                favs.forEach { country ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable {
                                                onCountryChanged(country.cca3)
                                                destinationSearchText = country.commonName
                                                isDestinationEditing = false
                                                keyboardController?.hide()
                                                focusManager.clearFocus()
                                            }
                                            .padding(horizontal = 10.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = country.flagEmoji, fontSize = 18.sp)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = country.commonName,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = OnSurface,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                        Text("❤️", fontSize = 12.sp)
                                    }
                                }
                            } else if (matchingSuggestions.isEmpty()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        tint = OnSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "No destinations matching \"$destinationSearchText\"",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = OnSurfaceVariant
                                    )
                                }
                            } else {
                                Text(
                                    text = "SUGGESTIONS",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Primary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    letterSpacing = 1.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                                matchingSuggestions.forEach { country ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable {
                                                onCountryChanged(country.cca3)
                                                destinationSearchText = country.commonName
                                                isDestinationEditing = false
                                                keyboardController?.hide()
                                                focusManager.clearFocus()
                                            }
                                            .padding(horizontal = 10.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = country.flagEmoji, fontSize = 20.sp)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = country.commonName,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = OnSurface,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                                Text(
                                                    text = "${country.region} • ${country.cca3}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = OnSurfaceVariant,
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }
                                        if (country.isFavorite) {
                                            Text("❤️", fontSize = 14.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Dates Row (Start & End)
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
                                focusedContainerColor = SurfaceContainerLow.copy(alpha = 0.85f),
                                unfocusedContainerColor = SurfaceContainerLow.copy(alpha = 0.85f),
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
                                focusedContainerColor = SurfaceContainerLow.copy(alpha = 0.85f),
                                unfocusedContainerColor = SurfaceContainerLow.copy(alpha = 0.85f),
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
                            focusedContainerColor = SurfaceContainerLow.copy(alpha = 0.85f),
                            unfocusedContainerColor = SurfaceContainerLow.copy(alpha = 0.85f),
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
                            focusedContainerColor = SurfaceContainerLow.copy(alpha = 0.85f),
                            unfocusedContainerColor = SurfaceContainerLow.copy(alpha = 0.85f),
                            focusedTextColor = OnSurface,
                            unfocusedTextColor = OnSurface,
                            cursorColor = Primary,
                        )
                    )
                }

                // Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text = "Cancel", color = OnSurfaceVariant, fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    GlassPillButton(
                        onClick = onSave,
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Save Trip",
                            color = Primary,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
    }
}
