package com.example.nomadcompass.domain.usecase

import com.example.nomadcompass.domain.model.UserProfile
import com.example.nomadcompass.domain.repository.ProfileRepository
import javax.inject.Inject

class SaveProfileUseCase @Inject constructor(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke(profile: UserProfile) {
        repository.saveProfile(profile)
    }
}
