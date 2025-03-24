package com.example.plantwise
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PlantDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plant_detail)

        val plantName = intent.getStringExtra("plantName")
        val plantImage = intent.getIntExtra("plantImage", 0)

        val nameTextView: TextView = findViewById(R.id.plantDetailName)
        val imageView: ImageView = findViewById(R.id.plantDetailImage)

        nameTextView.text = plantName
        imageView.setImageResource(plantImage)
    }
}
