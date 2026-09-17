package com.example.nomadcompass.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nomadcompass.ui.theme.OnPrimaryContainer
import com.example.nomadcompass.ui.theme.OnSurface
import com.example.nomadcompass.ui.theme.OnSurfaceVariant
import com.example.nomadcompass.ui.theme.Primary
import com.example.nomadcompass.ui.theme.SurfaceContainer
import com.example.nomadcompass.ui.theme.SurfaceContainerHigh
import com.example.nomadcompass.ui.theme.icons.PhosphorIcons
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/**
 * Modern frosted glass calendar date picker dialog matching NomadCompass cards design.
 * Features specular sheen, glass blur background, and emerald/sage tactile highlights.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NomadDatePickerDialog(
    initialDate: String,
    title: String = "Select Date",
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val initialMillis = remember(initialDate) {
        try {
            if (initialDate.isNotBlank()) {
                val parts = initialDate.trim().split("-")
                if (parts.size == 3) {
                    val year = parts[0].toInt()
                    val month = parts[1].toInt()
                    val day = parts[2].toInt()
                    val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                        clear()
                        set(year, month - 1, day)
                    }
                    cal.timeInMillis
                } else null
            } else null
        } catch (_: Exception) {
            null
        } ?: System.currentTimeMillis()
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis,
        initialDisplayMode = DisplayMode.Picker
    )

    FrostedGlassDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .wrapContentHeight()
            .padding(vertical = 12.dp),
        cornerRadius = 28.dp,
        backgroundColor = SurfaceContainer.copy(alpha = 0.95f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Frosted Glass Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Primary.copy(alpha = 0.18f))
                            .border(1.dp, Primary.copy(alpha = 0.40f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = PhosphorIcons.CalendarBlank,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            color = OnSurface,
                            fontWeight = FontWeight.Bold
                        )
                        val selectedDateStr = datePickerState.selectedDateMillis?.let { millis ->
                            val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                                timeInMillis = millis
                            }
                            String.format(
                                Locale.US,
                                "%04d-%02d-%02d",
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH) + 1,
                                cal.get(Calendar.DAY_OF_MONTH)
                            )
                        } ?: initialDate
                        Text(
                            text = if (selectedDateStr.isNotBlank()) selectedDateStr else "Pick a date",
                            style = MaterialTheme.typography.bodySmall,
                            color = Primary,
                            fontWeight = FontWeight.SemiBold
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

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                thickness = 0.8.dp
            )

            // Transparent Material 3 DatePicker with frosted glass backdrop showing through
            DatePicker(
                state = datePickerState,
                title = null,
                headline = null,
                showModeToggle = false,
                colors = DatePickerDefaults.colors(
                    containerColor = Color.Transparent,
                    titleContentColor = OnSurface,
                    headlineContentColor = OnSurface,
                    weekdayContentColor = OnSurfaceVariant,
                    subheadContentColor = OnSurfaceVariant,
                    yearContentColor = OnSurface,
                    currentYearContentColor = Primary,
                    selectedYearContentColor = OnPrimaryContainer,
                    selectedYearContainerColor = Primary,
                    dayContentColor = OnSurface,
                    selectedDayContentColor = OnPrimaryContainer,
                    selectedDayContainerColor = Primary,
                    todayContentColor = Primary,
                    todayDateBorderColor = Primary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                thickness = 0.8.dp
            )

            // Dialog Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = OnSurfaceVariant, fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.width(10.dp))
                ClayButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                                timeInMillis = millis
                            }
                            val formatted = String.format(
                                Locale.US,
                                "%04d-%02d-%02d",
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH) + 1,
                                cal.get(Calendar.DAY_OF_MONTH)
                            )
                            onDateSelected(formatted)
                        }
                        onDismiss()
                    },
                    contentPadding = PaddingValues(horizontal = 22.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Select Date",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
