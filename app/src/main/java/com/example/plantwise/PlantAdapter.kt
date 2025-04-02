package com.example.plantwise

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PlantAdapter(private val plantList: List<PlantData>, private val onItemClick: (PlantData) -> Unit) :
    RecyclerView.Adapter<PlantAdapter.PlantViewHolder>() {


    class PlantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val plantName: TextView = view.findViewById(R.id.plant_name)
        val plantImage: ImageView = view.findViewById(R.id.plant_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.plant_item, parent, false)
        return PlantViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val plant = plantList[position]
        holder.plantName.text = plant.common_name

        // ✅ Prevents crash if the image URL is null
        Glide.with(holder.itemView.context)
            .load(plant.image_url?.toString()) // ✅ Ensure it's a String
            .placeholder(R.drawable.aloe_vera) // ✅ Placeholder image for null URLs
            .error(R.drawable.bee_balm) // ✅ Show if image fails to load
            .into(holder.plantImage)


        // ✅ Handle item click
        holder.itemView.setOnClickListener { onItemClick(plant) }
    }

    override fun getItemCount() = plantList.size
}
