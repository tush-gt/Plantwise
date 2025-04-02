package com.example.plantwise
import com.example.plantwise.PlantAdapter

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantwise.Plant
import com.example.plantwise.PlantDetailActivity
import com.example.plantwise.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity: AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var plantAdapter: PlantAdapter
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        bottomNav = findViewById(R.id.bottomNav)
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Handle Home Click
                    true
                }
                R.id.nav_nursery -> {
                    // Handle Earth Click
                    true
                }
                R.id.nav_garden -> {
                    // Handle Rectangle Click
                    true
                }
                else -> false
            }
        }

        // Sample plant data
        val plants = listOf(
            Plant("Aloe Vera", R.drawable.aloe_vera),
            Plant("Bee Balm", R.drawable.bee_balm),
            Plant("Chamomile", R.drawable.chamomile),
            Plant("Cosmos", R.drawable.cosmos),
            Plant("Dahlia", R.drawable.dahlia),
            Plant("Marigold",R.drawable.cosmos),
            Plant("Aloe Vera", R.drawable.aloe_vera),
            Plant("Bee Balm", R.drawable.bee_balm),
            Plant("Chamomile", R.drawable.chamomile),
            Plant("Cosmos", R.drawable.cosmos),
            Plant("Dahlia", R.drawable.dahlia),
            Plant("Marigold",R.drawable.cosmos)
        )

        plantAdapter = PlantAdapter(plants) { plant ->
            // Open PlantDetailActivity when a plant is clicked
            val intent = Intent(this, PlantDetailActivity::class.java)
            intent.putExtra("plantName", plant.name)
            intent.putExtra("plantImage", plant.imageResId)
            startActivity(intent)
        }

        recyclerView.adapter = plantAdapter
    }
}
