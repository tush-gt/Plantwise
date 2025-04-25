package com.example.plantwise

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchBar: EditText
    private lateinit var plantAdapter: PlantAdapter
    private var plantList: List<PlantData> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)  // Make sure this matches your XML file name

        val profileIcon: ImageView = findViewById(R.id.profileImage)

        profileIcon.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }


        recyclerView = findViewById(R.id.recyclerView)
        searchBar = findViewById(R.id.searchBar)

        // 2-column grid layout
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Load plant list from JSON
        fetchPlants()
        setupBottomNav()

        // Setup search
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

            plantAdapter = PlantAdapter(plantList) { selectedPlant ->
                val intent = Intent(this, PlantDetailActivity::class.java)
                intent.putExtra("plantData", selectedPlant)
                startActivity(intent)
            }

            recyclerView.adapter = plantAdapter
        } catch (e: Exception) {
            Log.e("JSON_ERROR", "Failed to load json file: ${e.message}")
            Toast.makeText(this, "Oops! Couldn't load plant data 😢", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                    R.id.nav_home -> {
                    // you're already on Home, do nothing or refresh maybe
                    true
                }

                R.id.nav_nursery -> {
                    val intent = Intent(this, NurseryActivity::class.java)
                    startActivity(intent)
                    true

                }

                R.id.nav_garden -> {
                    // Navigate to ReminderActivity when "My Garden" is tapped
                    val intent = Intent(this, AddPlantActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_garden1 -> {
                    // Navigate to ReminderActivity when "My Garden" is tapped
                    val intent = Intent(this, MyPlantsActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}
