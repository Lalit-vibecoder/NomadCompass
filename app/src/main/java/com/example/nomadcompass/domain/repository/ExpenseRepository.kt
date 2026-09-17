package com.example.nomadcompass.domain.repository

import com.example.nomadcompass.domain.model.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun getExpensesForTrip(tripId: Int): Flow<List<Expense>>
    fun getTotalSpentHome(tripId: Int): Flow<Double>
    suspend fun addExpense(expense: Expense): Long
    suspend fun updateExpense(expense: Expense)
    suspend fun deleteExpense(id: Long)
}
