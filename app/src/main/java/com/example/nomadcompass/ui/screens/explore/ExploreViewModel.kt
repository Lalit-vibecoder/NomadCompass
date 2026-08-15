package com.example.nomadcompass.ui.screens.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nomadcompass.domain.model.Country
import com.example.nomadcompass.domain.repository.CountryRepository
import com.example.nomadcompass.domain.usecase.GetAllCountriesUseCase
import com.example.nomadcompass.domain.usecase.SearchCountriesUseCase
import com.example.nomadcompass.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExploreUiState(
    val searchQuery: String = "",
    val selectedPill: String = "All",
    val countries: List<Country> = emptyList(),
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val getAllCountriesUseCase: GetAllCountriesUseCase,
    private val searchCountriesUseCase: SearchCountriesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val countryRepository: CountryRepository,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedPill = MutableStateFlow("All")
    val selectedPill: StateFlow<String> = _selectedPill.asStateFlow()

    val uiState: StateFlow<ExploreUiState> = combine(
        _searchQuery,
        _selectedPill,
        _searchQuery.flatMapLatest { query ->
            if (query.isBlank()) {
                getAllCountriesUseCase()
            } else {
                searchCountriesUseCase(query)
            }
        }
    ) { query, pill, countryList ->
        withContext(Dispatchers.Default) {
            val filtered = when (pill) {
                "My Favs" -> countryList.filter { it.isFavorite }
                "Europe" -> countryList.filter { it.region.contains("Europe", ignoreCase = true) }
                "Asia" -> countryList.filter { it.region.contains("Asia", ignoreCase = true) }
                "Americas" -> countryList.filter { it.region.contains("Americas", ignoreCase = true) }
                "Africa" -> countryList.filter { it.region.contains("Africa", ignoreCase = true) }
                "Oceania" -> countryList.filter { it.region.contains("Oceania", ignoreCase = true) }
                "Themes" -> countryList.take(6)
                else -> countryList
            }
            ExploreUiState(
                searchQuery = query,
                selectedPill = pill,
                countries = filtered
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ExploreUiState()
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onPillSelected(pill: String) {
        _selectedPill.value = pill
    }

    fun resetFilters() {
        _searchQuery.value = ""
        _selectedPill.value = "All"
    }

    fun toggleFavorite(cca3: String) {
        viewModelScope.launch {
            toggleFavoriteUseCase(cca3)
        }
    }
}
