package com.example.nomadcompass.ui.screens.explore

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.nomadcompass.domain.model.Country
import com.example.nomadcompass.ui.components.ClayCard
import com.example.nomadcompass.ui.components.ClayPill
import com.example.nomadcompass.ui.components.NomadBottomNavigationBar
import com.example.nomadcompass.ui.components.NomadNavTab
import com.example.nomadcompass.ui.components.bounceClick
import com.example.nomadcompass.ui.components.bounceOnState
import com.example.nomadcompass.ui.components.clayShadow
import com.example.nomadcompass.ui.theme.Background
import com.example.nomadcompass.ui.theme.LocalThemeController
import com.example.nomadcompass.ui.theme.MossButtonBorder
import com.example.nomadcompass.ui.theme.MossButtonGradientEnd
import com.example.nomadcompass.ui.theme.MossButtonGradientStart
import com.example.nomadcompass.ui.theme.OnSurface
import com.example.nomadcompass.ui.theme.OnSurfaceVariant
import com.example.nomadcompass.ui.theme.Primary
import com.example.nomadcompass.ui.theme.SageCardDark
import com.example.nomadcompass.ui.theme.Secondary
import com.example.nomadcompass.ui.theme.SecondaryContainer
import java.io.File

@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel,
    onCountryClick: (cca3: String) -> Unit,
    onPlannerClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    val isDark = LocalThemeController.current.isDarkMode
    var searchVisible by remember { mutableStateOf(false) }
    var filterMenuExpanded by remember { mutableStateOf(false) }

    val pills = listOf("All", "My Favs", "Europe", "Asia", "Americas", "Africa", "Oceania", "Themes")

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF132A22).copy(alpha = 0.75f),
                                Color.Transparent
                            )
                        )
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Search Bar with Frosted Glass Look
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(9999.dp))
                        .background(Color.White.copy(alpha = 0.12f))
                        .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(9999.dp)),
                    contentAlignment = Alignment.CenterStart
                ) {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = viewModel::onSearchQueryChanged,
                        placeholder = {
                            Text(
                                "Search destinations...",
                                color = OnSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            if (uiState.searchQuery.isNotEmpty()) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = OnSurfaceVariant,
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clickable { viewModel.onSearchQueryChanged("") }
                                )
                            }
                        },
                        singleLine = true,
                        shape = CircleShape,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = OnSurface,
                            unfocusedTextColor = OnSurface,
                            cursorColor = Primary,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                        )
                    )
                }

                // Profile / Settings Button with Gear Icon
                val photoUri = uiState.userProfile?.photoUri
                val photoFile = if (!photoUri.isNullOrBlank()) File(photoUri) else null

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .bounceClick(onClick = onProfileClick)
                        .clayShadow(
                            cornerRadius = 9999.dp,
                            ambientShadowColor = Color.Black.copy(alpha = 0.35f),
                            spotShadowColor = Color.Black.copy(alpha = 0.45f),
                            blurRadius = 6.dp
                        )
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .border(1.2.dp, Color.White.copy(alpha = 0.32f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (photoFile != null && photoFile.exists()) {
                        AsyncImage(
                            model = photoFile,
                            contentDescription = "Profile & Settings",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = OnSurface,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            NomadBottomNavigationBar(
                currentTab = NomadNavTab.EXPLORE,
                onExploreClick = { },
                onPlannerClick = onPlannerClick
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Hero Title matching screenshot: "Where Will You Go Next?"
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Where Will\nYou Go Next?",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 38.sp
                        ),
                        color = OnSurface
                    )
                }
            }

            // Horizontal Pill Carousel
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(top = 8.dp, bottom = 20.dp)
                ) {
                    items(
                        items = pills,
                        key = { it },
                        contentType = { "pill" }
                    ) { pill ->
                        ClayPill(
                            text = pill,
                            isActive = pill == uiState.selectedPill,
                            onClick = { viewModel.onPillSelected(pill) }
                        )
                    }
                }
            }

            // Section Header: "You Might Also Like" + "See All"
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "You Might Also Like",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = OnSurface
                    )
                    Text(
                        text = "See All",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = OnSurfaceVariant,
                        modifier = Modifier
                            .clickable { viewModel.onPillSelected("All") }
                            .padding(4.dp)
                    )
                }
            }

            // Empty State View when filters/search return no items
            if (uiState.countries.isEmpty()) {
                item {
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
                            com.example.nomadcompass.ui.components.GlassPillButton(
                                onClick = { viewModel.resetFilters() }
                            ) {
                                Text(
                                    "Reset Filters",
                                    color = Primary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                // Stacked Sage Destination Cards (Bali aesthetic from reference image)
                items(
                    items = uiState.countries,
                    key = { it.cca3 },
                    contentType = { "country_card" }
                ) { country ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        SageDestinationCard(
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

/**
 * Sage Green Curved Destination Card matching the Bali card layout in the reference image.
 */
@Composable
private fun SageDestinationCard(
    country: Country,
    onCardClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
) {
    val isDark = LocalThemeController.current.isDarkMode
    val cardShape = RoundedCornerShape(28.dp)

    ClayCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 28.dp,
        backgroundColor = SageCardDark,
        onClick = onCardClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Row inside Card: Destination Name + Favorite Heart
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = country.commonName,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                // Favorite Heart Button with glass backdrop
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .bounceClick(scaleDown = 0.85f, onClick = onFavoriteToggle)
                        .bounceOnState(state = country.isFavorite, maxScale = 1.35f)
                        .clayShadow(
                            cornerRadius = 9999.dp,
                            ambientShadowColor = Color.Black.copy(alpha = 0.35f),
                            spotShadowColor = Color.Black.copy(alpha = 0.45f),
                            blurRadius = 6.dp
                        )
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = if (country.isFavorite) 0.35f else 0.18f))
                        .border(1.dp, Color.White.copy(alpha = 0.35f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (country.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (country.isFavorite) Color(0xFFFF5252) else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Destination Image Cutout with Organic Rounded Corners
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color.Black.copy(alpha = 0.20f))
            ) {
                AsyncImage(
                    model = country.flagUrl,
                    contentDescription = country.commonName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Frosted Glass Location Badge (Bottom Right matching "📍 Indonesia")
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .clayShadow(
                            cornerRadius = 9999.dp,
                            ambientShadowColor = Color.Black.copy(alpha = 0.40f),
                            spotShadowColor = Color.Black.copy(alpha = 0.50f),
                            blurRadius = 6.dp
                        )
                        .clip(RoundedCornerShape(9999.dp))
                        .background(Color.Black.copy(alpha = 0.45f))
                        .border(1.dp, Color.White.copy(alpha = 0.30f), RoundedCornerShape(9999.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFFFF6B6B),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = country.region,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Color.White
                        )
                    }
                }

                // Nomad Connectivity Tag (Top Left inside photo)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(9999.dp))
                        .background(Color.Black.copy(alpha = 0.45f))
                        .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(9999.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Wifi,
                            contentDescription = null,
                            tint = Secondary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Fast WiFi",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = Color.White
                        )
                    }
                }
            }

            // Dynamic Highlight Snippet if present
            if (!country.highlightSnippet.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = country.highlightSnippet,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Moss-Emerald Glass "View Details" Pill Button (Matching bottom button in screenshot)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .bounceClick(scaleDown = 0.96f, onClick = onCardClick)
                    .clayShadow(
                        cornerRadius = 9999.dp,
                        ambientShadowColor = Color.Black.copy(alpha = 0.40f),
                        spotShadowColor = Color.Black.copy(alpha = 0.55f),
                        blurRadius = 8.dp
                    )
                    .clip(RoundedCornerShape(9999.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MossButtonGradientStart,
                                MossButtonGradientEnd
                            )
                        ),
                        shape = RoundedCornerShape(9999.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = MossButtonBorder,
                        shape = RoundedCornerShape(9999.dp)
                    )
                    .drawBehind {
                        drawRect(
                            color = Color.White.copy(alpha = 0.35f),
                            size = size.copy(height = 1.5.dp.toPx())
                        )
                    }
                    .padding(vertical = 13.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "View Details",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color.White
                )
            }
        }
    }
}


