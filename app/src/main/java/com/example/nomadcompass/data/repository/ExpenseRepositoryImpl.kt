package com.example.nomadcompass.data.repository

import com.example.nomadcompass.data.local.dao.ExpenseDao
import com.example.nomadcompass.data.local.entity.ExpenseCategory
import com.example.nomadcompass.data.local.entity.ExpenseEntity
import com.example.nomadcompass.domain.model.Expense
import com.example.nomadcompass.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao,
) : ExpenseRepository {

    override fun getExpensesForTrip(tripId: Int): Flow<List<Expense>> {
        return expenseDao.getExpensesForTrip(tripId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTotalSpentHome(tripId: Int): Flow<Double> {
        return expenseDao.getTotalSpentHome(tripId).map { it ?: 0.0 }
    }

    override suspend fun addExpense(expense: Expense): Long {
        return expenseDao.insertExpense(expense.toEntity())
    }

    override suspend fun deleteExpense(id: Long) {
        expenseDao.deleteExpense(id)
    }

    private fun ExpenseEntity.toDomain() = Expense(
        id = id,
        tripId = tripId,
        title = title,
        amountLocal = amountLocal,
        currencyCode = currencyCode,
        amountHome = amountHome,
        isUnconverted = isUnconverted,
        category = ExpenseCategory.fromString(category),
        date = date,
        notes = notes,
        paymentMethod = paymentMethod,
        receiptPath = receiptPath,
    )

    private fun Expense.toEntity() = ExpenseEntity(
        id = id,
        tripId = tripId,
        title = title,
        amountLocal = amountLocal,
        currencyCode = currencyCode,
        amountHome = amountHome,
        isUnconverted = isUnconverted,
        category = category.name,
        date = date,
        notes = notes,
        paymentMethod = paymentMethod,
        receiptPath = receiptPath,
    )
}
