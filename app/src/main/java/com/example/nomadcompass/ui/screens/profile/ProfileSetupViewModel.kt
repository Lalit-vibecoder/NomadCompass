package com.example.nomadcompass.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nomadcompass.domain.model.Country
import com.example.nomadcompass.domain.model.UserProfile
import com.example.nomadcompass.domain.usecase.GetAllCountriesUseCase
import com.example.nomadcompass.domain.usecase.GetProfileUseCase
import com.example.nomadcompass.domain.usecase.SaveProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileSetupUiState(
    val fullName: String = "",
    val homeCountryCca3: String = "USA",
    val baseCurrencyCode: String = "USD",
    val tempUnit: String = "C",
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
                    _uiState.value = _uiState.value.copy(
                        fullName = saved.userName,
                        homeCountryCca3 = saved.homeCountryCca3,
                        baseCurrencyCode = saved.baseCurrencyCode,
                        tempUnit = saved.tempUnit
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

    fun saveProfile() {
        val name = uiState.value.fullName.trim()
        if (name.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Full name is required")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null)
            val profile = UserProfile(
                userName = name,
                homeCountryCca3 = uiState.value.homeCountryCca3,
                baseCurrencyCode = uiState.value.baseCurrencyCode,
                tempUnit = uiState.value.tempUnit
            )
            saveProfileUseCase(profile)
            _uiState.value = _uiState.value.copy(isSaving = false, isSaved = true)
        }
    }

    fun onSavedHandled() {
        _uiState.value = _uiState.value.copy(isSaved = false)
    }
}

