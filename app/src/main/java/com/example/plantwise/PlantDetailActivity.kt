package com.example.plantwise

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.plantwise.databinding.PlantDetailsBinding

class PlantDetailActivity : AppCompatActivity() {

    private lateinit var binding: PlantDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding
        binding = PlantDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get plant data
        val plant: PlantData? = intent.getParcelableExtra("plantData")

        // Populate UI with plant data
        plant?.let {
            binding.commonName.text = it.commonName ?: "Unknown Plant"
            binding.scientificName.text = "Botanical Name: ${it.scientificName ?: "N/A"}"
            binding.familyGenus.text = "Family: ${it.family ?: "N/A"}, Genus: ${it.genus ?: "N/A"}"
            binding.edibleStatus.text = "Edible: ${if (it.edible == true) "Yes" else "No/Unknown"}"

            Glide.with(this)
                .load(it.imageUrl ?: "")
                .placeholder(R.drawable.sample_plant)
                .into(binding.plantImage)

            // Quick Facts Section
            binding.growthForm.text = it.growthForm ?: "Not available"

            // If you have specifications data, use it
            it.specifications?.let { specs ->
                binding.growthHabit.text = specs.growth_habit ?: "Not available"
                binding.toxicity.text = specs.toxicity ?: "Unknown"
            } ?: run {
                binding.growthHabit.text = "Not available"
                binding.toxicity.text = "Unknown"
            }

            // Observations Section
            binding.observations.text = it.observations ?: "No observations available."

            // Care Instructions
            binding.careInstructions.text = "Water when soil is dry. Place in bright, indirect sunlight. " +
                    "Keep away from drafts and extreme temperatures."
        }

        // Note: The quick action buttons in your Kotlin code don't exist in the XML layout
        // If you need those buttons, you'll need to add them to your XML layout
    }
}