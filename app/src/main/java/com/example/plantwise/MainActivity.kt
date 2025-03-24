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

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var plantAdapter: PlantAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Sample plant data
        val plants = listOf(
            Plant("Aloe Vera", R.drawable.aloe_vera),
            Plant("Bee Balm", R.drawable.bee_balm),
            Plant("Chamomile", R.drawable.chamomile),
            Plant("Cosmos", R.drawable.cosmos),
            Plant("Dahlia", R.drawable.dahlia)
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
