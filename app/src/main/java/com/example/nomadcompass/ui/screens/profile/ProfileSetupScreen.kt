package com.example.nomadcompass.ui.screens.profile

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nomadcompass.ui.components.ClayButton
import com.example.nomadcompass.ui.components.ClayCard
import com.example.nomadcompass.ui.theme.Background
import com.example.nomadcompass.ui.theme.OnPrimary
import com.example.nomadcompass.ui.theme.OnSurface
import com.example.nomadcompass.ui.theme.OnSurfaceVariant
import com.example.nomadcompass.ui.theme.Primary
import com.example.nomadcompass.ui.theme.Secondary
import com.example.nomadcompass.ui.theme.SurfaceContainer
import com.example.nomadcompass.ui.theme.SurfaceContainerHighest

@Composable
fun ProfileSetupScreen(
    viewModel: ProfileSetupViewModel,
    onContinue: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            viewModel.onSavedHandled()
            onContinue()
        }
    }

    val currencies = listOf("USD", "EUR", "GBP", "JPY", "AUD", "CAD", "SGD", "BRL", "INR")

    var countryDropdownExpanded by remember { mutableStateOf(false) }
    var currencyDropdownExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Setup Profile",
                    style = MaterialTheme.typography.headlineLarge,
                    color = OnSurface
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Form Card (Claymorphism)
            ClayCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 32.dp,
                backgroundColor = SurfaceContainer
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
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
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = OnSurfaceVariant,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            singleLine = true,
                            isError = uiState.errorMessage != null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(CircleShape)
                                .background(Background),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary.copy(alpha = 0.3f),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.05f),
                                focusedTextColor = OnSurface,
                                unfocusedTextColor = OnSurface,
                            )
                        )
                        if (uiState.errorMessage != null) {
                            Text(
                                text = uiState.errorMessage!!,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    // Home Country Dropdown
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "HOME COUNTRY",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant,
                            letterSpacing = 1.sp
                        )
                        Box {
                            val selectedCountry = uiState.availableCountries.find { it.cca3 == uiState.homeCountryCca3 }
                            val displayLabel = if (selectedCountry != null) {
                                "${selectedCountry.flagEmoji}  ${selectedCountry.commonName}"
                            } else "Select origin"

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .clip(CircleShape)
                                    .background(Background)
                                    .border(1.dp, Color.White.copy(alpha = 0.05f), CircleShape)
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
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
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
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            DropdownMenu(
                                expanded = countryDropdownExpanded,
                                onDismissRequest = { countryDropdownExpanded = false }
                            ) {
                                uiState.availableCountries.forEach { country ->
                                    DropdownMenuItem(
                                        text = { Text("${country.flagEmoji}  ${country.commonName}") },
                                        onClick = {
                                            viewModel.onHomeCountryChanged(country.cca3)
                                            countryDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // 2-Column Row for Currency & Temp Unit
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Base Currency
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "BASE CURRENCY",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariant,
                                letterSpacing = 1.sp
                            )
                            Box {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .clip(CircleShape)
                                        .background(Background)
                                        .border(1.dp, Color.White.copy(alpha = 0.05f), CircleShape)
                                        .clickable { currencyDropdownExpanded = true }
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Payments,
                                            contentDescription = null,
                                            tint = OnSurfaceVariant,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = uiState.baseCurrencyCode,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = OnSurface
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ExpandMore,
                                        contentDescription = null,
                                        tint = OnSurfaceVariant,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                DropdownMenu(
                                    expanded = currencyDropdownExpanded,
                                    onDismissRequest = { currencyDropdownExpanded = false }
                                ) {
                                    currencies.forEach { curr ->
                                        DropdownMenuItem(
                                            text = { Text(curr) },
                                            onClick = {
                                                viewModel.onBaseCurrencyChanged(curr)
                                                currencyDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Temp Unit Toggle (°C / °F)
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "TEMP. UNIT",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariant,
                                letterSpacing = 1.sp
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .clip(CircleShape)
                                    .background(Background)
                                    .padding(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val isC = uiState.tempUnit == "C"
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clip(CircleShape)
                                        .background(if (isC) Color(0xFFCCC6BC) else Color.Transparent)
                                        .clickable { viewModel.onTempUnitChanged("C") },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "°C",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = if (isC) FontWeight.Bold else FontWeight.Medium
                                        ),
                                        color = if (isC) Color(0xFF353025) else OnSurfaceVariant
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clip(CircleShape)
                                        .background(if (!isC) Color(0xFFCCC6BC) else Color.Transparent)
                                        .clickable { viewModel.onTempUnitChanged("F") },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "°F",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = if (!isC) FontWeight.Bold else FontWeight.Medium
                                        ),
                                        color = if (!isC) Color(0xFF353025) else OnSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Explanation Text
                    Text(
                        text = "These settings help us personalize your travel insights, weather warnings, and budget forecasts.",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // CTA Button
            ClayButton(
                onClick = viewModel::saveProfile,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSaving
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (uiState.isSaving) "Saving..." else "Save & Continue",
                        style = MaterialTheme.typography.headlineSmall,
                        color = OnPrimary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = OnPrimary
                    )
                }
            }
        }
    }
}
