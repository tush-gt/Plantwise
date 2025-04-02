package com.example.plantwise

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlantApiService {
    @GET("plants")
    fun getPlants(@Query("token") apiKey: String): Call<PlantResponse>  // ✅ Ensured Correct Mapping
}
