package com.example.plantwise

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.util.*

object JsonUtils {
    fun loadPlantCareData(context: Context): List<PlantData> {
        val jsonString = context.assets.open("indian-plants-data.json")
            .bufferedReader().use { it.readText() }

//        Log.e("JSON_RAW", jsonString) // Add this line

        val listType = object : TypeToken<List<PlantData>>() {}.type
        return Gson().fromJson(jsonString, listType)

    }
}
