package com.example.plantwise

import com.google.gson.annotations.SerializedName

data class PlantResponse(
    val data: List<PlantData> // ✅ Ensure 'data' matches JSON
)




