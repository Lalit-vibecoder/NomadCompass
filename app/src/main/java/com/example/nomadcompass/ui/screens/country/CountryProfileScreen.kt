package com.example.nomadcompass.ui.screens.country

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import com.example.nomadcompass.ui.theme.icons.PhosphorIcons
import androidx.compose.material3.CircularProgressIndicator
import java.io.File
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.nomadcompass.ui.components.ClayCard
import com.example.nomadcompass.ui.components.ClayPill
import com.example.nomadcompass.ui.components.GlassPillButton
import com.example.nomadcompass.ui.components.clayShadow
import com.example.nomadcompass.ui.components.CurrencyConverterModal
import com.example.nomadcompass.ui.components.NomadBottomNavigationBar
import com.example.nomadcompass.ui.components.NomadNavTab
import java.util.Locale
import com.example.nomadcompass.ui.theme.Background
import com.example.nomadcompass.ui.theme.LocalThemeController
import com.example.nomadcompass.ui.theme.OnPrimary
import com.example.nomadcompass.ui.theme.OnPrimaryContainer
import com.example.nomadcompass.ui.theme.OnSurface
import com.example.nomadcompass.ui.theme.OnSurfaceVariant
import com.example.nomadcompass.ui.theme.Primary
import com.example.nomadcompass.ui.theme.PrimaryContainer
import com.example.nomadcompass.ui.theme.Secondary
import com.example.nomadcompass.ui.theme.SecondaryContainer
import com.example.nomadcompass.ui.theme.SurfaceContainer
import com.example.nomadcompass.ui.theme.SurfaceContainerHigh
import com.example.nomadcompass.ui.theme.SurfaceContainerLow
import com.example.nomadcompass.ui.theme.SafetyGreen
import com.example.nomadcompass.ui.theme.SafetyYellow
import com.example.nomadcompass.ui.theme.SafetyRed

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.ui.draw.blur
import com.example.nomadcompass.ui.components.bounceClick
import com.example.nomadcompass.ui.components.bounceOnState

