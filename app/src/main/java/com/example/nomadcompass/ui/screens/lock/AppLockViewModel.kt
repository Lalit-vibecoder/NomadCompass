package com.example.nomadcompass.ui.screens.lock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nomadcompass.domain.model.UserProfile
import com.example.nomadcompass.domain.usecase.GetProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AppLockUiState(
    val profile: UserProfile? = null,
    val pinInput: String = "",
    val isUnlocked: Boolean = false,
    val errorMessage: String? = null,
    val isBiometricAvailable: Boolean = false,
    val shakeTrigger: Int = 0,
)

@HiltViewModel
class AppLockViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppLockUiState())
    val uiState: StateFlow<AppLockUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getProfileUseCase().collect { savedProfile ->
                _uiState.value = _uiState.value.copy(profile = savedProfile)
            }
        }
    }

    fun onDigitEntered(digit: String) {
        val current = _uiState.value.pinInput
        if (current.length < 4) {
            val updated = current + digit
            _uiState.value = _uiState.value.copy(pinInput = updated, errorMessage = null)

            if (updated.length == 4) {
                verifyPin(updated)
            }
        }
    }

    fun onDeleteDigit() {
        val current = _uiState.value.pinInput
        if (current.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(
                pinInput = current.dropLast(1),
                errorMessage = null
            )
        }
    }

    private fun verifyPin(pin: String) {
        val expectedCode = _uiState.value.profile?.accessCode
        if (expectedCode.isNullOrBlank() || pin == expectedCode) {
            _uiState.value = _uiState.value.copy(isUnlocked = true)
        } else {
            _uiState.value = _uiState.value.copy(
                pinInput = "",
                errorMessage = "Incorrect PIN. Please try again.",
                shakeTrigger = _uiState.value.shakeTrigger + 1
            )
        }
    }

    fun onBiometricSuccess() {
        _uiState.value = _uiState.value.copy(isUnlocked = true)
    }

    fun onBiometricError(message: String) {
        _uiState.value = _uiState.value.copy(errorMessage = message)
    }
}
