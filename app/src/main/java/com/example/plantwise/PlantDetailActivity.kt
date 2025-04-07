package com.example.plantwise

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.plantwise.databinding.PlantDetailsBinding

class PlantDetailActivity : AppCompatActivity() {

    private lateinit var binding: PlantDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding init
        binding = PlantDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get PlantData from intent
        val plant: PlantData? = intent.getParcelableExtra("plantData")

        plant?.let {
            binding.commonName.text = it.commonName
            binding.scientificName.text = it.scientificName
            binding.type.text = it.type?.joinToString(", ") ?: "N/A"
            binding.use.text = it.use
            binding.waterNeeds.text = it.waterNeeds
            binding.sunlightNeeds.text = it.sunlightNeeds
            binding.careTips.text = it.careTips
            binding.storageTips.text = it.storageTips
            binding.growthForm.text = it.growthForm
            binding.specification.text = it.specification

            Glide.with(this)
                .load(it.image)
                .placeholder(R.drawable.sample_plant)
                .into(binding.plantImage)
        }
    }
}
