package com.example.nomadcompass.data.remote.api

import com.example.nomadcompass.data.remote.dto.TravelAdvisoryResponse
import retrofit2.http.GET

interface TravelAdvisoryApi {
    @GET("api")
    suspend fun getAdvisories(): TravelAdvisoryResponse
}
