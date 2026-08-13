package com.example.nomadcompass.domain.usecase

import com.example.nomadcompass.domain.repository.CountryRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: CountryRepository,
) {
    suspend operator fun invoke(cca3: String) {
        repository.toggleFavorite(cca3)
    }
}
