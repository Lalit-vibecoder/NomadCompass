package com.example.nomadcompass.ui.screens.explore

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.nomadcompass.domain.model.Country
import com.example.nomadcompass.ui.components.ClayCard
import com.example.nomadcompass.ui.components.ClayPill
import com.example.nomadcompass.ui.theme.Background
import com.example.nomadcompass.ui.theme.LocalThemeController
import com.example.nomadcompass.ui.theme.OnPrimary
import com.example.nomadcompass.ui.theme.OnSurface
import com.example.nomadcompass.ui.theme.OnSurfaceVariant
import com.example.nomadcompass.ui.theme.Secondary
import com.example.nomadcompass.ui.theme.SecondaryContainer
import com.example.nomadcompass.ui.theme.SurfaceContainerHigh
import com.example.nomadcompass.ui.theme.SurfaceContainerLow

import androidx.compose.foundation.layout.statusBarsPadding

@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel,
    onCountryClick: (cca3: String) -> Unit,
    onPlannerClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    val pills = listOf("All", "My Favs", "Europe", "Asia", "Americas", "Africa", "Oceania", "Themes")
    var filterMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            // Sticky Top Search Header with System Status Bar Padding
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceContainerHigh)
                    .border(1.dp, Color.White.copy(alpha = 0.05f))
                    .statusBarsPadding()
                    .padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = viewModel::onSearchQueryChanged,
                    placeholder = { Text("Search destinations...", color = OnSurfaceVariant) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(24.dp))
                    },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .clip(CircleShape)
                        .background(SurfaceContainerLow),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface,
                    )
                )

                // Dark / Light Mode Switch Button
                val themeController = LocalThemeController.current
                val isDark = themeController.isDarkMode

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(SurfaceContainerLow)
                        .border(1.dp, OnSurface.copy(alpha = 0.1f), CircleShape)
                        .clickable { themeController.toggleTheme() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isDark) Icons.Default.WbSunny else Icons.Default.DarkMode,
                        contentDescription = if (isDark) "Switch to Light Mode" else "Switch to Dark Mode",
                        tint = Secondary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Profile Avatar Section on Right
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(SecondaryContainer)
                        .border(1.dp, Secondary.copy(alpha = 0.3f), CircleShape)
                        .clickable(onClick = onProfileClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "Profile", tint = Secondary, modifier = Modifier.size(24.dp))
                }
            }
        },
        bottomBar = {
            // Bottom Navigation Bar (Explore & Planner)
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
                    isSelected = true,
                    onClick = { }
                )

                BottomNavItem(
                    icon = Icons.Default.EventNote,
                    label = "Planner",
                    isSelected = false,
                    onClick = onPlannerClick
                )
            }
        },
        containerColor = Background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Hero Title & Subtitle Section
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                val destinationText = if (uiState.countries.isNotEmpty()) "Explore ${uiState.countries.size} Destinations" else "Find your next base"
                Text(
                    text = destinationText,
                    style = MaterialTheme.typography.headlineLarge,
                    color = OnSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Discover top-rated destinations for digital nomads with real-time connectivity and safety scores.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
            }

            // Category Pill Bar
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                items(pills, key = { it }) { pill ->
                    ClayPill(
                        text = pill,
                        isActive = pill == uiState.selectedPill,
                        onClick = { viewModel.onPillSelected(pill) }
                    )
                }
            }

            if (uiState.countries.isEmpty()) {
                // Empty State View when filters/search return no items
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No destinations match your filter criteria",
                            style = MaterialTheme.typography.titleMedium,
                            color = OnSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Try clearing your search query or selecting a different category.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        com.example.nomadcompass.ui.components.ClayButton(
                            onClick = { viewModel.resetFilters() }
                        ) {
                            Text("Reset Filters", color = OnPrimary, modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }
            } else {
                // Country Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.countries, key = { it.cca3 }) { country ->
                        CountryCardItem(
                            country = country,
                            onCardClick = { onCountryClick(country.cca3) },
                            onFavoriteToggle = { viewModel.toggleFavorite(country.cca3) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CountryCardItem(
    country: Country,
    onCardClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
) {
    ClayCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
        cornerRadius = 24.dp,
        backgroundColor = SurfaceContainerHigh
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Card Flag / Image Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceContainerLow)
            ) {
                AsyncImage(
                    model = country.flagUrl,
                    contentDescription = country.commonName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Favorite Heart Button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.TopEnd)
                        .padding(top = 8.dp, end = 8.dp)
                        .clip(CircleShape)
                        .background(Background.copy(alpha = 0.6f))
                        .clickable(onClick = onFavoriteToggle),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (country.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (country.isFavorite) Color(0xFFFF5252) else OnSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Card Details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = country.commonName,
                        style = MaterialTheme.typography.titleMedium,
                        color = OnSurface
                    )
                    Text(text = country.flagEmoji, fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = null,
                        tint = Secondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${country.region} • Fast",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
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

