package com.example.nomadcompass.ui.screens.profile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.nomadcompass.ui.components.ClayButton
import com.example.nomadcompass.ui.components.ClayCard
import com.example.nomadcompass.ui.components.bounceClick
import com.example.nomadcompass.ui.theme.OnPrimary
import com.example.nomadcompass.ui.theme.OnPrimaryContainer
import com.example.nomadcompass.ui.theme.OnSurface
import com.example.nomadcompass.ui.theme.OnSurfaceVariant
import com.example.nomadcompass.ui.theme.Primary
import com.example.nomadcompass.ui.theme.PrimaryContainer
import com.example.nomadcompass.ui.theme.Secondary
import com.example.nomadcompass.ui.theme.SurfaceContainer
import com.example.nomadcompass.ui.theme.SurfaceContainerHigh
import com.example.nomadcompass.ui.theme.SurfaceContainerLow
import com.example.nomadcompass.ui.theme.icons.GlassIconBadge
import com.example.nomadcompass.ui.theme.icons.PhosphorIcons

/**
 * Information model for an open-source library or licensed artifact.
 */
data class OpenSourceLicenseItem(
    val name: String,
    val author: String,
    val licenseType: String,
    val icon: ImageVector,
    val description: String,
    val copyrightNotice: String,
    val fullLicenseText: String
)

@Composable
fun AboutLicensesSection(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedItemForLicense by remember { mutableStateOf<OpenSourceLicenseItem?>(null) }

    val licenses = remember { getOpenSourceLicenses() }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // App Overview & Mission Card
        AppOverviewCard()

        // Privacy & Architecture Card
        PrivacyArchitectureCard()

        // Open Source Licenses List Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = PhosphorIcons.FileText,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Open Source Acknowledgements",
                    style = MaterialTheme.typography.titleLarge,
                    color = OnSurface,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "NomadCompass is proudly built with open-source software. Below are the third-party components, authors, and licenses used in accordance with their terms.",
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant
            )
        }

        // License Cards
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            licenses.forEach { item ->
                LicenseCardItem(
                    item = item,
                    onViewLicense = { selectedItemForLicense = item }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // Modal to view full license text
    selectedItemForLicense?.let { item ->
        LicenseViewerDialog(
            item = item,
            onDismiss = { selectedItemForLicense = null },
            onCopy = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("${item.name} License", "${item.copyrightNotice}\n\n${item.fullLicenseText}")
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "${item.name} license copied to clipboard", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

/**
 * Header card presenting NomadCompass version, mission, and creators.
 */
@Composable
private fun AppOverviewCard() {
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                GlassIconBadge(
                    icon = PhosphorIcons.Compass,
                    contentDescription = "NomadCompass Logo",
                    size = 54.dp,
                    iconSize = 28.dp,
                    containerColor = Primary.copy(alpha = 0.15f),
                    tint = Primary,
                    shape = RoundedCornerShape(16.dp)
                )

                Column {
                    Text(
                        text = "NomadCompass",
                        style = MaterialTheme.typography.headlineSmall,
                        color = OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Version 1.0.0 • On-Device Travel Hub",
                        style = MaterialTheme.typography.labelSmall,
                        color = Primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                thickness = 1.dp
            )

            Text(
                text = "NomadCompass is an offline-first companion tailored for digital nomads, remote workers, and independent travelers. It bundles trip finances, multi-currency live conversions, document storage, and on-device machine learning into a fluid claymorphic experience.",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurface.copy(alpha = 0.9f),
                lineHeight = 22.sp
            )
        }
    }
}

/**
 * Card highlighting on-device privacy guarantee.
 */
@Composable
private fun PrivacyArchitectureCard() {
    ClayCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 20.dp,
        backgroundColor = SurfaceContainerLow
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Primary.copy(alpha = 0.14f))
                        .border(1.dp, Primary.copy(alpha = 0.35f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = PhosphorIcons.ShieldCheck,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = "100% On-Device Privacy Guarantee",
                    style = MaterialTheme.typography.titleMedium,
                    color = OnSurface,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Your sensitive boarding passes, hotel reservations, expenses, and notes never touch third-party servers. All text extraction and OCR operate completely within your local device sandbox.",
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant,
                lineHeight = 19.sp
            )
        }
    }
}

/**
 * Individual open-source library card with license badge and inspect action.
 */
@Composable
private fun LicenseCardItem(
    item: OpenSourceLicenseItem,
    onViewLicense: () -> Unit
) {
    ClayCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 18.dp,
        backgroundColor = SurfaceContainerLow
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Primary.copy(alpha = 0.12f))
                            .border(1.dp, Primary.copy(alpha = 0.30f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = OnSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = item.author,
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant
                        )
                    }
                }

                // License Pill Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(PrimaryContainer.copy(alpha = 0.35f))
                        .border(1.dp, Primary.copy(alpha = 0.35f), RoundedCornerShape(9999.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = item.licenseType,
                        style = MaterialTheme.typography.labelSmall,
                        color = Primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.5.sp
                    )
                }
            }

            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant,
                lineHeight = 18.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onViewLicense,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "View License",
                            style = MaterialTheme.typography.labelMedium,
                            color = Primary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            imageVector = PhosphorIcons.ArrowSquareOut,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Dialog displaying full license text with copy functionality.
 */
