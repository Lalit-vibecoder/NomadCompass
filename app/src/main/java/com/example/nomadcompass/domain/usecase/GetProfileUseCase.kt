package com.example.nomadcompass.domain.usecase

import com.example.nomadcompass.domain.model.UserProfile
import com.example.nomadcompass.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val repository: ProfileRepository,
) {
    operator fun invoke(): Flow<UserProfile?> = repository.getProfile()
}
