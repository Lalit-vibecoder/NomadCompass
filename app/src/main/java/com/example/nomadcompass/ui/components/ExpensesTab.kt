package com.example.nomadcompass.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nomadcompass.data.local.entity.ExpenseCategory
import com.example.nomadcompass.domain.model.Expense
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExpensesTab(
    expenses: List<Expense>,
    totalSpentHome: Double,
    tripBudgetHome: Double,
    homeCurrencyCode: String,
    tripDestinationCurrencyCode: String,
    onAddExpense: (title: String, amountLocal: Double, currencyCode: String, category: ExpenseCategory, notes: String) -> Unit,
    onDeleteExpense: (Long) -> Unit,
    onCalculateLivePreview: (amountLocal: Double, currencyCode: String, callback: (Double, Boolean) -> Unit) -> Unit,
) {
    var isLogSheetOpen by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Budget Summary Card
            item(key = "budget_summary_card") {
                BudgetSummaryCard(
                    totalSpentHome = totalSpentHome,
                    tripBudgetHome = tripBudgetHome,
                    homeCurrencyCode = homeCurrencyCode,
                    hasUnconverted = expenses.any { it.isUnconverted }
                )
            }

            // 2. Ledger Header
            item(key = "ledger_header") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "EXPENSE LEDGER (${expenses.size})",
                            style = MaterialTheme.typography.labelLarge,
                            color = Primary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Chronological trip transactions & multi-currency logs",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant
                        )
                    }
                }
            }

            // 3. Ledger Items / Empty State
            if (expenses.isEmpty()) {
                item(key = "empty_ledger") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Receipt,
                                contentDescription = null,
                                tint = OnSurfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.size(52.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No Expenses Logged Yet",
                                style = MaterialTheme.typography.titleMedium,
                                color = OnSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap the '+' button below to log food, stay, or transport expenses.",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(expenses, key = { it.id }) { expense ->
                    ExpenseLedgerRow(
                        expense = expense,
                        homeCurrencyCode = homeCurrencyCode,
                        onDelete = { onDeleteExpense(expense.id) }
                    )
                }
            }
        }

        // Quick-Add FAB at Bottom-Right
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 12.dp, end = 12.dp)
                .size(58.dp)
                .clip(CircleShape)
                .background(PrimaryContainer)
                .border(1.5.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                .clickable { isLogSheetOpen = true },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Log Expense",
                tint = OnPrimary,
                modifier = Modifier.size(30.dp)
            )
        }
    }

    // Quick-Add Log Expense Bottom Sheet
    if (isLogSheetOpen) {
        LogExpenseBottomSheet(
            defaultCurrencyCode = tripDestinationCurrencyCode.ifBlank { homeCurrencyCode },
            homeCurrencyCode = homeCurrencyCode,
            onDismiss = { isLogSheetOpen = false },
            onSave = { title, amount, currency, category, notes ->
                onAddExpense(title, amount, currency, category, notes)
                isLogSheetOpen = false
            },
            onCalculatePreview = onCalculateLivePreview
        )
    }
}

