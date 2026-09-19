package com.example.nomadcompass.ui.screens.explore

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.nomadcompass.ui.theme.icons.PhosphorIcons
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.nomadcompass.domain.model.Country
import com.example.nomadcompass.ui.components.ClayCard
import com.example.nomadcompass.ui.components.ClayPill
import com.example.nomadcompass.ui.components.ScrollableClaySlidingTabRow
import com.example.nomadcompass.ui.components.NomadBottomNavigationBar
import com.example.nomadcompass.ui.components.NomadNavTab
import com.example.nomadcompass.ui.components.bounceClick
import com.example.nomadcompass.ui.components.bounceOnState
import com.example.nomadcompass.ui.components.clayShadow
import com.example.nomadcompass.ui.theme.ActionPrimary
import com.example.nomadcompass.ui.theme.Background
import com.example.nomadcompass.ui.theme.GlassCardBackground
import com.example.nomadcompass.ui.theme.GlassCardBorder
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
    var searchInput by remember { mutableStateOf(uiState.searchQuery) }

    // Synchronize local input state when query is reset externally
    LaunchedEffect(uiState.searchQuery) {
        if (uiState.searchQuery.isEmpty() && searchInput.isNotEmpty()) {
            searchInput = ""
        }
    }

    val pills = listOf("All", "My Favs", "Europe", "Asia", "Americas", "Africa", "Oceania")

    val currentDateText = remember {
        val now = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("EEE. d MMMM", Locale.ENGLISH)
        now.format(formatter)
    }
    val greetingName = uiState.userProfile?.userName?.takeIf { it.isNotBlank() } ?: "alvi"

    Scaffold(
        topBar = {
            Column(
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
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                // Top Header Row matching attached reference design
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile Avatar
                    val photoUri = uiState.userProfile?.photoUri
                    val photoFile = remember(photoUri) {
                        if (!photoUri.isNullOrBlank()) File(photoUri).takeIf { it.exists() } else null
                    }

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
                        if (photoFile != null) {
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

                    Spacer(modifier = Modifier.width(12.dp))

                    // Date & Greeting Column
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = currentDateText,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            color = OnSurfaceVariant,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Hello $greetingName",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = OnSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Right Actions: Glass Search Button + Glass Settings Wheel Button
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Circular Glass Search Button
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .bounceClick(onClick = { searchVisible = !searchVisible })
                                .clayShadow(
                                    cornerRadius = 9999.dp,
                                    ambientShadowColor = Color.Black.copy(alpha = if (isDark) 0.35f else 0.12f),
                                    spotShadowColor = Color.Black.copy(alpha = if (isDark) 0.45f else 0.18f),
                                    blurRadius = 6.dp
                                )
                                .clip(CircleShape)
                                .background(
                                    if (searchVisible || searchInput.isNotEmpty())
                                        Primary.copy(alpha = if (isDark) 0.32f else 0.22f)
                                    else
                                        Color.White.copy(alpha = if (isDark) 0.14f else 0.22f)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (searchVisible || searchInput.isNotEmpty())
                                        Primary.copy(alpha = 0.6f)
                                    else
                                        Color.White.copy(alpha = if (isDark) 0.28f else 0.45f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = PhosphorIcons.MagnifyingGlass,
                                contentDescription = "Search",
                                tint = if (searchVisible || searchInput.isNotEmpty()) Primary else OnSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Circular Glass Settings Button (preserves setting wheel icon)
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .bounceClick(onClick = onProfileClick)
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
                                imageVector = PhosphorIcons.GearSix,
                                contentDescription = "Settings",
                                tint = OnSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Expandable Frosted Glass Search Bar
                AnimatedVisibility(
                    visible = searchVisible || searchInput.isNotEmpty(),
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    OutlinedTextField(
                        value = searchInput,
                        onValueChange = {
                            searchInput = it
                            viewModel.onSearchQueryChanged(it)
                        },
                        placeholder = {
                            Text(
                                "Search destinations, countries...",
                                color = OnSurfaceVariant.copy(alpha = 0.8f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = PhosphorIcons.MagnifyingGlass,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            if (searchInput.isNotEmpty()) {
                                Icon(
                                    imageVector = PhosphorIcons.XCircle,
                                    contentDescription = "Clear",
                                    tint = OnSurfaceVariant,
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clickable {
                                            searchInput = ""
                                            viewModel.onSearchQueryChanged("")
                                        }
                                )
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(9999.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = Color.White.copy(alpha = if (isDark) 0.25f else 0.40f),
                            focusedTextColor = OnSurface,
                            unfocusedTextColor = OnSurface,
                            cursorColor = Primary,
                            focusedContainerColor = Color.White.copy(alpha = if (isDark) 0.14f else 0.20f),
                            unfocusedContainerColor = Color.White.copy(alpha = if (isDark) 0.14f else 0.20f),
                        )
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
                bottom = 110.dp
            )
        ) {
            // Hero Title matching screenshot: "Where Will You Go Next?"
            item(key = "hero_title") {
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

            // Horizontal Pill Carousel with Animated Sliding Background Effect
            item(key = "pill_carousel") {
                Box(modifier = Modifier.padding(top = 8.dp, bottom = 18.dp)) {
                    ScrollableClaySlidingTabRow(
                        tabs = pills,
                        selectedTab = uiState.selectedPill,
                        onTabSelected = viewModel::onPillSelected,
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        height = 42.dp,
                        spacing = 10.dp,
                        fontSize = 13.5.sp,
                        labelProvider = { it }
                    )
                }
            }

            // Section Header: "You Might Also Like"
            item(key = "section_header") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
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
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = Primary,
                        modifier = Modifier
                            .clickable { viewModel.onPillSelected("All") }
                            .padding(4.dp)
                    )
                }
            }

            // Fast, Lazy Rendered Destination Cards (matching Bali card layout in screenshot)
            if (uiState.countries.isEmpty()) {
                item(key = "empty_destinations") {
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
                                imageVector = PhosphorIcons.MagnifyingGlass,
                                contentDescription = null,
                                tint = OnSurfaceVariant,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No destinations in ${uiState.selectedPill}",
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
                                onClick = {
                                    searchInput = ""
                                    viewModel.resetFilters()
                                }
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
                items(
                    items = uiState.countries,
                    key = { it.cca3 },
                    contentType = { "destination_card" }
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
 * Features stacked frosted glass top tabs, frosted sage glass frame, semi-transparent dark overlay for high contrast,
 * and translucent cutout notches for title and location.
 */
@Composable
private fun SageDestinationCard(
    country: Country,
    onCardClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
) {
    val isDark = LocalThemeController.current.isDarkMode
    val cardShape = remember { RoundedCornerShape(36.dp) }

    val sageGradientStart = if (isDark) Color(0xFF537E76) else Color(0xFF7AA59D)
    val sageGradientEnd = if (isDark) Color(0xFF385E56) else Color(0xFF59857D)
    val sageSolid = if (isDark) Color(0xFF456F67) else Color(0xFF67928A)

    val titleNotchShape = remember {
        RoundedCornerShape(
            topStart = 28.dp,
            bottomEnd = 26.dp,
            topEnd = 8.dp,
            bottomStart = 8.dp
        )
    }
    val locationNotchShape = remember {
        RoundedCornerShape(
            topStart = 24.dp,
            bottomEnd = 28.dp,
            topEnd = 8.dp,
            bottomStart = 8.dp
        )
    }

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
    val cardSpecularBrush = remember {
        Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,
                Color.White.copy(alpha = 0.25f),
                Color.Transparent
            )
        )
    }
    val darkOverlayBrush = remember {
        Brush.verticalGradient(
            colors = listOf(
                Color.Black.copy(alpha = 0.50f),
                Color.Black.copy(alpha = 0.18f),
                Color.Black.copy(alpha = 0.60f)
            )
        )
    }

    // Animated heart color and scale for interactive favoriting micro-interaction
    val heartColor by animateColorAsState(
        targetValue = if (country.isFavorite) ActionPrimary else Color.White,
        animationSpec = tween(250),
        label = "heartColor"
    )
    val heartScale by animateFloatAsState(
        targetValue = if (country.isFavorite) 1.15f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "heartScale"
    )

    // Main Destination Card (matching reference image with Frosted Glass styling)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(340.dp)
            .bounceClick(scaleDown = 0.98f, onClick = onCardClick)
            .clayShadow(
                cornerRadius = 36.dp,
                ambientShadowColor = Color.Black.copy(alpha = if (isDark) 0.35f else 0.15f),
                spotShadowColor = Color.Black.copy(alpha = if (isDark) 0.50f else 0.22f),
                blurRadius = 16.dp
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
                    brush = cardSpecularBrush,
                    size = size.copy(height = 1.5.dp.toPx())
                )
            }
            .padding(10.dp)
    ) {
        // Main Destination Image Cutout Canvas with Deep Organic Rounded Corners
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(28.dp))
                .background(Color.Black.copy(alpha = 0.25f))
        ) {
            AsyncImage(
                model = country.flagUrl,
                contentDescription = country.commonName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Semi-transparent dark overlay for high contrast and ultra-legible typography
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(brush = darkOverlayBrush)
            )

            // Top-Left Frosted Dark Glass Notch for Destination Title
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .clip(titleNotchShape)
                    .background(Color(0xCC121212))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.08f),
                                Color.White.copy(alpha = 0.02f)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = GlassCardBorder,
                        shape = titleNotchShape
                    )
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = country.commonName,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.3).sp
                    ),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Top-Right: Glass Favorite Heart Button with Interactive Micro-interactions
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .size(42.dp)
                    .bounceClick(scaleDown = 0.82f, onClick = onFavoriteToggle)
                    .bounceOnState(state = country.isFavorite, maxScale = 1.35f)
                    .clayShadow(
                        cornerRadius = 9999.dp,
                        ambientShadowColor = Color.Black.copy(alpha = 0.40f),
                        spotShadowColor = Color.Black.copy(alpha = 0.50f),
                        blurRadius = 8.dp
                    )
                    .clip(CircleShape)
                    .background(Color(0xCC121212))
                    .border(
                        width = 1.2.dp,
                        color = if (country.isFavorite) ActionPrimary.copy(alpha = 0.7f) else GlassCardBorder,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (country.isFavorite) PhosphorIcons.HeartFill else PhosphorIcons.Heart,
                    contentDescription = "Favorite",
                    tint = heartColor,
                    modifier = Modifier
                        .size(20.dp)
                        .graphicsLayer {
                            scaleX = heartScale
                            scaleY = heartScale
                        }
                )
            }

            // Bottom-Right Frosted Dark Glass Notch for Country / Region Location
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .clip(locationNotchShape)
                    .background(Color(0xCC121212))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.08f),
                                Color.White.copy(alpha = 0.02f)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = GlassCardBorder,
                        shape = locationNotchShape
                    )
                    .padding(horizontal = 16.dp, vertical = 9.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Location pin icon
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(ActionPrimary.copy(alpha = 0.20f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = PhosphorIcons.MapPin,
                            contentDescription = null,
                            tint = ActionPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = country.region.ifBlank { country.commonName },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = Color.White
                    )
                }
            }
        }
    }
}



