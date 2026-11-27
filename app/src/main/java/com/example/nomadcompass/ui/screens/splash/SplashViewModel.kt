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

import kotlinx.coroutines.flow.firstOrNull

data class SplashUiState(
    val progress: Int = 0,
    val statusMessage: String = "Initializing...",
    val isComplete: Boolean = false,
    val hasProfile: Boolean = false,
    val isSecurityLocked: Boolean = false,
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
            val startTime = System.currentTimeMillis()
            val minSplashDurationMs = 4000L

            // Run database hydration concurrently
            val hydrationJob = launch {
                countryRepository.seedIfNeeded()
            }

            // Smoothly animate progress and status messages across the duration
            val stages = listOf(
                Pair(18, "Seeding offline database..."),
                Pair(42, "Mapping terrain clusters..."),
                Pair(65, "Indexing safety protocols..."),
                Pair(84, "Optimizing navigation mesh..."),
                Pair(96, "Finalizing local environment...")
            )

            val stepDurationMs = 650L // 5 * 650ms = 3250ms
            for ((progress, message) in stages) {
                _uiState.value = _uiState.value.copy(
                    progress = progress,
                    statusMessage = message
                )
                delay(stepDurationMs)
            }

            // Ensure database work has finished
            hydrationJob.join()

            val savedProfile = profileRepository.getProfile().firstOrNull()
            val hasProfile = savedProfile != null
            val isSecurityLocked = savedProfile != null && (savedProfile.isBiometricEnabled || savedProfile.accessCode.isNotBlank())

            // Ensure the splash screen stays for at least 4 full seconds
            val elapsed = System.currentTimeMillis() - startTime
            val remaining = minSplashDurationMs - elapsed
            if (remaining > 0) {
                delay(remaining)
            }

            _uiState.value = SplashUiState(
                progress = 100,
                statusMessage = "Sync Complete",
                isComplete = true,
                hasProfile = hasProfile,
                isSecurityLocked = isSecurityLocked
            )
        }
    }
}
