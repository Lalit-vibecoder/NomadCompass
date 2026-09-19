package com.example.nomadcompass.ui.components

import android.app.DatePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.ui.draw.blur
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import com.example.nomadcompass.ui.theme.icons.PhosphorIcons
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.nomadcompass.data.local.entity.ExpenseCategory
import com.example.nomadcompass.domain.model.Expense
import com.example.nomadcompass.ui.theme.ActionPrimary
import com.example.nomadcompass.ui.theme.LocalThemeController
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
import com.example.nomadcompass.util.CurrencyFormatter
import com.example.nomadcompass.util.FileStorageHelper
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ExpensesTab(
    expenses: List<Expense>,
    totalSpentHome: Double,
    tripBudgetHome: Double,
    homeCurrencyCode: String,
    tripDestinationCurrencyCode: String,
    onAddExpense: (
        title: String,
        amountLocal: Double,
        currencyCode: String,
        category: ExpenseCategory,
        notes: String,
        date: Long,
        paymentMethod: String,
        receiptPath: String?
    ) -> Unit,
    onEditExpense: (
        id: Long,
        title: String,
        amountLocal: Double,
        currencyCode: String,
        category: ExpenseCategory,
        notes: String,
        date: Long,
        paymentMethod: String,
        receiptPath: String?
    ) -> Unit = { _, _, _, _, _, _, _, _, _ -> },
    onDeleteExpense: (Long) -> Unit,
    onCalculateLivePreview: (amountLocal: Double, currencyCode: String, callback: (Double, Boolean) -> Unit) -> Unit,
) {
    var isLogSheetOpen by remember { mutableStateOf(false) }
    var editingExpense by remember { mutableStateOf<Expense?>(null) }
    val logSheetBlur by animateDpAsState(
        targetValue = if (isLogSheetOpen || editingExpense != null) 18.dp else 0.dp,
        label = "expenses_log_sheet_blur"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .blur(logSheetBlur)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds(),
            contentPadding = PaddingValues(bottom = 110.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Clean Budget Summary Card showing spent, budget, and remaining balances without extra text
            item(key = "budget_summary_card") {
                BudgetSummaryCard(
                    totalSpentHome = totalSpentHome,
                    tripBudgetHome = tripBudgetHome,
                    homeCurrencyCode = homeCurrencyCode,
                    hasUnconverted = expenses.any { it.isUnconverted }
                )
            }

            // 2. Recent Transactions Header (clean, without extra subtitle text)
            item(key = "recent_transactions_header") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Transactions",
                        style = MaterialTheme.typography.titleMedium,
                        color = OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                    if (expenses.isNotEmpty()) {
                        Text(
                            text = "${expenses.size}",
                            style = MaterialTheme.typography.labelMedium,
                            color = Primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 3. Transactions List or Minimalist Empty State
            if (expenses.isEmpty()) {
                item(key = "empty_expenses") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        ClayButton(
                            onClick = { isLogSheetOpen = true },
                            modifier = Modifier.wrapContentWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = PhosphorIcons.Plus,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Log your first expense",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            } else {
                items(expenses, key = { it.id }) { expense ->
                    ExpenseLedgerRow(
                        expense = expense,
                        homeCurrencyCode = homeCurrencyCode,
                        onClick = { editingExpense = expense },
                        onDelete = { onDeleteExpense(expense.id) },
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }

        // Quick-Add FAB at Bottom-Right with #FF2E63 ActionPrimary styling
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 12.dp, end = 12.dp)
                .size(58.dp)
                .clip(CircleShape)
                .background(ActionPrimary)
                .border(1.5.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                .clickable { isLogSheetOpen = true },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = PhosphorIcons.Plus,
                contentDescription = "Log Expense",
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }
    }

    // Quick-Add Log Expense Bottom Sheet
    if (isLogSheetOpen) {
        LogExpenseBottomSheet(
            initialExpense = null,
            defaultCurrencyCode = tripDestinationCurrencyCode.ifBlank { homeCurrencyCode },
            homeCurrencyCode = homeCurrencyCode,
            onDismiss = { isLogSheetOpen = false },
            onSave = { title, amount, currency, category, notes, date, paymentMethod, receiptPath ->
                onAddExpense(title, amount, currency, category, notes, date, paymentMethod, receiptPath)
                isLogSheetOpen = false
            },
            onCalculatePreview = onCalculateLivePreview
        )
    }

    // Edit Expense Bottom Sheet for Tappable Cards
    editingExpense?.let { targetExpense ->
        LogExpenseBottomSheet(
            initialExpense = targetExpense,
            defaultCurrencyCode = targetExpense.currencyCode.ifBlank { tripDestinationCurrencyCode.ifBlank { homeCurrencyCode } },
            homeCurrencyCode = homeCurrencyCode,
            onDismiss = { editingExpense = null },
            onSave = { title, amount, currency, category, notes, date, paymentMethod, receiptPath ->
                onEditExpense(targetExpense.id, title, amount, currency, category, notes, date, paymentMethod, receiptPath)
                editingExpense = null
            },
            onCalculatePreview = onCalculateLivePreview
        )
    }
}

/**
 * Modern Visual Insights Budget Card with Progress Ring / Chart
 */
@Composable
private fun BudgetSummaryCard(
    totalSpentHome: Double,
    tripBudgetHome: Double,
    homeCurrencyCode: String,
    hasUnconverted: Boolean,
) {
    val isDark = LocalThemeController.current.isDarkMode
    val progress = if (tripBudgetHome > 0) (totalSpentHome / tripBudgetHome).toFloat().coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "budgetProgress"
    )

    val remaining = tripBudgetHome - totalSpentHome
    val isOverBudget = totalSpentHome > tripBudgetHome && tripBudgetHome > 0
    val percentSpent = if (tripBudgetHome > 0) Math.round((totalSpentHome / tripBudgetHome) * 100).toInt() else 0

    val progressRingColor = when {
        isOverBudget -> Color(0xFFEF5350)
        progress >= 0.85f -> Color(0xFFFFB74D)
        else -> Primary
    }

    ClayCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        backgroundColor = SurfaceContainerLow
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Clean visual indicators showing spent, budget, and remaining balances without extra text
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Circular Progress Ring Chart
                Box(
                    modifier = Modifier.size(96.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val trackColor = if (isDark) Color(0xFF2C3E37) else Color(0xFFE0EAE6)
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 9.dp.toPx()
                        // Track Arc
                        drawArc(
                            color = trackColor,
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                        // Progress Arc
                        drawArc(
                            color = progressRingColor,
                            startAngle = -90f,
                            sweepAngle = (animatedProgress * 360f).coerceAtLeast(1f),
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    // Center percentage metric
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$percentSpent%",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            color = if (isOverBudget) Color(0xFFEF5350) else OnSurface
                        )
                        Text(
                            text = if (isOverBudget) "Over" else "Spent",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = OnSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.width(20.dp))

                // Clean Balance Metrics Column: Spent, Budget, Remaining
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Total Spent Indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .clip(CircleShape)
                                    .background(progressRingColor)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Spent",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurfaceVariant
                            )
                        }
                        Text(
                            text = formatAmount(totalSpentHome, homeCurrencyCode),
                            style = MaterialTheme.typography.titleSmall,
                            color = if (isOverBudget) Color(0xFFEF5350) else OnSurface,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Total Budget Indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .clip(CircleShape)
                                    .background(OnSurfaceVariant.copy(alpha = 0.5f))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Budget",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurfaceVariant
                            )
                        }
                        Text(
                            text = formatAmount(tripBudgetHome, homeCurrencyCode),
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Remaining Balance Indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .clip(CircleShape)
                                    .background(if (isOverBudget) Color(0xFFEF5350) else Primary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Remaining",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurfaceVariant
                            )
                        }
                        Text(
                            text = formatAmount(remaining, homeCurrencyCode),
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isOverBudget) Color(0xFFEF5350) else Primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (hasUnconverted) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFFFB74D).copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Icon(
                        imageVector = PhosphorIcons.WarningCircle,
                        contentDescription = null,
                        tint = Color(0xFFFFB74D),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "1:1 rate used",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFFFB74D),
                        fontSize = 9.sp
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
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (icon, categoryBg, categoryIconColor) = getCategoryStyle(expense.category)

    ClayCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        cornerRadius = 18.dp,
        backgroundColor = SurfaceContainerLow
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Category Icon + Title/Date/Payment Method
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

                    // Date & Category & Payment Method Badges
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = formatDate(expense.date),
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant
                        )
                        // Payment Method Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Primary.copy(alpha = 0.10f))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = expense.paymentMethod,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = Primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Receipt attached badge if any
                        if (!expense.receiptPath.isNullOrBlank()) {
                            Text(
                                text = "•",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariant
                            )
                            Icon(
                                imageVector = PhosphorIcons.Paperclip,
                                contentDescription = "Receipt attached",
                                tint = Primary,
                                modifier = Modifier.size(13.dp)
                            )
                        }
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
                    Text(
                        text = "${formatNumber(expense.amountLocal)} ${expense.currencyCode}",
                        style = MaterialTheme.typography.titleMedium,
                        color = OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "~${formatAmount(expense.amountHome, homeCurrencyCode)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = PhosphorIcons.Trash,
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
    initialExpense: Expense? = null,
    defaultCurrencyCode: String,
    homeCurrencyCode: String,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        amount: Double,
        currency: String,
        category: ExpenseCategory,
        notes: String,
        date: Long,
        paymentMethod: String,
        receiptPath: String?
    ) -> Unit,
    onCalculatePreview: (amountLocal: Double, currencyCode: String, callback: (Double, Boolean) -> Unit) -> Unit,
) {
    val isEditing = initialExpense != null
    val context = LocalContext.current
    var title by remember { mutableStateOf(initialExpense?.title ?: "") }
    var amountText by remember { mutableStateOf(if (initialExpense != null && initialExpense.amountLocal > 0) formatPlainNumber(initialExpense.amountLocal) else "") }
    var selectedCurrency by remember { mutableStateOf(initialExpense?.currencyCode ?: defaultCurrencyCode) }
    var selectedCategory by remember { mutableStateOf(initialExpense?.category ?: ExpenseCategory.FOOD) }
    var notes by remember { mutableStateOf(initialExpense?.notes ?: "") }
    var selectedDateMillis by remember { mutableLongStateOf(initialExpense?.date ?: System.currentTimeMillis()) }
    var selectedPaymentMethod by remember { mutableStateOf(initialExpense?.paymentMethod ?: "Credit Card") }
    var attachedReceiptPath by remember { mutableStateOf<String?>(initialExpense?.receiptPath) }
    var attachedReceiptName by remember { mutableStateOf<String?>(if (initialExpense?.receiptPath != null) "Receipt Attached" else null) }

    var previewAmountHome by remember { mutableStateOf(0.0) }
    var isPreviewUnconverted by remember { mutableStateOf(false) }

    var isCurrencyDropdownOpen by remember { mutableStateOf(false) }
    var isPaymentDropdownOpen by remember { mutableStateOf(false) }
    var isSavingSuccess by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val availableCurrencies = remember {
        listOf("USD", "EUR", "GBP", "JPY", "AUD", "CAD", "CHF", "CNY", "INR", "HKD", "SGD", "THB", "IDR", "MXN", "BRL", "KRW").distinct()
    }
    val paymentMethods = listOf("Credit Card", "Debit Card", "Cash", "UPI")

    // Receipt File Picker Launcher
    val receiptPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val saved = FileStorageHelper.saveUriToInternalStorage(context, uri, "expense_receipts")
            if (saved != null) {
                attachedReceiptPath = saved.filePath
                attachedReceiptName = saved.title
            }
        }
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

    FrostedGlassDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth(0.94f)
            .fillMaxHeight(0.88f)
            .padding(vertical = 16.dp),
    ) {
        Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header with glowing frosted icon badge and close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Primary.copy(alpha = 0.15f))
                                .border(1.dp, Primary.copy(alpha = 0.35f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isEditing) PhosphorIcons.PencilSimple else PhosphorIcons.Receipt,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isEditing) "Edit Expense" else "Log New Expense",
                                style = MaterialTheme.typography.titleLarge,
                                color = OnSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isEditing) "Modify expense details" else "Record spending & convert live",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant
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

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

            // Quick Category Pills
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "QUICK CATEGORIES",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

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
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) Primary else SurfaceContainerLow)
                                    .border(
                                        1.dp,
                                        if (isSelected) Primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedCategory = cat }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = catIcon,
                                        contentDescription = cat.displayName,
                                        tint = if (isSelected) OnPrimary else catColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = cat.displayName,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (isSelected) OnPrimary else OnSurface,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Expense Title TextField - Clean 12dp Rounded Corners
            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Expense Title (e.g. Subway Ticket, Ramen Dinner)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                        focusedContainerColor = SurfaceContainerLow,
                        unfocusedContainerColor = SurfaceContainerLow,
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface,
                        focusedLabelColor = Primary,
                        unfocusedLabelColor = OnSurfaceVariant,
                        cursorColor = Primary,
                    )
                )
            }

            // Amount & Currency Selector Row
            item {
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
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
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
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceContainerLow)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                .clickable { isCurrencyDropdownOpen = true }
                                .padding(horizontal = 14.dp),
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
            }

            // Live Conversion Preview Banner
            val amtNumber = amountText.toDoubleOrNull() ?: 0.0
            if (amtNumber > 0) {
                item {
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
                                    imageVector = if (isPreviewUnconverted) PhosphorIcons.WarningCircle else PhosphorIcons.Info,
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

                            AnimatedContent(
                                targetState = previewAmountHome,
                                transitionSpec = {
                                    (slideInVertically { height -> height / 3 } + fadeIn(animationSpec = tween(150))) togetherWith
                                        (slideOutVertically { height -> -height / 3 } + fadeOut(animationSpec = tween(150)))
                                },
                                label = "conversion_preview_amount"
                            ) { convertedAmt ->
                                Text(
                                    text = "~${formatAmount(convertedAmt, homeCurrencyCode)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (isPreviewUnconverted) Color(0xFFFFB74D) else Primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Date Picker Field & Payment Method Selector Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Date Picker Button
                    val cal = remember(selectedDateMillis) {
                        Calendar.getInstance().apply { timeInMillis = selectedDateMillis }
                    }
                    val formattedDate = remember(selectedDateMillis) {
                        SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(selectedDateMillis))
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "EXPENSE DATE",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceContainerLow)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                .clickable {
                                    DatePickerDialog(
                                        context,
                                        { _, y, m, d ->
                                            val newCal = Calendar.getInstance().apply {
                                                set(y, m, d, 12, 0, 0)
                                            }
                                            selectedDateMillis = newCal.timeInMillis
                                        },
                                        cal.get(Calendar.YEAR),
                                        cal.get(Calendar.MONTH),
                                        cal.get(Calendar.DAY_OF_MONTH)
                                    ).apply {
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                                            window?.addFlags(android.view.WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
                                            window?.attributes = window?.attributes?.apply { blurBehindRadius = 32 }
                                        }
                                    }.show()
                                }
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = PhosphorIcons.CalendarBlank,
                                        contentDescription = null,
                                        tint = Primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = formattedDate,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = OnSurface,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    // Payment Method Dropdown
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "PAYMENT METHOD",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceContainerLow)
                                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                    .clickable { isPaymentDropdownOpen = true }
                                    .padding(horizontal = 12.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = PhosphorIcons.CreditCard,
                                            contentDescription = null,
                                            tint = Primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = selectedPaymentMethod,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = OnSurface,
                                            fontWeight = FontWeight.Medium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Text(
                                        text = "▼",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = OnSurfaceVariant
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = isPaymentDropdownOpen,
                                onDismissRequest = { isPaymentDropdownOpen = false }
                            ) {
                                paymentMethods.forEach { method ->
                                    DropdownMenuItem(
                                        text = { Text(method, fontWeight = if (method == selectedPaymentMethod) FontWeight.Bold else FontWeight.Normal) },
                                        onClick = {
                                            selectedPaymentMethod = method
                                            isPaymentDropdownOpen = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Receipt Attachment Section
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "RECEIPT ATTACHMENT",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )

                    if (attachedReceiptPath == null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Primary.copy(alpha = 0.08f))
                                .border(1.dp, Primary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .clickable { receiptPickerLauncher.launch("*/*") }
                                .padding(vertical = 14.dp, horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = PhosphorIcons.Paperclip,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Upload Receipt (Image or PDF)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    } else {
                        // Attached Receipt Chip with Preview
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Primary.copy(alpha = 0.12f))
                                .border(1.dp, Primary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                val isPdf = attachedReceiptPath!!.lowercase().endsWith(".pdf")
                                if (isPdf) {
                                    Icon(
                                        imageVector = PhosphorIcons.FilePdf,
                                        contentDescription = null,
                                        tint = Color(0xFFEF5350),
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else {
                                    AsyncImage(
                                        model = File(attachedReceiptPath!!),
                                        contentDescription = "Receipt preview",
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(RoundedCornerShape(4.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = attachedReceiptName ?: "Receipt Attached",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnSurface,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            IconButton(
                                onClick = {
                                    attachedReceiptPath = null
                                    attachedReceiptName = null
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = PhosphorIcons.X,
                                    contentDescription = "Remove receipt",
                                    tint = OnSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Notes Optional TextField
            item {
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / Description (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                        focusedContainerColor = SurfaceContainerLow,
                        unfocusedContainerColor = SurfaceContainerLow,
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface,
                        focusedLabelColor = Primary,
                        unfocusedLabelColor = OnSurfaceVariant,
                        cursorColor = Primary,
                    )
                )
            }

            // Save Expense Button
            item {
                val isFormValid = title.isNotBlank() && (amountText.toDoubleOrNull() ?: 0.0) > 0.0
                ClayButton(
                    onClick = {
                        val amt = amountText.toDoubleOrNull() ?: 0.0
                        if (isFormValid && !isSavingSuccess) {
                            scope.launch {
                                isSavingSuccess = true
                                kotlinx.coroutines.delay(320)
                                onSave(
                                    title,
                                    amt,
                                    selectedCurrency,
                                    selectedCategory,
                                    notes,
                                    selectedDateMillis,
                                    selectedPaymentMethod,
                                    attachedReceiptPath
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isFormValid && !isSavingSuccess
                ) {
                    AnimatedContent(
                        targetState = isSavingSuccess,
                        transitionSpec = {
                            (fadeIn(animationSpec = tween(140)) + slideInVertically { it / 2 }) togetherWith
                                (fadeOut(animationSpec = tween(140)) + slideOutVertically { -it / 2 })
                        },
                        label = "save_expense_btn_anim"
                    ) { success ->
                        if (success) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = PhosphorIcons.CheckCircle,
                                    contentDescription = "Success",
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isEditing) "Expense Updated!" else "Expense Logged!",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color(0xFF4CAF50),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = if (isEditing) PhosphorIcons.CheckCircle else PhosphorIcons.PlusCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isEditing) "Save Changes" else "Save Expense",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
}



// Helpers
private fun getCategoryStyle(category: ExpenseCategory): Triple<ImageVector, Color, Color> {
    return when (category) {
        ExpenseCategory.FOOD -> Triple(PhosphorIcons.ForkKnife, Color(0xFFFF7043).copy(alpha = 0.18f), Color(0xFFFF7043))
        ExpenseCategory.TRANSPORT -> Triple(PhosphorIcons.CarSimple, Color(0xFF42A5F5).copy(alpha = 0.18f), Color(0xFF1E88E5))
        ExpenseCategory.LODGING -> Triple(PhosphorIcons.Bed, Color(0xFFAB47BC).copy(alpha = 0.18f), Color(0xFFAB47BC))
        ExpenseCategory.WORK -> Triple(PhosphorIcons.Briefcase, Color(0xFF26A69A).copy(alpha = 0.18f), Color(0xFF26A69A))
        ExpenseCategory.MISC -> Triple(PhosphorIcons.ShoppingBagOpen, Color(0xFFFFA726).copy(alpha = 0.18f), Color(0xFFFFA726))
    }
}

private fun formatAmount(amount: Double, currencyCode: String): String {
    return CurrencyFormatter.formatAmount(amount, currencyCode)
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

private fun formatPlainNumber(amount: Double): String {
    return if (amount % 1.0 == 0.0) {
        amount.toLong().toString()
    } else {
        String.format(Locale.US, "%.2f", amount)
    }
}
