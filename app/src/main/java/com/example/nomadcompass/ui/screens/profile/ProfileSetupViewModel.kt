package com.example.nomadcompass.ui.screens.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nomadcompass.domain.model.Country
import com.example.nomadcompass.domain.model.UserProfile
import com.example.nomadcompass.domain.usecase.GetAllCountriesUseCase
import com.example.nomadcompass.domain.usecase.GetProfileUseCase
import com.example.nomadcompass.domain.usecase.SaveProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class ProfileSetupUiState(
    val fullName: String = "",
    val homeCountryCca3: String = "USA",
    val baseCurrencyCode: String = "USD",
    val tempUnit: String = "C",
    val photoUri: String? = null,
    val isSecurityEnabled: Boolean = false,
    val isBiometricEnabled: Boolean = false,
    val accessCode: String = "",
    val themeMode: String = "DARK", // "DARK" or "LIGHT"
    val availableCountries: List<Country> = emptyList(),
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class ProfileSetupViewModel @Inject constructor(
    private val saveProfileUseCase: SaveProfileUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val getAllCountriesUseCase: GetAllCountriesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileSetupUiState())
    val uiState: StateFlow<ProfileSetupUiState> = _uiState.asStateFlow()

    init {
        loadCountries()
        loadExistingProfile()
    }

    private fun loadCountries() {
        viewModelScope.launch {
            getAllCountriesUseCase().collect { list ->
                val sorted = list.sortedBy { it.commonName }
                _uiState.value = _uiState.value.copy(availableCountries = sorted)
            }
        }
    }

    private fun loadExistingProfile() {
        viewModelScope.launch {
            getProfileUseCase().collect { saved ->
                if (saved != null) {
                    val hasSecurity = saved.isBiometricEnabled || saved.accessCode.isNotBlank()
                    _uiState.value = _uiState.value.copy(
                        fullName = saved.userName,
                        homeCountryCca3 = saved.homeCountryCca3,
                        baseCurrencyCode = saved.baseCurrencyCode,
                        tempUnit = saved.tempUnit,
                        photoUri = saved.photoUri,
                        isSecurityEnabled = hasSecurity,
                        isBiometricEnabled = saved.isBiometricEnabled,
                        accessCode = saved.accessCode,
                        themeMode = saved.themeMode.ifBlank { "DARK" }
                    )
                }
            }
        }
    }

    fun onFullNameChanged(name: String) {
        _uiState.value = _uiState.value.copy(fullName = name, errorMessage = null)
    }

    fun onHomeCountryChanged(cca3: String) {
        _uiState.value = _uiState.value.copy(homeCountryCca3 = cca3)
    }

    fun onBaseCurrencyChanged(code: String) {
        _uiState.value = _uiState.value.copy(baseCurrencyCode = code)
    }

    fun onTempUnitChanged(unit: String) {
        _uiState.value = _uiState.value.copy(tempUnit = unit)
    }

    fun onThemeModeChanged(mode: String) {
        _uiState.value = _uiState.value.copy(themeMode = mode)
    }

    fun onSecurityEnabledChanged(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(
            isSecurityEnabled = enabled,
            isBiometricEnabled = if (enabled) _uiState.value.isBiometricEnabled else false,
            accessCode = if (enabled) _uiState.value.accessCode else ""
        )
    }

    fun onBiometricEnabledChanged(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isBiometricEnabled = enabled)
    }

    fun onAccessCodeChanged(code: String) {
        val filtered = code.filter { it.isDigit() }.take(4)
        _uiState.value = _uiState.value.copy(accessCode = filtered)
    }

    fun onPhotoSelected(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val destinationFile = File(context.filesDir, "profile_avatar.jpg")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    destinationFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                _uiState.value = _uiState.value.copy(photoUri = destinationFile.absolutePath)
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(photoUri = uri.toString())
            }
        }
    }

    fun saveProfile() {
        val name = uiState.value.fullName.trim()
        if (name.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Full name is required")
            return
        }

        if (uiState.value.isSecurityEnabled && uiState.value.accessCode.isNotBlank() && uiState.value.accessCode.length < 4) {
            _uiState.value = _uiState.value.copy(errorMessage = "Access PIN must be 4 digits")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null)
            val profile = UserProfile(
                userName = name,
                homeCountryCca3 = uiState.value.homeCountryCca3,
                baseCurrencyCode = uiState.value.baseCurrencyCode,
                tempUnit = uiState.value.tempUnit,
                photoUri = uiState.value.photoUri,
                isBiometricEnabled = if (uiState.value.isSecurityEnabled) uiState.value.isBiometricEnabled else false,
                accessCode = if (uiState.value.isSecurityEnabled) uiState.value.accessCode else "",
                themeMode = uiState.value.themeMode
            )
            saveProfileUseCase(profile)
            _uiState.value = _uiState.value.copy(isSaving = false, isSaved = true)
        }
    }

    fun onSavedHandled() {
        _uiState.value = _uiState.value.copy(isSaved = false)
    }
}
