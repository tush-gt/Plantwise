package com.example.plantwise

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object JsonUtils {
    fun loadPlantCareData(context: Context): List<Plant> {
        val jsonString = context.assets.open("plantcare.json").bufferedReader().use { it.readText() }
        val listType = object : TypeToken<List<Plant>>() {}.type
        return Gson().fromJson(jsonString, listType)
    }
}
