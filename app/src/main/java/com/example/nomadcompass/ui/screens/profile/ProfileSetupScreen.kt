package com.example.nomadcompass.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import com.example.nomadcompass.ui.theme.icons.PhosphorIcons
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.nomadcompass.ui.components.ClayButton
import com.example.nomadcompass.ui.components.ClayCard
import com.example.nomadcompass.ui.components.ClayPill
import com.example.nomadcompass.ui.components.ClaySlidingTabRow
import com.example.nomadcompass.ui.components.bounceClick
import com.example.nomadcompass.ui.theme.Background
import com.example.nomadcompass.ui.theme.Error
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
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues

import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.ui.draw.blur
import androidx.compose.ui.res.painterResource
import com.example.nomadcompass.R
import com.example.nomadcompass.ui.components.GlassPillButton
import com.example.nomadcompass.ui.theme.SurfaceContainerLowest
import java.io.File

@Composable
fun ProfileSetupScreen(
    viewModel: ProfileSetupViewModel,
    onContinue: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            viewModel.onSavedHandled()
            onContinue()
        }
    }

    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.onPhotoSelected(context, uri)
        }
    }

    // Background Wallpaper picker launcher
    val bgPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.onBgPhotoSelected(context, uri)
        }
    }

    val currencies = listOf("USD", "EUR", "GBP", "JPY", "AUD", "CAD", "SGD", "BRL", "INR")
    var countryDropdownExpanded by remember { mutableStateOf(false) }
    var currencyDropdownExpanded by remember { mutableStateOf(false) }

    var activeTab by remember { mutableStateOf("Preferences") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header Title
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Nomad Settings",
                    style = MaterialTheme.typography.headlineLarge,
                    color = OnSurface,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Profile preferences, security, FAQs, and support",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
            }

            // Tab Selector: Preferences vs Help & Support vs About & Licenses
            ClaySlidingTabRow(
                tabs = listOf("Preferences", "Help & Support", "About & Licenses"),
                selectedTab = activeTab,
                onTabSelected = { activeTab = it },
                fontSize = 11.5.sp,
                labelProvider = { it }
            )

            when (activeTab) {
                "Help & Support" -> {
                    HelpSupportSection(
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                }
                "About & Licenses" -> {
                    AboutLicensesSection(
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                }
                else -> {
                    // Avatar Photo Upload Section
                val photoFile = if (!uiState.photoUri.isNullOrBlank()) File(uiState.photoUri!!) else null

                Box(
                    modifier = Modifier
                        .size(108.dp)
                    .bounceClick(scaleDown = 0.94f) {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(SecondaryContainer)
                        .border(2.5.dp, Primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (photoFile != null && photoFile.exists()) {
                        AsyncImage(
                            model = photoFile,
                            contentDescription = "Profile Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = PhosphorIcons.UserCircle,
                            contentDescription = "Add Avatar",
                            tint = Secondary,
                            modifier = Modifier.size(52.dp)
                        )
                    }
                }

                // Camera Badge Button
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(Primary)
                        .border(2.dp, Background, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = PhosphorIcons.Camera,
                        contentDescription = "Upload Photo",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Main Form Card
            ClayCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 28.dp,
                backgroundColor = SurfaceContainer
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    // Full Name Field
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "FULL NAME",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant,
                            letterSpacing = 1.sp
                        )
                        OutlinedTextField(
                            value = uiState.fullName,
                            onValueChange = viewModel::onFullNameChanged,
                            placeholder = { Text("e.g. Alex Rivera", color = OnSurfaceVariant.copy(alpha = 0.5f)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = PhosphorIcons.UserCircle,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            singleLine = true,
                            isError = uiState.errorMessage != null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(CircleShape)
                                .background(SurfaceContainerLow),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                focusedTextColor = OnSurface,
                                unfocusedTextColor = OnSurface,
                                cursorColor = Primary,
                            )
                        )
                    }

                    // Home Country Selection
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "HOME COUNTRY / PASSPORT",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant,
                            letterSpacing = 1.sp
                        )
                        val selectedCountry = uiState.availableCountries.firstOrNull { it.cca3 == uiState.homeCountryCca3 }
                        val countryDisplayText = selectedCountry?.let { "${it.flagEmoji} ${it.commonName}" } ?: uiState.homeCountryCca3

                        Box(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceContainerLow)
                                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                                    .bounceClick { countryDropdownExpanded = true }
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = PhosphorIcons.GlobeSimple,
                                        contentDescription = null,
                                        tint = Secondary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = countryDisplayText,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = OnSurface,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Icon(
                                    imageVector = PhosphorIcons.CaretDown,
                                    contentDescription = null,
                                    tint = OnSurfaceVariant
                                )
                            }

                            DropdownMenu(
                                expanded = countryDropdownExpanded,
                                onDismissRequest = { countryDropdownExpanded = false },
                                modifier = Modifier.background(SurfaceContainerHigh)
                            ) {
                                uiState.availableCountries.forEach { country ->
                                    DropdownMenuItem(
                                        text = { Text("${country.flagEmoji} ${country.commonName}", color = OnSurface) },
                                        onClick = {
                                            viewModel.onHomeCountryChanged(country.cca3)
                                            countryDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Base Currency Selection
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "BASE EXPENSE CURRENCY",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant,
                            letterSpacing = 1.sp
                        )
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceContainerLow)
                                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                                    .bounceClick { currencyDropdownExpanded = true }
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = PhosphorIcons.Coins,
                                        contentDescription = null,
                                        tint = Primary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = uiState.baseCurrencyCode,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = OnSurface,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Icon(
                                    imageVector = PhosphorIcons.CaretDown,
                                    contentDescription = null,
                                    tint = OnSurfaceVariant
                                )
                            }

                            DropdownMenu(
                                expanded = currencyDropdownExpanded,
                                onDismissRequest = { currencyDropdownExpanded = false },
                                modifier = Modifier.background(SurfaceContainerHigh)
                            ) {
                                currencies.forEach { curr ->
                                    DropdownMenuItem(
                                        text = { Text(curr, color = OnSurface) },
                                        onClick = {
                                            viewModel.onBaseCurrencyChanged(curr)
                                            currencyDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Temperature Unit Preference
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "TEMPERATURE SCALE",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant,
                            letterSpacing = 1.sp
                        )
                        ClaySlidingTabRow(
                            tabs = listOf("C", "F"),
                            selectedTab = uiState.tempUnit,
                            onTabSelected = viewModel::onTempUnitChanged,
                            labelProvider = { if (it == "C") "Celsius (°C)" else "Fahrenheit (°F)" }
                        )
                    }
                }
            }

            // App Background Wallpaper & Blurriness Card
            ClayCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 28.dp,
                backgroundColor = SurfaceContainer
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = PhosphorIcons.FileImage,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "App Background Wallpaper",
                                style = MaterialTheme.typography.titleMedium,
                                color = OnSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Upload custom wallpaper & adjust blur level",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant
                            )
                        }
                    }

                    // Live Wallpaper Preview Window
                    val customBgFile = if (!uiState.bgPhotoUri.isNullOrBlank()) File(uiState.bgPhotoUri!!) else null
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        val blurModifier = Modifier
                            .fillMaxSize()
                            .then(
                                if (uiState.bgBlurRadius > 0f) Modifier.blur(uiState.bgBlurRadius.dp)
                                else Modifier
                            )

                        if (customBgFile != null && customBgFile.exists()) {
                            AsyncImage(
                                model = customBgFile,
                                contentDescription = "Custom Background Preview",
                                contentScale = ContentScale.Crop,
                                modifier = blurModifier
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.app_default_bg),
                                contentDescription = "Default Background Preview",
                                contentScale = ContentScale.Crop,
                                modifier = blurModifier
                            )
                        }

                        // Gradient overlay with status badge
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.35f))
                        )

                        // Badge showing current source
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(9999.dp))
                                .background(Color.Black.copy(alpha = 0.65f))
                                .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(9999.dp))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (customBgFile != null && customBgFile.exists()) "📷 Custom Wallpaper" else "🎡 Default Ferris Wheel",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Wallpaper Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        GlassPillButton(
                            onClick = {
                                bgPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.weight(1.2f),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Icon(imageVector = PhosphorIcons.UploadSimple, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Upload Photo", style = MaterialTheme.typography.labelMedium, color = Primary, fontWeight = FontWeight.Bold)
                        }

                        if (!uiState.bgPhotoUri.isNullOrBlank()) {
                            GlassPillButton(
                                onClick = { viewModel.onResetDefaultBg(context) },
                                modifier = Modifier.weight(0.9f),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                Icon(imageVector = PhosphorIcons.ArrowsClockwise, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Reset Default", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                            }
                        }
                    }

                    // Blur Slider Section
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "BLURRINESS LEVEL",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariant,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "${uiState.bgBlurRadius.toInt()} dp",
                                style = MaterialTheme.typography.labelMedium,
                                color = Primary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Slider(
                            value = uiState.bgBlurRadius,
                            onValueChange = viewModel::onBgBlurRadiusChanged,
                            valueRange = 0f..50f,
                            steps = 25,
                            colors = SliderDefaults.colors(
                                thumbColor = Primary,
                                activeTrackColor = Primary,
                                inactiveTrackColor = Color.White.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Clear (0 dp)", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant.copy(alpha = 0.6f), fontSize = 11.sp)
                            Text("Frosted (24 dp)", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant.copy(alpha = 0.6f), fontSize = 11.sp)
                            Text("Ultra Blur (50 dp)", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant.copy(alpha = 0.6f), fontSize = 11.sp)
                        }
                    }
                }
            }

            // Security Settings Card
            ClayCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 28.dp,
                backgroundColor = SurfaceContainer
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val isSecOn = uiState.securityOption != SecurityOption.NONE

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = PhosphorIcons.ShieldCheck,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Secure App Launch",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = OnSurface,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Protect app with PIN or Biometrics",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnSurfaceVariant
                                )
                            }
                        }
                        Switch(
                            checked = isSecOn,
                            onCheckedChange = { checked ->
                                if (checked) {
                                    viewModel.onSecurityOptionChanged(SecurityOption.PIN)
                                } else {
                                    viewModel.onSecurityOptionChanged(SecurityOption.NONE)
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Primary,
                                uncheckedTrackColor = SurfaceContainerHigh
                            )
                        )
                    }

                    // Security Method Selection (PIN vs Biometrics)
                    AnimatedVisibility(
                        visible = isSecOn,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text(
                                text = "AUTHENTICATION METHOD",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariant,
                                letterSpacing = 1.sp
                            )

                            ClaySlidingTabRow(
                                tabs = listOf(SecurityOption.PIN, SecurityOption.BIOMETRIC),
                                selectedTab = uiState.securityOption,
                                onTabSelected = viewModel::onSecurityOptionChanged,
                                labelProvider = {
                                    when (it) {
                                        SecurityOption.PIN -> "🔐 PIN Code"
                                        SecurityOption.BIOMETRIC -> "👆 Biometric"
                                        else -> ""
                                    }
                                }
                            )

                            // PIN Input Field
                            if (uiState.securityOption == SecurityOption.PIN) {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "SET 4-DIGIT ACCESS PIN",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = OnSurfaceVariant,
                                        letterSpacing = 1.sp
                                    )
                                    OutlinedTextField(
                                        value = uiState.accessCode,
                                        onValueChange = viewModel::onAccessCodeChanged,
                                        placeholder = { Text("4-digit PIN (e.g. 1234)", color = OnSurfaceVariant.copy(alpha = 0.5f)) },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = PhosphorIcons.LockKey,
                                                contentDescription = null,
                                                tint = Primary,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        },
                                        singleLine = true,
                                        visualTransformation = PasswordVisualTransformation(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(CircleShape)
                                            .background(SurfaceContainerLow),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Primary,
                                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                            focusedTextColor = OnSurface,
                                            unfocusedTextColor = OnSurface,
                                            cursorColor = Primary,
                                            focusedContainerColor = SurfaceContainerLow,
                                            unfocusedContainerColor = SurfaceContainerLow,
                                        )
                                    )
                                }
                            } else if (uiState.securityOption == SecurityOption.BIOMETRIC) {
                                // Biometric Mode Info Banner
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(SurfaceContainerLow)
                                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                                        .padding(horizontal = 16.dp, vertical = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = PhosphorIcons.Fingerprint,
                                        contentDescription = null,
                                        tint = Secondary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Biometrics (Fingerprint / Face ID) will be prompted when launching the app.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = OnSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Error Message
            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage ?: "",
                    style = MaterialTheme.typography.labelMedium,
                    color = Error,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Submit Button
            ClayButton(
                onClick = viewModel::saveProfile,
                enabled = !uiState.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Text(
                    text = if (uiState.isSaving) "SAVING..." else "SAVE PROFILE & CONTINUE",
                    style = MaterialTheme.typography.labelLarge,
                    color = OnPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
}
}

