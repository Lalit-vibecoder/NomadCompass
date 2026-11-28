package com.example.nomadcompass.ui.screens.planner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.nomadcompass.ui.theme.icons.PhosphorIcons
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlin.math.roundToInt
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
import com.example.nomadcompass.ui.components.ClayButton
import com.example.nomadcompass.ui.components.ClayCard
import com.example.nomadcompass.ui.components.FrostedGlassDialog
import com.example.nomadcompass.ui.components.GlassPillButton
import com.example.nomadcompass.ui.components.NomadDatePickerDialog
import com.example.nomadcompass.ui.components.NomadDateRangePickerDialog
import com.example.nomadcompass.ui.components.NomadBottomNavigationBar
import com.example.nomadcompass.ui.components.NomadNavTab
import com.example.nomadcompass.ui.components.TripWorkspaceModal
import com.example.nomadcompass.ui.components.bounceClick
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.ui.draw.blur
import com.example.nomadcompass.ui.components.clayShadow
import com.example.nomadcompass.ui.theme.ActionPrimary
import com.example.nomadcompass.ui.theme.GlassCardBackground
import com.example.nomadcompass.ui.theme.GlassCardBorder
import com.example.nomadcompass.ui.theme.LocalThemeController
import com.example.nomadcompass.ui.theme.OnSurface
import com.example.nomadcompass.ui.theme.OnSurfaceVariant
import com.example.nomadcompass.ui.theme.Primary
import com.example.nomadcompass.ui.theme.Secondary
import com.example.nomadcompass.ui.theme.SurfaceContainer
import com.example.nomadcompass.util.CurrencyFormatter
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

    var editingTripDetails by remember { mutableStateOf<Trip?>(null) }
    val isModalOpen = uiState.activeWorkspaceTrip != null || uiState.isAddDialogOpen || editingTripDetails != null
    val backgroundBlur by animateDpAsState(
        targetValue = if (isModalOpen) 20.dp else 0.dp,
        label = "planner_bg_blur"
    )

    Scaffold(
        modifier = if (backgroundBlur > 0.dp) Modifier.blur(backgroundBlur) else Modifier,
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
                            imageVector = PhosphorIcons.Notebook,
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
                            imageVector = PhosphorIcons.UserCircle,
                            contentDescription = "Profile",
                            tint = OnSurface,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            // Signature Cyan-to-Emerald Wide Pill Sticky Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .navigationBarsPadding()
                    .padding(bottom = 76.dp)
                    .height(52.dp)
                    .bounceClick(scaleDown = 0.97f, onClick = { viewModel.openAddDialog() })
                    .clayShadow(
                        cornerRadius = 9999.dp,
                        ambientShadowColor = Color(0xFF06B6D4).copy(alpha = 0.35f),
                        spotShadowColor = Color(0xFF10B981).copy(alpha = 0.45f),
                        blurRadius = 12.dp
                    )
                    .clip(RoundedCornerShape(9999.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF06B6D4), // Cyan
                                Color(0xFF10B981)  // Emerald
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(9999.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = PhosphorIcons.Plus,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Plan New Trip",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
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
                        text = "Organize upcoming destinations, travel budgets, and offline itineraries in one place.",
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
                    if (uiState.trips.isNotEmpty()) {
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
            }

            if (uiState.trips.isEmpty()) {
                item(key = "empty_trips") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 20.dp)
                            .clayShadow(
                                cornerRadius = 20.dp,
                                ambientShadowColor = Color.Black.copy(alpha = 0.35f),
                                spotShadowColor = Color.Black.copy(alpha = 0.45f),
                                blurRadius = 16.dp
                            )
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF0B121D).copy(alpha = 0.85f))
                            .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.10f),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 24.dp, vertical = 36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(Primary.copy(alpha = 0.15f))
                                    .border(1.dp, Primary.copy(alpha = 0.35f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = PhosphorIcons.AirplaneTilt,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
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
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp
                                ),
                                color = OnSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
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
                            onClick = { viewModel.openTripWorkspace(trip) },
                            onEditTrip = { editingTripDetails = trip }
                        )
                    }
                }
            }
        }
    }

    // Edit Trip Details Dialog Modal (Dates & Budget)
    if (editingTripDetails != null) {
        EditTripDetailsDialog(
            trip = editingTripDetails!!,
            currencyCode = uiState.userCurrencyCode,
            onDismiss = { editingTripDetails = null },
            onSave = { startDate, endDate, budgetUsd ->
                viewModel.updateTripDetails(editingTripDetails!!, startDate, endDate, budgetUsd)
                editingTripDetails = null
            }
        )
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
            onEditTrip = { editingTripDetails = uiState.activeWorkspaceTrip },
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

private fun getStatusInfo(status: String): Quadruple<String, Color, Color, Color> {
    val lower = status.lowercase().trim()
    return when {
        lower.contains("completed") || lower.contains("ready") -> Quadruple(
            "Completed",
            Color(0xFF81C784),
            Color(0xFF1B5E20).copy(alpha = 0.55f),
            Color(0xFF81C784).copy(alpha = 0.60f)
        )
        lower.contains("progress") -> Quadruple(
            "In Progress",
            Color(0xFFFFB74D),
            Color(0xFFE65100).copy(alpha = 0.55f),
            Color(0xFFFFB74D).copy(alpha = 0.60f)
        )
        else -> Quadruple(
            "Planned",
            Color(0xFF90CAF9),
            Color(0xFF0D47A1).copy(alpha = 0.55f),
            Color(0xFF90CAF9).copy(alpha = 0.60f)
        )
    }
}

@Composable
private fun SageTripCard(
    trip: Trip,
    currencyCode: String,
    onClick: () -> Unit,
    onEditTrip: () -> Unit,
) {
    val isDark = LocalThemeController.current.isDarkMode
    val cardShape = remember { RoundedCornerShape(24.dp) }

    val sageGradientStart = if (isDark) Color(0xFF537E76) else Color(0xFF7AA59D)
    val sageGradientEnd = if (isDark) Color(0xFF385E56) else Color(0xFF59857D)

    val cardBgBrush = remember {
        Brush.verticalGradient(
            colors = listOf(
                GlassCardBackground,
                GlassCardBackground
            )
        )
    }
    val cardSheenBrush = remember {
        Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.08f),
                Color.White.copy(alpha = 0.02f),
                Color.Transparent
            )
        )
    }
    val cardBorderBrush = remember {
        Brush.verticalGradient(
            colors = listOf(
                GlassCardBorder,
                GlassCardBorder
            )
        )
    }
    val specularBrush = remember {
        Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,
                Color.White.copy(alpha = 0.25f),
                Color.Transparent
            )
        )
    }

    // High-contrast vibrant status styling for dark/sage card surfaces
    val (statusLabel, statusTextColor, statusBg, statusBorder) = getStatusInfo(trip.status)

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
                    // Small country flag in place of first country code
                    Box(
                        modifier = Modifier
                            .size(32.dp)
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
                        } else if (trip.flagEmoji.isNotBlank() && trip.flagEmoji != "✈️") {
                            Text(text = trip.flagEmoji, fontSize = 16.sp)
                        } else {
                            Icon(
                                imageVector = PhosphorIcons.AirplaneTilt,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    val standardizedCountryName = remember(trip.countryName, trip.destinationCca3) {
                        trip.countryName.ifBlank { trip.destinationCca3.uppercase() }.trim()
                    }

                    Text(
                        text = standardizedCountryName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
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
                // Dates Pill (Editable)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(Color.Black.copy(alpha = if (isDark) 0.30f else 0.18f))
                        .border(1.dp, GlassCardBorder, RoundedCornerShape(9999.dp))
                        .clickable(onClick = onEditTrip)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = PhosphorIcons.CalendarBlank,
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
                        Icon(
                            imageVector = PhosphorIcons.PencilSimple,
                            contentDescription = "Edit Dates",
                            tint = Color.White.copy(alpha = 0.65f),
                            modifier = Modifier.size(11.dp)
                        )
                    }
                }

                // Budget Pill (Editable)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(Color.Black.copy(alpha = if (isDark) 0.30f else 0.18f))
                        .border(1.dp, GlassCardBorder, RoundedCornerShape(9999.dp))
                        .clickable(onClick = onEditTrip)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val sym = CurrencyFormatter.getSymbol(currencyCode).trim()
                        Text(
                            text = sym,
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
                        Icon(
                            imageVector = PhosphorIcons.PencilSimple,
                            contentDescription = "Edit Budget",
                            tint = Color.White.copy(alpha = 0.65f),
                            modifier = Modifier.size(11.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EditTripDetailsDialog(
    trip: Trip,
    currencyCode: String,
    onDismiss: () -> Unit,
    onSave: (startDate: String, endDate: String, budgetUsd: Double) -> Unit,
) {
    var startDate by remember { mutableStateOf(trip.startDate) }
    var endDate by remember { mutableStateOf(trip.endDate) }
    var budgetText by remember { mutableStateOf(trip.budgetUsd.toString().removeSuffix(".0")) }
    var showDateRangePicker by remember { mutableStateOf(false) }

    if (showDateRangePicker) {
        NomadDateRangePickerDialog(
            initialStartDate = startDate,
            initialEndDate = endDate,
            title = "Select Trip Dates",
            onDatesSelected = { start, end ->
                startDate = start
                endDate = end
            },
            onDismiss = { showDateRangePicker = false }
        )
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
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
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
                            imageVector = PhosphorIcons.PencilSimple,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Edit Trip Details",
                            style = MaterialTheme.typography.titleLarge,
                            color = OnSurface,
                            fontWeight = FontWeight.Bold
                        )
                        val standardizedCountryName = remember(trip.countryName, trip.destinationCca3) {
                            trip.countryName.ifBlank { trip.destinationCca3.uppercase() }.trim()
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(9999.dp))
                                .background(SurfaceContainerHigh.copy(alpha = 0.85f))
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), RoundedCornerShape(9999.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (trip.flagUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = trip.flagUrl,
                                        contentDescription = standardizedCountryName,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                    )
                                } else if (trip.flagEmoji.isNotBlank() && trip.flagEmoji != "✈️") {
                                    Text(text = trip.flagEmoji, fontSize = 13.sp)
                                } else {
                                    Icon(
                                        imageVector = PhosphorIcons.AirplaneTilt,
                                        contentDescription = null,
                                        tint = Primary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Text(
                                    text = standardizedCountryName,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp
                                    ),
                                    color = OnSurface
                                )
                            }
                        }
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
                        imageVector = PhosphorIcons.X,
                        contentDescription = "Close",
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                thickness = 0.8.dp
            )

            // Single Date Range Pill ("Select Date")
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "SELECT DATE",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant,
                    letterSpacing = 1.sp
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceContainerLow.copy(alpha = 0.85f))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                        .clickable { showDateRangePicker = true }
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = PhosphorIcons.CalendarBlank,
                                contentDescription = "Select Dates",
                                tint = Primary,
                                modifier = Modifier.size(20.dp)
                            )
                            val dateDisplay = if (startDate.isNotBlank() && endDate.isNotBlank()) {
                                "$startDate  →  $endDate"
                            } else if (startDate.isNotBlank()) {
                                startDate
                            } else {
                                "Select trip dates"
                            }
                            Text(
                                text = dateDisplay,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (startDate.isNotBlank()) OnSurface else OnSurfaceVariant.copy(alpha = 0.5f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Icon(
                            imageVector = PhosphorIcons.CaretDown,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Monthly Budget Field
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "MONTHLY BUDGET",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant,
                    letterSpacing = 1.sp
                )
                OutlinedTextField(
                    value = budgetText,
                    onValueChange = { budgetText = it },
                    placeholder = { Text("e.g. 2500", color = OnSurfaceVariant.copy(alpha = 0.5f)) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    leadingIcon = {
                        Text(
                            text = currencyCode,
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

            Spacer(modifier = Modifier.height(6.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Cancel", color = OnSurfaceVariant)
                }

                ClayButton(
                    onClick = {
                        val budgetVal = budgetText.toDoubleOrNull() ?: trip.budgetUsd
                        onSave(startDate.trim(), endDate.trim(), budgetVal)
                    },
                    containerColor = ActionPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Icon(
                        imageVector = PhosphorIcons.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Save",
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
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

    var showDateRangePicker by remember { mutableStateOf(false) }

    if (showDateRangePicker) {
        NomadDateRangePickerDialog(
            initialStartDate = uiState.startDate,
            initialEndDate = uiState.endDate,
            title = "Select Trip Dates",
            onDatesSelected = { start, end ->
                onStartDateChanged(start)
                onEndDateChanged(end)
            },
            onDismiss = { showDateRangePicker = false }
        )
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
                                imageVector = PhosphorIcons.GlobeSimple,
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
                            imageVector = PhosphorIcons.X,
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
                            selectedCountry?.commonName ?: ""
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
                                    imageVector = PhosphorIcons.GlobeSimple,
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
                                        imageVector = PhosphorIcons.XCircle,
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
                                        imageVector = if (isDestinationEditing) PhosphorIcons.MagnifyingGlass else PhosphorIcons.CaretDown,
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
                                        imageVector = PhosphorIcons.MagnifyingGlass,
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

                // Single Date Range Pill ("Select Date")
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "SELECT DATE",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant,
                        letterSpacing = 1.sp
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(SurfaceContainerLow.copy(alpha = 0.85f))
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                            .clickable { showDateRangePicker = true }
                            .padding(horizontal = 14.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = PhosphorIcons.CalendarBlank,
                                    contentDescription = "Select Dates",
                                    tint = Primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                val dateDisplay = if (uiState.startDate.isNotBlank() && uiState.endDate.isNotBlank()) {
                                    val durationSuffix = try {
                                        val s = java.time.LocalDate.parse(uiState.startDate.trim())
                                        val e = java.time.LocalDate.parse(uiState.endDate.trim())
                                        val days = java.time.temporal.ChronoUnit.DAYS.between(s, e) + 1
                                        " • $days ${if (days == 1L) "Day" else "Days"}"
                                    } catch (_: Exception) {
                                        ""
                                    }
                                    "${uiState.startDate}  →  ${uiState.endDate}$durationSuffix"
                                } else if (uiState.startDate.isNotBlank()) {
                                    uiState.startDate
                                } else {
                                    "Select trip dates"
                                }
                                Text(
                                    text = dateDisplay,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (uiState.startDate.isNotBlank()) OnSurface else OnSurfaceVariant.copy(alpha = 0.5f),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Icon(
                                imageVector = PhosphorIcons.CaretDown,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
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

                // Travelling For Dropdown Field
                val travelReasons = remember {
                    listOf(
                        "🏖️ Vacation & Tourism",
                        "💻 Digital Nomad / Remote Work",
                        "🏡 Workation (Work + Leisure)",
                        "💼 Business & Networking",
                        "🎒 Solo Backpacking & Exploration",
                        "📍 Relocation & Scouting",
                        "🎓 Study & Research",
                        "🎪 Conference & Events"
                    )
                }
                var isTravellingForExpanded by remember { mutableStateOf(false) }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "TRAVELLING FOR",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant,
                        letterSpacing = 1.sp
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(SurfaceContainerLow.copy(alpha = 0.85f))
                            .border(
                                width = 1.dp,
                                color = if (isTravellingForExpanded) Primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { isTravellingForExpanded = !isTravellingForExpanded }
                            .padding(horizontal = 14.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = uiState.notes.ifBlank { "🏖️ Vacation & Tourism" },
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = if (isTravellingForExpanded) PhosphorIcons.CaretUp else PhosphorIcons.CaretDown,
                                contentDescription = "Expand travelling options",
                                tint = Primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = isTravellingForExpanded,
                            onDismissRequest = { isTravellingForExpanded = false },
                            modifier = Modifier
                                .fillMaxWidth(0.82f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(SurfaceContainerHigh)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        ) {
                            travelReasons.forEach { reason ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = reason,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (uiState.notes == reason) Primary else OnSurface,
                                            fontWeight = if (uiState.notes == reason) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        onNotesChanged(reason)
                                        isTravellingForExpanded = false
                                    }
                                )
                            }
                        }
                    }
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
                    ClayButton(
                        onClick = onSave,
                        containerColor = ActionPrimary,
                        contentColor = Color.White,
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Icon(
                            imageVector = PhosphorIcons.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Save Trip",
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
    }
}
