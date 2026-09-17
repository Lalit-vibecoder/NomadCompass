package com.example.nomadcompass.ui.screens.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.nomadcompass.ui.theme.icons.PhosphorIcons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nomadcompass.ui.components.ClayButton
import com.example.nomadcompass.ui.components.ClayCard
import com.example.nomadcompass.ui.components.bounceClick
import com.example.nomadcompass.ui.theme.OnPrimaryContainer
import com.example.nomadcompass.ui.theme.OnSurface
import com.example.nomadcompass.ui.theme.OnSurfaceVariant
import com.example.nomadcompass.ui.theme.Primary
import com.example.nomadcompass.ui.theme.Secondary
import com.example.nomadcompass.ui.theme.SurfaceContainer
import com.example.nomadcompass.ui.theme.SurfaceContainerLow

@Composable
fun HelpSupportSection(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Section 1: Quick Start Guide
        QuickStartGuideCard()

        // Section 2: Frequently Asked Questions (FAQs)
        FaqSectionCard()

        // Section 3: Contact Options
        ContactOptionsCard(context = context)
    }
}

/**
 * Interactive Quick Start Guide detailing app essentials
 */
@Composable
private fun QuickStartGuideCard() {
    ClayCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        backgroundColor = SurfaceContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = PhosphorIcons.Lightbulb,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Quick Start Guide",
                        style = MaterialTheme.typography.titleMedium,
                        color = OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Everything you need to travel prepared",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant
                    )
                }
            }

            QuickStartItem(
                icon = PhosphorIcons.GlobeSimple,
                title = "1. Explore Destinations Offline",
                description = "Browse 250+ countries with visa advice, currency rates, emergency numbers, and plug sockets available offline."
            )

            QuickStartItem(
                icon = PhosphorIcons.AirplaneTilt,
                title = "2. Smart Itinerary Extractor",
                description = "Paste booking confirmation text or import travel PDFs to automatically detect flights, hotels, and activities with on-device OCR."
            )

            QuickStartItem(
                icon = PhosphorIcons.Coins,
                title = "3. Dual-Currency Expenses",
                description = "Log expenses in destination currency with instant conversion to your home currency. View clean balance indicators and tap any card to view or edit."
            )

            QuickStartItem(
                icon = PhosphorIcons.SquaresFour,
                title = "4. Home Screen Packing Widget",
                description = "Add the NomadCompass widget to your phone's home screen to check and uncheck luggage items on the go without launching the app."
            )

            QuickStartItem(
                icon = PhosphorIcons.ShieldCheck,
                title = "5. Biometric Privacy",
                description = "Enable fingerprint or face unlock under Security settings to keep your bookings, passports, and notes securely protected."
            )
        }
    }
}

@Composable
private fun QuickStartItem(
    icon: ImageVector,
    title: String,
    description: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(SurfaceContainerLow),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Secondary,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = OnSurface,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant,
                lineHeight = 18.sp
            )
        }
    }
}

/**
 * Expandable FAQs Section
 */
@Composable
private fun FaqSectionCard() {
    ClayCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        backgroundColor = SurfaceContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Secondary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = PhosphorIcons.Question,
                        contentDescription = null,
                        tint = Secondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Frequently Asked Questions",
                        style = MaterialTheme.typography.titleMedium,
                        color = OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Common questions & solutions",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            FaqItem(
                question = "Does NomadCompass work completely offline?",
                answer = "Yes! All country intelligence, saved itineraries, documents, expenses, and packing lists are stored locally in your on-device Room database. You can travel anywhere with zero internet connectivity."
            )

            FaqItem(
                question = "How do I add and use the Packing List widget?",
                answer = "Go to your Android home screen, press and hold an empty space, tap 'Widgets', find 'NomadCompass', and place the 'Packing List' widget. You can tap checkboxes directly on the widget to check or uncheck items."
            )

            FaqItem(
                question = "How are exchange rates updated?",
                answer = "Exchange rates are cached when you have an active network connection. When offline, NomadCompass seamlessly uses the latest cached rates so conversions remain accurate."
            )

            FaqItem(
                question = "Are my travel documents and receipts sent to any server?",
                answer = "No. NomadCompass is privacy-first. All documents, OCR scans, and expense receipts are kept strictly on your device storage."
            )

            FaqItem(
                question = "Can I edit an expense after logging it?",
                answer = "Yes! In your trip's Expenses tab under 'Recent Transactions', simply tap any transaction card to open the edit sheet and update its amount, currency, category, or notes."
            )
        }
    }
}

@Composable
private fun FaqItem(
    question: String,
    answer: String,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceContainerLow)
            .border(
                width = 1.dp,
                color = if (expanded) Primary.copy(alpha = 0.3f) else Color.Transparent,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { expanded = !expanded }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = question,
                style = MaterialTheme.typography.titleSmall,
                color = if (expanded) Primary else OnSurface,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (expanded) PhosphorIcons.CaretUp else PhosphorIcons.CaretDown,
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = if (expanded) Primary else OnSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = answer,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant,
                    lineHeight = 19.sp
                )
            }
        }
    }
}

/**
 * Contact and Support Options
 */
@Composable
private fun ContactOptionsCard(
    context: Context,
) {
    ClayCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        backgroundColor = SurfaceContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = PhosphorIcons.EnvelopeSimple,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Contact Support",
                        style = MaterialTheme.typography.titleMedium,
                        color = OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "We are here to help your journey",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Contact Action: Email Support
            ContactActionRow(
                icon = PhosphorIcons.EnvelopeSimple,
                title = "Email Support",
                subtitle = "support@nomadcompass.app",
                onClick = {
                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:support@nomadcompass.app?subject=NomadCompass%20Support%20Request")
                    }
                    try {
                        context.startActivity(Intent.createChooser(emailIntent, "Send Email"))
                    } catch (_: Exception) {
                        Toast.makeText(context, "No email client available", Toast.LENGTH_SHORT).show()
                    }
                }
            )

            // Contact Action: Bug Report
            ContactActionRow(
                icon = PhosphorIcons.Bug,
                title = "Report an Issue",
                subtitle = "Share logs or glitches encountered",
                onClick = {
                    val deviceInfo = "\n\n---\nDevice: ${Build.MANUFACTURER} ${Build.MODEL}\nAndroid SDK: ${Build.VERSION.SDK_INT}"
                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:support@nomadcompass.app?subject=NomadCompass%20Bug%20Report&body=${Uri.encode(deviceInfo)}")
                    }
                    try {
                        context.startActivity(Intent.createChooser(emailIntent, "Report Bug"))
                    } catch (_: Exception) {
                        Toast.makeText(context, "No email client available", Toast.LENGTH_SHORT).show()
                    }
                }
            )

            // Contact Action: Feedback
            ContactActionRow(
                icon = PhosphorIcons.ChatTeardropText,
                title = "Feature Suggestions",
                subtitle = "Tell us what feature you want next",
                onClick = {
                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:support@nomadcompass.app?subject=NomadCompass%20Feature%20Suggestion")
                    }
                    try {
                        context.startActivity(Intent.createChooser(emailIntent, "Send Feedback"))
                    } catch (_: Exception) {
                        Toast.makeText(context, "No email client available", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }
}

@Composable
private fun ContactActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceContainerLow)
            .bounceClick(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = OnSurface,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant
            )
        }
    }
}