@Composable
private fun LicenseViewerDialog(
    item: OpenSourceLicenseItem,
    onDismiss: () -> Unit,
    onCopy: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val rimBrush = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.25f),
                Color.White.copy(alpha = 0.05f)
            )
        )

        Box(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(SurfaceContainer)
                .border(1.dp, rimBrush, RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = OnSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = item.licenseType,
                                style = MaterialTheme.typography.labelSmall,
                                color = Primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = PhosphorIcons.X,
                            contentDescription = "Close",
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                    thickness = 1.dp
                )

                // Copyright summary
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceContainerHigh.copy(alpha = 0.6f))
                        .padding(10.dp)
                ) {
                    Text(
                        text = item.copyrightNotice,
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurface,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Scrollable License Text
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 320.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceContainerLow)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        Text(
                            text = item.fullLicenseText,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = OnSurfaceVariant
                        )
                    }
                }

                // Actions: Copy & Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onCopy) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = PhosphorIcons.CopySimple,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text("Copy Notice", color = Primary, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    ClayButton(
                        onClick = onDismiss,
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "Done",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Factory providing the curated list of licensed items with full license texts.
 */
private fun getOpenSourceLicenses(): List<OpenSourceLicenseItem> {
    return listOf(
        OpenSourceLicenseItem(
            name = "Phosphor Icons",
            author = "Helena Zhang & Tobias Fried",
            licenseType = "MIT License",
            icon = PhosphorIcons.Sparkle,
            description = "Flexible icon family providing clean, geometric travel iconography and glassmorphic badging across all screens.",
            copyrightNotice = "Copyright (c) 2020 Phosphor Icons (Helena Zhang, Tobias Fried)",
            fullLicenseText = """
                MIT License

                Copyright (c) 2020 Phosphor Icons

                Permission is hereby granted, free of charge, to any person obtaining a copy
                of this software and associated documentation files (the "Software"), to deal
                in the Software without restriction, including without limitation the rights
                to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
                copies of the Software, and to permit persons to whom the Software is
                furnished to do so, subject to the following conditions:

                The above copyright notice and this permission notice shall be included in all
                copies or substantial portions of the Software.

                THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
                IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
                FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
                AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
                LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
                OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
                SOFTWARE.
            """.trimIndent()
        ),
        OpenSourceLicenseItem(
            name = "Jsoup HTML Parser",
            author = "Jonathan Hedley",
            licenseType = "MIT License",
            icon = PhosphorIcons.GlobeSimple,
            description = "Java HTML parser used for on-device parsing and sanitization of saved travel itineraries and hotel confirmation pages.",
            copyrightNotice = "Copyright (c) 2009-2024 Jonathan Hedley <https://jsoup.org/>",
            fullLicenseText = """
                The MIT License

                Copyright (c) 2009-2024 Jonathan Hedley <https://jsoup.org/>

                Permission is hereby granted, free of charge, to any person obtaining a copy
                of this software and associated documentation files (the "Software"), to deal
                in the Software without restriction, including without limitation the rights
                to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
                copies of the Software, and to permit persons to whom the Software is
                furnished to do so, subject to the following conditions:

                The above copyright notice and this permission notice shall be included in all
                copies or substantial portions of the Software.

                THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
                IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
                FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
                AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
                LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
                OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
                SOFTWARE.
            """.trimIndent()
        ),
        OpenSourceLicenseItem(
            name = "PdfBox-Android",
            author = "Tom Roush & The Apache Software Foundation",
            licenseType = "Apache License 2.0",
            icon = PhosphorIcons.FilePdf,
            description = "Android port of Apache PDFBox enabling native on-device rendering of PDF boarding passes, visas, and receipts.",
            copyrightNotice = "Copyright 2014-2023 Tom Roush; Copyright 2002-2023 The Apache Software Foundation",
            fullLicenseText = """
                Apache License, Version 2.0

                Copyright 2014-2023 Tom Roush
                Copyright 2002-2023 The Apache Software Foundation

                Licensed under the Apache License, Version 2.0 (the "License");
                you may not use this file except in compliance with the License.
                You may obtain a copy of the License at

                    http://www.apache.org/licenses/LICENSE-2.0

                Unless required by applicable law or agreed to in writing, software
                distributed under the License is distributed on an "AS IS" BASIS,
                WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
                See the License for the specific language governing permissions and
                limitations under the License.
            """.trimIndent()
        ),
        OpenSourceLicenseItem(
            name = "Coil 3 (Coroutine Image Loader)",
            author = "Coil Contributors",
            licenseType = "Apache License 2.0",
            icon = PhosphorIcons.FileImage,
            description = "Asynchronous image loader backing photo attachments, trip wallpapers, and country highlight galleries.",
            copyrightNotice = "Copyright 2024 Coil Contributors",
            fullLicenseText = """
                Apache License, Version 2.0

                Copyright 2024 Coil Contributors

                Licensed under the Apache License, Version 2.0 (the "License");
                you may not use this file except in compliance with the License.
                You may obtain a copy of the License at

                    http://www.apache.org/licenses/LICENSE-2.0

                Unless required by applicable law or agreed to in writing, software
                distributed under the License is distributed on an "AS IS" BASIS,
                WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
                See the License for the specific language governing permissions and
                limitations under the License.
            """.trimIndent()
        ),
        OpenSourceLicenseItem(
            name = "Retrofit, Moshi & OkHttp",
            author = "Square, Inc.",
            licenseType = "Apache License 2.0",
            icon = PhosphorIcons.ArrowsClockwise,
            description = "HTTP client and JSON serialization engine used for fetching live currency exchange rates and parsing country seeds.",
            copyrightNotice = "Copyright 2013-2024 Square, Inc.",
            fullLicenseText = """
                Apache License, Version 2.0

                Copyright Square, Inc.

                Licensed under the Apache License, Version 2.0 (the "License");
                you may not use this file except in compliance with the License.
                You may obtain a copy of the License at

                    http://www.apache.org/licenses/LICENSE-2.0

                Unless required by applicable law or agreed to in writing, software
                distributed under the License is distributed on an "AS IS" BASIS,
                WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
                See the License for the specific language governing permissions and
                limitations under the License.
            """.trimIndent()
        ),
        OpenSourceLicenseItem(
            name = "Google ML Kit Vision & NLP",
            author = "Google LLC",
            licenseType = "Google APIs ToS & Apache 2.0",
            icon = PhosphorIcons.Eye,
            description = "Powers offline optical character recognition (OCR) and travel entity extraction for flight tickets and bookings.",
            copyrightNotice = "Copyright Google LLC. All Rights Reserved.",
            fullLicenseText = """
                Google ML Kit

                Copyright Google LLC. All Rights Reserved.
                Distributed in accordance with Google APIs Terms of Service and Apache License 2.0.

                Licensed under the Apache License, Version 2.0 (the "License");
                you may not use this file except in compliance with the License.
                You may obtain a copy of the License at

                    http://www.apache.org/licenses/LICENSE-2.0

                Enables 100% on-device text recognition and entity extraction without transmitting document data off-device.
            """.trimIndent()
        ),
        OpenSourceLicenseItem(
            name = "Android Jetpack & Compose",
            author = "The Android Open Source Project / Google LLC",
            licenseType = "Apache License 2.0",
            icon = PhosphorIcons.SquaresFour,
            description = "Declarative Jetpack Compose UI toolkit, Room SQLite persistence, Biometric authentication, and Architecture Components.",
            copyrightNotice = "Copyright (C) The Android Open Source Project",
            fullLicenseText = """
                Apache License, Version 2.0

                Copyright (C) The Android Open Source Project

                Licensed under the Apache License, Version 2.0 (the "License");
                you may not use this file except in compliance with the License.
                You may obtain a copy of the License at

                    http://www.apache.org/licenses/LICENSE-2.0

                Unless required by applicable law or agreed to in writing, software
                distributed under the License is distributed on an "AS IS" BASIS,
                WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
                See the License for the specific language governing permissions and
                limitations under the License.
            """.trimIndent()
        ),
        OpenSourceLicenseItem(
            name = "Kotlin & Coroutines",
            author = "JetBrains s.r.o.",
            licenseType = "Apache License 2.0",
            icon = PhosphorIcons.Clock,
            description = "Kotlin standard library and kotlinx.coroutines powering reactive state streams and non-blocking I/O.",
            copyrightNotice = "Copyright 2010-2024 JetBrains s.r.o. and Kotlin Coroutines contributors.",
            fullLicenseText = """
                Apache License, Version 2.0

                Copyright 2010-2024 JetBrains s.r.o. and Kotlin Coroutines contributors.

                Licensed under the Apache License, Version 2.0 (the "License");
                you may not use this file except in compliance with the License.
                You may obtain a copy of the License at

                    http://www.apache.org/licenses/LICENSE-2.0

                Unless required by applicable law or agreed to in writing, software
                distributed under the License is distributed on an "AS IS" BASIS,
                WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
                See the License for the specific language governing permissions and
                limitations under the License.
            """.trimIndent()
        )
    )
}