@Composable
private fun BudgetSummaryCard(
    totalSpentHome: Double,
    tripBudgetHome: Double,
    homeCurrencyCode: String,
    hasUnconverted: Boolean,
) {
    val progress = if (tripBudgetHome > 0) (totalSpentHome / tripBudgetHome).toFloat().coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "budgetProgress"
    )

    val remaining = (tripBudgetHome - totalSpentHome).coerceAtLeast(0.0)
    val isOverBudget = totalSpentHome > tripBudgetHome && tripBudgetHome > 0

    val progressColor = when {
        isOverBudget -> Color(0xFFEF5350)
        progress >= 0.8f -> Color(0xFFFFB74D)
        else -> Primary
    }

    ClayCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 20.dp,
        backgroundColor = SurfaceContainerLow
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Trip Budget Tracker",
                            style = MaterialTheme.typography.titleMedium,
                            color = OnSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Home Currency ($homeCurrencyCode)",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant
                        )
                    }
                }

                // Remaining Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isOverBudget) Color(0xFFEF5350).copy(alpha = 0.15f)
                            else Primary.copy(alpha = 0.12f)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isOverBudget) "Over by ${formatAmount(totalSpentHome - tripBudgetHome, homeCurrencyCode)}"
                        else "${formatAmount(remaining, homeCurrencyCode)} left",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isOverBudget) Color(0xFFEF5350) else Primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Amounts Display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "TOTAL SPENT",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatAmount(totalSpentHome, homeCurrencyCode),
                        style = MaterialTheme.typography.headlineMedium,
                        color = if (isOverBudget) Color(0xFFEF5350) else OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "TRIP BUDGET",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatAmount(tripBudgetHome, homeCurrencyCode),
                        style = MaterialTheme.typography.titleLarge,
                        color = OnSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Animated Visual Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(CircleShape)
                    .background(SurfaceContainerHigh)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(progressColor, progressColor.copy(alpha = 0.8f))
                            )
                        )
                )
            }

            // Unconverted notice if any
            if (hasUnconverted) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFFB74D).copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFFFB74D),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Some expenses use 1:1 fallback rate (offline exchange rate unavailable).",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFFFB74D),
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ExpenseLedgerRow(
    expense: Expense,
    homeCurrencyCode: String,
    onDelete: () -> Unit,
) {
    val (icon, categoryBg, categoryIconColor) = getCategoryStyle(expense.category)

    ClayCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 16.dp,
        backgroundColor = SurfaceContainerLow
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Category Icon + Title/Date
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(categoryBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = expense.category.displayName,
                        tint = categoryIconColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = expense.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = OnSurface,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (expense.isUnconverted) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFFFB74D).copy(alpha = 0.2f))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "UNCONVERTED",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFFFB74D),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = expense.category.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant
                        )
                        Text(
                            text = " • ",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant
                        )
                        Text(
                            text = formatDate(expense.date),
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant
                        )
                    }

                    if (expense.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = expense.notes,
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant.copy(alpha = 0.8f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right: Local & Home Amount + Delete Button
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    // Local Amount (e.g. 1,500 JPY)
                    Text(
                        text = "${formatNumber(expense.amountLocal)} ${expense.currencyCode}",
                        style = MaterialTheme.typography.titleMedium,
                        color = OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                    // Converted Home Amount (e.g. ~$9.68 USD)
                    Text(
                        text = "~${formatAmount(expense.amountHome, homeCurrencyCode)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Expense",
                        tint = OnSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun LogExpenseBottomSheet(
    defaultCurrencyCode: String,
    homeCurrencyCode: String,
    onDismiss: () -> Unit,
    onSave: (title: String, amount: Double, currency: String, category: ExpenseCategory, notes: String) -> Unit,
    onCalculatePreview: (amountLocal: Double, currencyCode: String, callback: (Double, Boolean) -> Unit) -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var selectedCurrency by remember { mutableStateOf(defaultCurrencyCode) }
    var selectedCategory by remember { mutableStateOf(ExpenseCategory.FOOD) }
    var notes by remember { mutableStateOf("") }

    var previewAmountHome by remember { mutableStateOf(0.0) }
    var isPreviewUnconverted by remember { mutableStateOf(false) }

    var isCurrencyDropdownOpen by remember { mutableStateOf(false) }
    val availableCurrencies = remember {
        listOf("USD", "EUR", "GBP", "JPY", "AUD", "CAD", "CHF", "CNY", "INR", "HKD", "SGD", "THB", "IDR", "MXN", "BRL", "KRW").distinct()
    }

    // Trigger live preview calculation whenever amount or currency changes
    LaunchedEffect(amountText, selectedCurrency) {
        val amt = amountText.toDoubleOrNull() ?: 0.0
        if (amt > 0) {
            onCalculatePreview(amt, selectedCurrency) { homeAmt, unconverted ->
                previewAmountHome = homeAmt
                isPreviewUnconverted = unconverted
            }
        } else {
            previewAmountHome = 0.0
            isPreviewUnconverted = false
        }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .padding(bottom = 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Log New Trip Expense",
                    style = MaterialTheme.typography.titleLarge,
                    color = OnSurface,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = OnSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Expense Title TextField
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Expense Title (e.g. Subway Ticket, Ramen Dinner)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedContainerColor = SurfaceContainerLow,
                    unfocusedContainerColor = SurfaceContainerLow,
                    focusedTextColor = OnSurface,
                    unfocusedTextColor = OnSurface,
                    focusedLabelColor = Primary,
                    unfocusedLabelColor = OnSurfaceVariant,
                    cursorColor = Primary,
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Amount & Currency Selector Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount") },
                    modifier = Modifier.weight(1.4f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedContainerColor = SurfaceContainerLow,
                        unfocusedContainerColor = SurfaceContainerLow,
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface,
                        focusedLabelColor = Primary,
                        unfocusedLabelColor = OnSurfaceVariant,
                        cursorColor = Primary,
                    )
                )

                // Currency Dropdown Button
                Box(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceContainerLow)
                            .border(1.dp, Primary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .clickable { isCurrencyDropdownOpen = true }
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedCurrency,
                                style = MaterialTheme.typography.titleMedium,
                                color = Primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "▼",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariant
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = isCurrencyDropdownOpen,
                        onDismissRequest = { isCurrencyDropdownOpen = false }
                    ) {
                        availableCurrencies.forEach { code ->
                            DropdownMenuItem(
                                text = { Text(text = code, fontWeight = if (code == selectedCurrency) FontWeight.Bold else FontWeight.Normal) },
                                onClick = {
                                    selectedCurrency = code
                                    isCurrencyDropdownOpen = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Live Conversion Preview Banner
            val amtNumber = amountText.toDoubleOrNull() ?: 0.0
            if (amtNumber > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isPreviewUnconverted) Color(0xFFFFB74D).copy(alpha = 0.15f)
                            else Primary.copy(alpha = 0.12f)
                        )
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isPreviewUnconverted) Icons.Default.Warning else Icons.Default.Info,
                                contentDescription = null,
                                tint = if (isPreviewUnconverted) Color(0xFFFFB74D) else Primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isPreviewUnconverted) "Offline fallback (1:1 rate)"
                                else "Converts to Home Currency:",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant
                            )
                        }

                        Text(
                            text = "~${formatAmount(previewAmountHome, homeCurrencyCode)}",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isPreviewUnconverted) Color(0xFFFFB74D) else Primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Category Chips Selection
            Text(
                text = "Category",
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                ExpenseCategory.entries.forEach { cat ->
                    val isSelected = cat == selectedCategory
                    val (catIcon, catBg, catColor) = getCategoryStyle(cat)

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) Primary else SurfaceContainerLow)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) Primary else OnSurfaceVariant.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = cat.emoji, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = cat.displayName,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) OnPrimary else OnSurface,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Notes Optional TextField
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes / Description (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedContainerColor = SurfaceContainerLow,
                    unfocusedContainerColor = SurfaceContainerLow,
                    focusedTextColor = OnSurface,
                    unfocusedTextColor = OnSurface,
                    focusedLabelColor = Primary,
                    unfocusedLabelColor = OnSurfaceVariant,
                    cursorColor = Primary,
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Save Expense Button
            val isFormValid = title.isNotBlank() && (amountText.toDoubleOrNull() ?: 0.0) > 0.0
            ClayButton(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: 0.0
                    if (isFormValid) {
                        onSave(title, amt, selectedCurrency, selectedCategory, notes)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isFormValid
            ) {
                Text(
                    text = "Save Expense",
                    style = MaterialTheme.typography.labelLarge,
                    color = com.example.nomadcompass.ui.theme.OnPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Helpers
private fun getCategoryStyle(category: ExpenseCategory): Triple<ImageVector, Color, Color> {
    return when (category) {
        ExpenseCategory.FOOD -> Triple(Icons.Default.Restaurant, Color(0xFFFF7043).copy(alpha = 0.18f), Color(0xFFFF7043))
        ExpenseCategory.TRANSPORT -> Triple(Icons.Default.DirectionsCar, Color(0xFF42A5F5).copy(alpha = 0.18f), Color(0xFF1E88E5))
        ExpenseCategory.LODGING -> Triple(Icons.Default.Hotel, Color(0xFFAB47BC).copy(alpha = 0.18f), Color(0xFFAB47BC))
        ExpenseCategory.WORK -> Triple(Icons.Default.Work, Color(0xFF26A69A).copy(alpha = 0.18f), Color(0xFF26A69A))
        ExpenseCategory.MISC -> Triple(Icons.Default.ShoppingBag, Color(0xFFFFA726).copy(alpha = 0.18f), Color(0xFFFFA726))
    }
}

private fun formatAmount(amount: Double, currencyCode: String): String {
    val symbol = when (currencyCode.uppercase()) {
        "USD" -> "$"
        "EUR" -> "€"
        "GBP" -> "£"
        "JPY" -> "¥"
        "INR" -> "₹"
        else -> "$currencyCode "
    }
    return "$symbol${String.format(Locale.US, "%,.2f", amount)}"
}

private fun formatNumber(amount: Double): String {
    return String.format(Locale.US, "%,.2f", amount)
}

private fun formatDate(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        sdf.format(Date(timestamp))
    } catch (_: Exception) {
        "Today"
    }
}
