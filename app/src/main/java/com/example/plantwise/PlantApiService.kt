package com.example.plantwise

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface PlantApiService {
    @GET("plants")
    fun getPlants(@Header("Authorization") token: String): Call<PlantResponse>


}
