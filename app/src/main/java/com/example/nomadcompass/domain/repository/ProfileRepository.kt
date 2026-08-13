package com.example.nomadcompass.domain.repository

import com.example.nomadcompass.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getProfile(): Flow<UserProfile?>
    suspend fun saveProfile(profile: UserProfile)
    suspend fun hasProfile(): Boolean
}
