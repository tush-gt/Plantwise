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

        val name = intent.getStringExtra("name")
        val imageUrl = intent.getStringExtra("imageUrl")
        val sunlight = intent.getStringExtra("sunlight")
        val watering = intent.getStringExtra("watering")
        val spacing = intent.getStringExtra("spacing")

        val plantImage = findViewById<ImageView>(R.id.plantImage)
        val plantName = findViewById<TextView>(R.id.plantName)
        val sunlightText = findViewById<TextView>(R.id.guide_title_4)
        val wateringText = findViewById<TextView>(R.id.guide_content_3)
//        val spacingText = findViewById<TextView>(R.id.spacingText)

        Glide.with(this).load(imageUrl).into(plantImage)
        plantName.text = name
        sunlightText.text = "Sunlight: $sunlight"
        wateringText.text = "Watering: $watering"
//        spacingText.text = "Spacing: $spacing"
    }
}
