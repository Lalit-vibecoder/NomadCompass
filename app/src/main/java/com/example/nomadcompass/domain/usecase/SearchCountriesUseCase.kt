package com.example.nomadcompass.domain.usecase

import com.example.nomadcompass.domain.model.Country
import com.example.nomadcompass.domain.repository.CountryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchCountriesUseCase @Inject constructor(
    private val repository: CountryRepository,
) {
    operator fun invoke(query: String): Flow<List<Country>> = repository.search(query)
}
