package com.example.nomadcompass.data.remote.api

import com.example.nomadcompass.data.remote.dto.FrankfurterResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface FrankfurterApi {
    @GET("v1/latest")
    suspend fun getLatestRates(
        @Query("base") base: String,
    ): FrankfurterResponse
}
