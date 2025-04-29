package com.example.plantwise  // use your actual package name here

data class Nursery(
    val name: String,
    val address: String,
    val justdialPhone: String,
    val businessPhone: String,
    val rating: String,
    val reviews: String,
    val latitude: Double = 0.0,  // Default value for backward compatibility
    val longitude: Double = 0.0

) {
    // Generates Google Maps URL
    fun getMapsUrl(): String {
        return "https://www.google.com/maps/search/?api=1&query=$latitude,$longitude"
    }
}
