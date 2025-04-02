package com.example.plantwise

import com.google.gson.annotations.SerializedName

data class Plant(
    @SerializedName("common_name") val commonName: String?,
    @SerializedName("scientific_name") val scientificName: String?,
    @SerializedName("image_url") val imageUrl: String?
)
