package com.example.nomadcompass.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nomadcompass.data.local.entity.CountryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CountryDao {

    @Query("SELECT * FROM countries ORDER BY commonName ASC")
    fun getAll(): Flow<List<CountryEntity>>

    @Query("SELECT * FROM countries WHERE isFavorite = 1 ORDER BY commonName ASC")
    fun getFavorites(): Flow<List<CountryEntity>>

    @Query(
        """
        SELECT * FROM countries 
        WHERE commonName LIKE '%' || :query || '%' 
           OR officialName LIKE '%' || :query || '%'
           OR capital LIKE '%' || :query || '%'
           OR region LIKE '%' || :query || '%'
        ORDER BY commonName ASC
        """
    )
    fun search(query: String): Flow<List<CountryEntity>>

    @Query("SELECT * FROM countries WHERE cca3 = :cca3 LIMIT 1")
    suspend fun getByCode(cca3: String): CountryEntity?

    @Query("SELECT * FROM countries WHERE cca3 IN (:cca3s)")
    suspend fun getByCodes(cca3s: List<String>): List<CountryEntity>

    @Query("UPDATE countries SET isFavorite = NOT isFavorite WHERE cca3 = :cca3")
    suspend fun toggleFavorite(cca3: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(countries: List<CountryEntity>)

    @Query("SELECT COUNT(*) FROM countries")
    suspend fun count(): Int
}
