package com.example.plantwise

data class PlantModel(
    val name: String = "",
    val desc: String = "",
    val hour: Int = 0,
    val minute: Int = 0,
    val id: String = ""  // <- this is important!
)
