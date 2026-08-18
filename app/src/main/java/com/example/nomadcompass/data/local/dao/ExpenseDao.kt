package com.example.nomadcompass.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nomadcompass.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Query("SELECT * FROM expenses WHERE tripId = :tripId ORDER BY date DESC, id DESC")
    fun getExpensesForTrip(tripId: Int): Flow<List<ExpenseEntity>>

    @Query("SELECT SUM(amountHome) FROM expenses WHERE tripId = :tripId")
    fun getTotalSpentHome(tripId: Int): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity): Long

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteExpense(id: Long)
}
