package com.example.plantwise

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchBar: EditText
    private lateinit var plantAdapter: PlantAdapter
    private var plantList: List<Plant> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)

        recyclerView = findViewById(R.id.recyclerView)
        searchBar = findViewById(R.id.searchBar)

        recyclerView.layoutManager = GridLayoutManager(this, 2)

        fetchPlants()

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                plantAdapter.filter.filter(s)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun fetchPlants() {
        try {
            plantList = JsonUtils.loadPlantCareData(this)

            // 💡 Pass the click listener here
            plantAdapter = PlantAdapter(plantList) { selectedPlant ->
                val intent = Intent(this, PlantDetailActivity::class.java).apply {
                    putExtra("name", selectedPlant.commonName)
                    putExtra("imageUrl", selectedPlant.image)
                    putExtra("sunlight", selectedPlant.sunlightNeeds)
                    putExtra("watering", selectedPlant.waterNeeds)
                    putExtra("spacing", selectedPlant.use)
                }
                startActivity(intent)
            }

            recyclerView.adapter = plantAdapter
        } catch (e: Exception) {
            Log.e("JSON_ERROR", "Failed to load plantcare.json: ${e.message}")
            Toast.makeText(this, "Oops! Couldn't load plant data 😢", Toast.LENGTH_SHORT).show()
        }
    }
}
