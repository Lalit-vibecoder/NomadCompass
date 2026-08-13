package com.example.nomadcompass.ui.screens.country

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nomadcompass.domain.repository.CurrencyRepository
import com.example.nomadcompass.domain.usecase.CountryDetailResult
import com.example.nomadcompass.domain.usecase.GetCountryDetailUseCase
import com.example.nomadcompass.domain.usecase.GetProfileUseCase
import com.example.nomadcompass.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

data class CountryProfileUiState(
    val isLoading: Boolean = true,
    val detail: CountryDetailResult? = null,
    val baseCurrencyCode: String = "USD",
    val targetCurrencyCode: String = "USD",
    val exchangeRate: Double = 1.0,
    val inputAmount: String = "1.0",
    val convertedCurrencyAmount: Double = 1.0,
    val isConverterOpen: Boolean = false,
    val isSwapped: Boolean = false,
    val isRefreshingCurrency: Boolean = false,
    val currencyRefreshMessage: String? = null,
)

@HiltViewModel
class CountryProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCountryDetailUseCase: GetCountryDetailUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val currencyRepository: CurrencyRepository,
) : ViewModel() {

    private val cca3: String = checkNotNull(savedStateHandle["cca3"])

    private val _uiState = MutableStateFlow(CountryProfileUiState())
    val uiState: StateFlow<CountryProfileUiState> = _uiState.asStateFlow()

    init {
        loadDetail()
    }

    fun loadDetail() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val profile = getProfileUseCase().firstOrNull()
            val baseCurrency = profile?.baseCurrencyCode ?: "USD"
            val result = getCountryDetailUseCase(cca3, baseCurrency)
            val targetCurrency = result?.country?.currencyCode ?: "USD"
            val rate = result?.currencyRate?.rate ?: 1.0

            val currentInput = _uiState.value.inputAmount.toDoubleOrNull() ?: 1.0
            val effectiveRate = if (_uiState.value.isSwapped && rate > 0) 1.0 / rate else rate

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                detail = result,
                baseCurrencyCode = baseCurrency,
                targetCurrencyCode = targetCurrency,
                exchangeRate = rate,
                convertedCurrencyAmount = currentInput * effectiveRate
            )
        }
    }

    fun refreshExchangeRate() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isRefreshingCurrency = true,
                currencyRefreshMessage = null
            )
            val base = _uiState.value.baseCurrencyCode
            val target = _uiState.value.targetCurrencyCode
            val updatedRateObj = currencyRepository.getRate(base, target, forceRefresh = true)
            val newRate = updatedRateObj?.rate ?: _uiState.value.exchangeRate
            val amount = _uiState.value.inputAmount.toDoubleOrNull() ?: 1.0
            val effectiveRate = if (_uiState.value.isSwapped && newRate > 0) 1.0 / newRate else newRate

            val formattedRate = String.format(Locale.US, "%.4f", newRate)
            _uiState.value = _uiState.value.copy(
                isRefreshingCurrency = false,
                exchangeRate = newRate,
                convertedCurrencyAmount = amount * effectiveRate,
                currencyRefreshMessage = "Live rate updated: 1 $base = $formattedRate $target"
            )
        }
    }

    fun openConverter() {
        _uiState.value = _uiState.value.copy(isConverterOpen = true)
    }

    fun closeConverter() {
        _uiState.value = _uiState.value.copy(
            isConverterOpen = false,
            currencyRefreshMessage = null
        )
    }

    fun onInputAmountChanged(amountStr: String) {
        val amount = amountStr.toDoubleOrNull() ?: 0.0
        val rate = uiState.value.exchangeRate
        val effectiveRate = if (uiState.value.isSwapped && rate > 0) 1.0 / rate else rate
        _uiState.value = _uiState.value.copy(
            inputAmount = amountStr,
            convertedCurrencyAmount = amount * effectiveRate
        )
    }

    fun toggleSwapCurrencies() {
        val currentlySwapped = !uiState.value.isSwapped
        val amount = uiState.value.inputAmount.toDoubleOrNull() ?: 0.0
        val rate = uiState.value.exchangeRate
        val effectiveRate = if (currentlySwapped && rate > 0) 1.0 / rate else rate
        _uiState.value = _uiState.value.copy(
            isSwapped = currentlySwapped,
            convertedCurrencyAmount = amount * effectiveRate
        )
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            toggleFavoriteUseCase(cca3)
            loadDetail()
        }
    }
}

