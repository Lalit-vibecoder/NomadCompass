package com.example.nomadcompass.domain.model

import com.example.nomadcompass.data.local.entity.ExpenseCategory

data class Expense(
    val id: Long = 0,
    val tripId: Int,
    val title: String,
    val amountLocal: Double,
    val currencyCode: String,
    val amountHome: Double,
    val isUnconverted: Boolean = false,
    val category: ExpenseCategory,
    val date: Long,
    val notes: String = "",
    val paymentMethod: String = "Credit Card",
    val receiptPath: String? = null,
)
