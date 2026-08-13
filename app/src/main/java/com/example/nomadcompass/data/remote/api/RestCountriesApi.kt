package com.example.nomadcompass.data.remote.api

import com.example.nomadcompass.data.remote.dto.RestCountryDto
import retrofit2.http.GET

interface RestCountriesApi {
    @GET("v3.1/all?fields=cca3,cca2,name,capital,region,subregion,flags,flag,currencies,languages,borders,latlng,landlocked")
    suspend fun getAllCountries(): List<RestCountryDto>
}
