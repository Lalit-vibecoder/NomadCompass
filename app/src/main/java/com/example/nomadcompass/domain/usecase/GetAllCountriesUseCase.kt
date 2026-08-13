package com.example.nomadcompass.domain.usecase

import com.example.nomadcompass.domain.model.Country
import com.example.nomadcompass.domain.repository.CountryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllCountriesUseCase @Inject constructor(
    private val repository: CountryRepository,
) {
    operator fun invoke(): Flow<List<Country>> = repository.getAllCountries()
}
