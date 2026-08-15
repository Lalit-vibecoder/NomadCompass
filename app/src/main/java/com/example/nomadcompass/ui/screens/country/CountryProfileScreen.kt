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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.nomadcompass.ui.components.ClayCard
import com.example.nomadcompass.ui.components.CurrencyConverterModal
import java.util.Locale
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
fun CountryProfileScreen(
    viewModel: CountryProfileViewModel,
    onNeighborClick: (cca3: String) -> Unit,
    onExploreClick: () -> Unit = {},
    onPlannerClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onAddTripClick: (cca3: String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            // Header Top Navigation with System Status Bar Padding
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Background.copy(alpha = 0.8f))
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clip(CircleShape)
                        .background(SurfaceContainerLow)
                        .border(1.dp, Color.White.copy(alpha = 0.05f), CircleShape)
                        .clickable(onClick = onExploreClick)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Search destinations...", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                    }
                }

                // Profile Avatar on Right
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SecondaryContainer)
                        .border(1.dp, Secondary.copy(alpha = 0.3f), CircleShape)
                        .clickable(onClick = onProfileClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "Profile", tint = Secondary, modifier = Modifier.size(20.dp))
                }
            }
        },
        bottomBar = {
            // Bottom Navigation Bar
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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable(onClick = onExploreClick)
                ) {
                    Icon(imageVector = Icons.Default.Explore, contentDescription = null, tint = Primary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = "Explore", style = MaterialTheme.typography.labelSmall, color = Primary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable(onClick = onPlannerClick)
                ) {
                    Icon(imageVector = Icons.Default.EventNote, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = "Planner", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant, fontSize = 11.sp)
                }
            }
        },
        floatingActionButton = {
            // Floating Action Button - Clay Pill Style
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(PrimaryContainer)
                    .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                    .clickable {
                        val currentCca3 = uiState.detail?.country?.cca3
                        if (currentCca3 != null) {
                            onAddTripClick(currentCca3)
                        } else {
                            onPlannerClick()
                        }
                    }
                    .padding(horizontal = 24.dp, vertical = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = OnPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "ADD TRIP", style = MaterialTheme.typography.labelLarge, color = OnPrimary, fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = Background
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
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
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

                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Background),
                                    startY = 100f
                                )
                            )
                    )

                    // Country title & safety badge
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(24.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = country.commonName,
                                style = MaterialTheme.typography.displayLarge,
                                color = Primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            // Safety Shield Badge
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Safe",
                                    tint = Background,
                                    modifier = Modifier.size(20.dp)
                                )
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
                                    color = Primary
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // 10-Segment Clay Bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                for (i in 1..10) {
                                    val isActive = i <= detail.nci.score
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(9999.dp))
                                            .background(if (isActive) Primary else Color(0xFF0E0E12))
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
                                        Icon(imageVector = Icons.Default.WbSunny, contentDescription = null, tint = Primary)
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
                                        color = OnSurface
                                    )
                                    Text(
                                        text = subTemp,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = OnSurfaceVariant
                                    )
                                }
                            }

                            // Currency Card
                            val formattedRate = if (uiState.exchangeRate >= 100) {
                                String.format(Locale.US, "%.0f", uiState.exchangeRate)
                            } else {
                                String.format(Locale.US, "%.2f", uiState.exchangeRate)
                            }
                            ClayCard(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.openConverter() },
                                cornerRadius = 24.dp,
                                backgroundColor = SurfaceContainer
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Icon(imageVector = Icons.Default.CurrencyExchange, contentDescription = null, tint = Primary)
                                        Text(text = "CURRENCY", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "1 ${uiState.baseCurrencyCode}", style = MaterialTheme.typography.titleMedium, color = OnSurface)
                                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(14.dp))
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
                                            .clickable { viewModel.openConverter() }
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
                            val tzInfo = com.example.nomadcompass.util.TimezoneHelper.getTimezoneInfo(
                                cca3 = country.cca3,
                                cca2 = country.cca2,
                                longitude = country.longitude
                            )

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
                                        Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = Primary)
                                        Text(text = "TIMEZONE", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                    }
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Text(text = tzInfo.utcDisplay, style = MaterialTheme.typography.headlineSmall, color = OnSurface)
                                    Text(text = tzInfo.localTimeDisplay, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                                }
                            }

                            // Dynamic Next Holiday Card
                            val todayStr = try {
                                java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US).format(java.util.Date())
                            } catch (_: Exception) {
                                "2026-01-01"
                            }

                            val upcomingHolidays = detail.holidays.filter { it.date >= todayStr }
                            val nextHoliday = upcomingHolidays.firstOrNull() ?: detail.holidays.lastOrNull() ?: detail.holidays.firstOrNull()

                            val holidayName = nextHoliday?.name?.ifBlank { nextHoliday.localName } ?: "No Upcoming Holidays"
                            val holidayDateFormatted = if (nextHoliday != null && nextHoliday.date.isNotBlank()) {
                                try {
                                    val inFormat = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US)
                                    val outFormat = java.text.SimpleDateFormat("MMM dd, yyyy", Locale.US)
                                    val dateObj = inFormat.parse(nextHoliday.date)
                                    if (dateObj != null) outFormat.format(dateObj) else nextHoliday.date
                                } catch (_: Exception) {
                                    nextHoliday.date
                                }
                            } else {
                                "None"
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
                                        Icon(imageVector = Icons.Default.Event, contentDescription = null, tint = Primary)
                                        Text(text = "NEXT HOLIDAY", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                    }
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Text(
                                        text = holidayName,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = OnSurface,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = holidayDateFormatted,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = OnSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Neighboring Destinations Carousel
                    if (detail.neighbors.isNotEmpty()) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Nearby Destinations",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = OnSurface
                                )
                                Text(
                                    text = "VIEW ALL",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Primary
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(detail.neighbors, key = { it.cca3 }) { neighbor ->
                                    ClayCard(
                                        modifier = Modifier
                                            .width(200.dp)
                                            .height(140.dp)
                                            .clickable { onNeighborClick(neighbor.cca3) },
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
                                                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
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
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = Color.White
                                                )
                                                Text(
                                                    text = neighbor.region,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = Color.White.copy(alpha = 0.8f)
                                                )
                                            }
                                        }
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
