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

enum class SecurityOption {
    NONE,
    PIN,
    BIOMETRIC
}

data class ProfileSetupUiState(
    val fullName: String = "",
    val homeCountryCca3: String = "USA",
    val baseCurrencyCode: String = "USD",
    val tempUnit: String = "C",
    val photoUri: String? = null,
    val securityOption: SecurityOption = SecurityOption.NONE,
    val accessCode: String = "",
    val themeMode: String = "DARK", // "DARK" or "LIGHT"
    val bgPhotoUri: String? = null,
    val bgBlurRadius: Float = 24f,
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
                    val secOpt = when {
                        saved.isBiometricEnabled -> SecurityOption.BIOMETRIC
                        saved.accessCode.isNotBlank() -> SecurityOption.PIN
                        else -> SecurityOption.NONE
                    }
                    _uiState.value = _uiState.value.copy(
                        fullName = saved.userName,
                        homeCountryCca3 = saved.homeCountryCca3,
                        baseCurrencyCode = saved.baseCurrencyCode,
                        tempUnit = saved.tempUnit,
                        photoUri = saved.photoUri,
                        securityOption = secOpt,
                        accessCode = saved.accessCode,
                        themeMode = saved.themeMode.ifBlank { "DARK" },
                        bgPhotoUri = saved.bgPhotoUri,
                        bgBlurRadius = saved.bgBlurRadius
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

    fun onSecurityOptionChanged(option: SecurityOption) {
        _uiState.value = _uiState.value.copy(
            securityOption = option,
            errorMessage = null,
            accessCode = if (option == SecurityOption.PIN) _uiState.value.accessCode else ""
        )
    }

    fun onAccessCodeChanged(code: String) {
        val filtered = code.filter { it.isDigit() }.take(4)
        _uiState.value = _uiState.value.copy(accessCode = filtered, errorMessage = null)
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

    fun onBgPhotoSelected(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val destinationFile = File(context.filesDir, "app_custom_bg.jpg")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    destinationFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                _uiState.value = _uiState.value.copy(bgPhotoUri = destinationFile.absolutePath)
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(bgPhotoUri = uri.toString())
            }
        }
    }

    fun onResetDefaultBg() {
        _uiState.value = _uiState.value.copy(bgPhotoUri = null)
    }

    fun onBgBlurRadiusChanged(radius: Float) {
        _uiState.value = _uiState.value.copy(bgBlurRadius = radius)
    }

    fun saveProfile() {
        val name = uiState.value.fullName.trim()
        if (name.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Full name is required")
            return
        }

        val secOpt = uiState.value.securityOption
        if (secOpt == SecurityOption.PIN && uiState.value.accessCode.length < 4) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter a 4-digit PIN")
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
                isBiometricEnabled = (secOpt == SecurityOption.BIOMETRIC),
                accessCode = if (secOpt == SecurityOption.PIN) uiState.value.accessCode else "",
                themeMode = uiState.value.themeMode,
                bgPhotoUri = uiState.value.bgPhotoUri,
                bgBlurRadius = uiState.value.bgBlurRadius
            )
            saveProfileUseCase(profile)
            _uiState.value = _uiState.value.copy(isSaving = false, isSaved = true)
        }
    }

    fun onSavedHandled() {
        _uiState.value = _uiState.value.copy(isSaved = false)
    }
}
