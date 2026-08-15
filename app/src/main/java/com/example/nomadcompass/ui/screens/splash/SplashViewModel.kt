package com.example.nomadcompass.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nomadcompass.domain.repository.CountryRepository
import com.example.nomadcompass.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SplashUiState(
    val progress: Int = 0,
    val statusMessage: String = "Initializing...",
    val isComplete: Boolean = false,
    val hasProfile: Boolean = false,
)

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val countryRepository: CountryRepository,
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    private val messages = listOf(
        "Seeding offline database...",
        "Mapping terrain clusters...",
        "Indexing safety protocols...",
        "Optimizing navigation mesh...",
        "Finalizing local environment..."
    )

    init {
        startHydration()
    }

    private fun startHydration() {
        viewModelScope.launch {
            // Seed DB asynchronously
            countryRepository.seedIfNeeded()
            val hasProfile = profileRepository.hasProfile()

            // Smooth progress loading matching splash animation
            for (p in 1..100) {
                delay(5)
                val msgIndex = (p / 25).coerceIn(0, messages.size - 1)
                _uiState.value = _uiState.value.copy(
                    progress = p,
                    statusMessage = if (p == 100) "Sync Complete" else messages[msgIndex]
                )
            }

            delay(100)
            _uiState.value = _uiState.value.copy(
                isComplete = true,
                hasProfile = hasProfile
            )
        }
    }
}