@Composable
fun CountryProfileScreen(
    viewModel: CountryProfileViewModel,
    onNeighborClick: (cca3: String) -> Unit,
    onExploreClick: () -> Unit = {},
    onPlannerClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onAddTripClick: (cca3: String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()

    val converterBlur by animateDpAsState(
        targetValue = if (uiState.isConverterOpen) 20.dp else 0.dp,
        label = "converter_bg_blur"
    )

    Scaffold(
        modifier = Modifier.blur(converterBlur),
        topBar = {
            val isDark = LocalThemeController.current.isDarkMode
            // Header Top Navigation with System Status Bar Padding & Subtle Frosted Glass Look
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                SurfaceContainerHigh.copy(alpha = if (isDark) 0.65f else 0.80f),
                                SurfaceContainerHigh.copy(alpha = if (isDark) 0.45f else 0.60f)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = if (isDark) 0.20f else 0.35f),
                                Color.White.copy(alpha = 0.05f)
                            )
                        ),
                        shape = RectangleShape
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = if (isDark) 0.12f else 0.20f))
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = if (isDark) 0.25f else 0.40f),
                            shape = CircleShape
                        )
                        .bounceClick(scaleDown = 0.97f, onClick = onExploreClick)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = PhosphorIcons.MagnifyingGlass, contentDescription = null, tint = Primary, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Search destinations...", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                    }
                }

                // Profile Avatar on Right
                val photoUri = uiState.userProfile?.photoUri
                val photoFile = remember(photoUri) {
                    if (!photoUri.isNullOrBlank()) File(photoUri).takeIf { it.exists() } else null
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(SecondaryContainer)
                        .border(1.5.dp, if (photoFile != null) Primary else Secondary.copy(alpha = 0.4f), CircleShape)
                        .bounceClick(onClick = onProfileClick),
                    contentAlignment = Alignment.Center
                ) {
                    if (photoFile != null) {
                        AsyncImage(
                            model = photoFile,
                            contentDescription = "Profile",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(imageVector = PhosphorIcons.GearSix, contentDescription = "Settings", tint = OnSurface, modifier = Modifier.size(22.dp))
                    }
                }
            }
        },
        floatingActionButton = {
            val isDark = LocalThemeController.current.isDarkMode
            // Floating Action Button - Glass Pill elevated above bottom navigation bar
            GlassPillButton(
                onClick = {
                    val currentCca3 = uiState.detail?.country?.cca3
                    if (currentCca3 != null) {
                        onAddTripClick(currentCca3)
                    } else {
                        onPlannerClick()
                    }
                },
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(bottom = 88.dp),
                contentPadding = PaddingValues(horizontal = 22.dp, vertical = 12.dp)
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
                        imageVector = PhosphorIcons.Plus,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "PLAN TRIP",
                    style = MaterialTheme.typography.labelLarge,
                    color = Primary,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        if (uiState.isLoading || uiState.detail == null) {
            com.example.nomadcompass.ui.components.NomadLoadingAnimation(
                modifier = Modifier.padding(paddingValues),
                message = "Calibrating Nomad Insights..."
            )
        } else {
            val detail = uiState.detail!!
            val country = detail.country

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding())
                    .clipToBounds()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 110.dp)
            ) {
                // Hero Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp)
                ) {
                    AsyncImage(
                        model = country.flagUrl,
                        contentDescription = country.commonName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Multi-layer Gradient overlay for depth & readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Black.copy(alpha = 0.2f), Color.Transparent, Background),
                                    startY = 0f
                                )
                            )
                    )

                    // Country title & dynamic safety badge
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(24.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = country.commonName,
                                style = MaterialTheme.typography.displayLarge,
                                color = OnSurface
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            // Contextual Safety Level Badge
                            val advisoryScore = detail.advisory?.score ?: 1.0
                            val (shieldColor, shieldText) = when {
                                advisoryScore < 2.5 -> SafetyGreen to "SAFE"
                                advisoryScore < 4.5 -> SafetyYellow to "CAUTION"
                                else -> SafetyRed to "WARNING"
                            }
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(shieldColor.copy(alpha = 0.2f))
                                    .border(1.dp, shieldColor, CircleShape)
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = PhosphorIcons.SealCheck,
                                        contentDescription = shieldText,
                                        tint = shieldColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = shieldText,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = shieldColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${country.officialName} • ${country.subregion}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = OnSurfaceVariant
                        )
                    }
                }

                // Content Section
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // NCI Score Section
                    ClayCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 24.dp,
                        backgroundColor = SurfaceContainer
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Nomad Comfort Index",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = OnSurface
                                )
                                Text(
                                    text = "${detail.nci.score}/10",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // 10-Segment Aurora Gradient Clay Bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                for (i in 1..10) {
                                    val isActive = i <= detail.nci.score
                                    val targetColor = if (isActive) {
                                        if (i <= 4) Primary else if (i <= 7) Secondary else com.example.nomadcompass.ui.theme.AccentAmber
                                    } else {
                                        SurfaceContainerHigh
                                    }
                                    val barColor by androidx.compose.animation.animateColorAsState(
                                        targetValue = targetColor,
                                        animationSpec = androidx.compose.animation.core.tween(300),
                                        label = "nci_bar_$i"
                                    )
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(9999.dp))
                                            .background(barColor)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "\"${detail.nci.summaryQuote}\"",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurfaceVariant,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }

                    // Bento Grid Cards (Weather, Currency, Timezone, Next Holiday)
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            // Weather Card
                            ClayCard(
                                modifier = Modifier.weight(1f),
                                cornerRadius = 24.dp,
                                backgroundColor = SurfaceContainer
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Icon(imageVector = PhosphorIcons.SunDim, contentDescription = null, tint = Primary)
                                        Text(text = "WEATHER", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                    }
                                    Spacer(modifier = Modifier.height(24.dp))
                                    val tempC = detail.weather?.temperatureCelsius?.toInt() ?: 22
                                    val tempF = detail.weather?.temperatureFahrenheit?.toInt() ?: 72
                                    val desc = detail.weather?.weatherDescription ?: "Sunny"
                                    val isFahrenheit = uiState.tempUnit.equals("F", ignoreCase = true)
                                    val headlineTemp = if (isFahrenheit) "${tempF}°F" else "${tempC}°C"
                                    val subTemp = if (isFahrenheit) "$desc • ${tempC}°C" else "$desc • ${tempF}°F"

                                    Text(
                                        text = headlineTemp,
                                        style = MaterialTheme.typography.headlineLarge,
                                        color = OnSurface,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = subTemp, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                                }
                            }

                            // Currency Card
                            val formattedRate = if (uiState.exchangeRate >= 100) {
                                String.format(Locale.US, "%.0f", uiState.exchangeRate)
                            } else {
                                String.format(Locale.US, "%.2f", uiState.exchangeRate)
                            }
                            ClayCard(
                                modifier = Modifier.weight(1f),
                                onClick = { viewModel.openConverter() },
                                cornerRadius = 24.dp,
                                backgroundColor = SurfaceContainer
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Icon(imageVector = PhosphorIcons.CurrencyCircleDollar, contentDescription = null, tint = Primary)
                                        Text(text = "CURRENCY", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "1 ${uiState.baseCurrencyCode}", style = MaterialTheme.typography.titleMedium, color = OnSurface)
                                        Icon(imageVector = PhosphorIcons.ArrowRight, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "$formattedRate ${country.currencyCode}",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = Primary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(CircleShape)
                                            .background(SecondaryContainer)
                                            .bounceClick(scaleDown = 0.94f) { viewModel.openConverter() }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "CONVERTER", style = MaterialTheme.typography.labelSmall, color = OnSurface)
                                    }
                                }
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            // Dynamic Timezone Card using TimezoneHelper
                            val tzInfo = remember(country.cca3) {
                                com.example.nomadcompass.util.TimezoneHelper.getTimezoneInfo(
                                    cca3 = country.cca3,
                                    cca2 = country.cca2,
                                    longitude = country.longitude
                                )
                            }

                            ClayCard(
                                modifier = Modifier.weight(1f),
                                cornerRadius = 24.dp,
                                backgroundColor = SurfaceContainer
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Icon(imageVector = PhosphorIcons.Clock, contentDescription = null, tint = Primary)
                                        Text(text = "TIMEZONE", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                    }
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Text(text = tzInfo.utcDisplay, style = MaterialTheme.typography.headlineSmall, color = OnSurface)
                                    Text(text = tzInfo.localTimeDisplay, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                                }
                            }

                            // Dynamic Next Holiday Card
                            val (holidayName, holidayDateFormatted) = remember(detail.holidays) {
                                val todayStr = try {
                                    java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US).format(java.util.Date())
                                } catch (_: Exception) {
                                    "2026-01-01"
                                }

                                val upcomingHolidays = detail.holidays.filter { it.date >= todayStr }
                                val nextHoliday = upcomingHolidays.firstOrNull() ?: detail.holidays.lastOrNull() ?: detail.holidays.firstOrNull()

                                val name = nextHoliday?.name?.ifBlank { nextHoliday.localName } ?: "No Upcoming Holidays"
                                val dateFormatted = if (nextHoliday != null && nextHoliday.date.isNotBlank()) {
                                    try {
                                        val inFormat = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US)
                                        val outFormat = java.text.SimpleDateFormat("MMM dd, yyyy", Locale.US)
                                        val dateObj = inFormat.parse(nextHoliday.date)
                                        if (dateObj != null) outFormat.format(dateObj) else nextHoliday.date
                                    } catch (_: Exception) {
                                        nextHoliday.date
                                    }
                                } else {
                                    "Check Local Listings"
                                }
                                Pair(name, dateFormatted)
                            }

                            ClayCard(
                                modifier = Modifier.weight(1f),
                                cornerRadius = 24.dp,
                                backgroundColor = SurfaceContainer
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Icon(imageVector = PhosphorIcons.CalendarCheck, contentDescription = null, tint = Primary)
                                        Text(text = "NEXT HOLIDAY", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                    }
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Text(
                                        text = holidayName,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = OnSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = holidayDateFormatted,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = OnSurfaceVariant
                                    )
                                }
                            }
                        }

                        // Destination Highlights & Occasions Card
                        val highlights = detail.highlights
                        if (highlights != null && (highlights.mediaItems.isNotEmpty() || highlights.topPlaces.isNotEmpty() || highlights.famousFestivals.isNotEmpty() || highlights.attractiveFeatures.isNotEmpty())) {
                            ClayCard(
                                modifier = Modifier.fillMaxWidth(),
                                cornerRadius = 24.dp,
                                backgroundColor = SurfaceContainer
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Icon(imageVector = PhosphorIcons.Compass, contentDescription = null, tint = Primary)
                                        Text(text = "DESTINATION HIGHLIGHTS & OCCASIONS", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))

                                    if (highlights.topPlaces.isNotEmpty()) {
                                        Text(text = "📍 Top Places", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            items(highlights.topPlaces, key = { it }) { place ->
                                                ClayPill(text = place, isActive = false, onClick = {})
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(12.dp))
                                    }

                                    if (highlights.famousFestivals.isNotEmpty()) {
                                        Text(text = "✨ Famous Festivals", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            items(highlights.famousFestivals, key = { it }) { festival ->
                                                ClayPill(text = festival, isActive = true, onClick = {})
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(12.dp))
                                    }

                                    if (highlights.attractiveFeatures.isNotEmpty()) {
                                        Text(text = "🌟 Key Attractions", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            items(highlights.attractiveFeatures, key = { it }) { feature ->
                                                ClayPill(text = feature, isActive = false, onClick = {})
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Neighboring Destinations (2 cards per row)
                    if (detail.neighbors.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Text(
                                text = "Nearby Destinations",
                                style = MaterialTheme.typography.headlineSmall,
                                color = OnSurface
                            )

                            val neighborRows = remember(detail.neighbors) {
                                detail.neighbors.chunked(2)
                            }

                            for (rowNeighbors in neighborRows) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    for (neighbor in rowNeighbors) {
                                        ClayCard(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(140.dp),
                                            onClick = { onNeighborClick(neighbor.cca3) },
                                            cornerRadius = 20.dp,
                                            backgroundColor = SurfaceContainer
                                        ) {
                                            Box(modifier = Modifier.fillMaxSize()) {
                                                AsyncImage(
                                                    model = neighbor.flagUrl,
                                                    contentDescription = neighbor.commonName,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .background(
                                                            Brush.verticalGradient(
                                                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                                                            )
                                                        )
                                                )
                                                Column(
                                                    modifier = Modifier
                                                        .align(Alignment.BottomStart)
                                                        .padding(12.dp)
                                                ) {
                                                    Text(
                                                        text = "${neighbor.flagEmoji} ${neighbor.commonName}",
                                                        style = MaterialTheme.typography.titleMedium.copy(
                                                            fontSize = 15.sp,
                                                            fontWeight = FontWeight.Bold
                                                        ),
                                                        color = Color.White,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    Text(
                                                        text = neighbor.region,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = Color.White.copy(alpha = 0.85f)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    if (rowNeighbors.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }

    if (uiState.isConverterOpen && uiState.detail != null) {
        CurrencyConverterModal(
            baseCurrency = uiState.baseCurrencyCode,
            targetCurrency = uiState.detail!!.country.currencyCode,
            exchangeRate = uiState.exchangeRate,
            inputAmount = uiState.inputAmount,
            convertedAmount = uiState.convertedCurrencyAmount,
            isSwapped = uiState.isSwapped,
            isRefreshing = uiState.isRefreshingCurrency,
            refreshMessage = uiState.currencyRefreshMessage,
            onRefreshRate = { viewModel.refreshExchangeRate() },
            onInputAmountChanged = { viewModel.onInputAmountChanged(it) },
            onToggleSwap = { viewModel.toggleSwapCurrencies() },
            onDismiss = { viewModel.closeConverter() }
        )
    }
}
