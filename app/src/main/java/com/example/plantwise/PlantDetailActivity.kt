package com.example.plantwise

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class PlantDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.plant_details)

        val plantImage: ImageView = findViewById(R.id.plant_image)
        val plantName: TextView = findViewById(R.id.plant_name)
        val botanicalName: TextView = findViewById(R.id.botanical_name)
        val guide1: TextView = findViewById(R.id.guide_content_1)
        val guide2: TextView = findViewById(R.id.guide_content_2)
        val guide3: TextView = findViewById(R.id.guide_content_3)
        val guide4: TextView = findViewById(R.id.guide_content_4)

        // Fetch data passed from the adapter
        val name = intent.getStringExtra("name")
        val imageUrl = intent.getStringExtra("imageUrl")
        val sunlight = intent.getStringExtra("sunlight")
        val watering = intent.getStringExtra("watering")
        val spacing = intent.getStringExtra("spacing")

        // Set values to the views
        plantName.text = name
        botanicalName.text = "Botanical Info: $spacing"
        guide1.text = "$name is a wonderful plant with amazing benefits!"
        guide2.text = "Grow your $name in healthy soil with love and patience 💚"
        guide3.text = "Water it as per need — usually $watering."
        guide4.text = "Keep it in $sunlight conditions to thrive ☀️"

        Glide.with(this).load(imageUrl).into(plantImage)
    }
}
