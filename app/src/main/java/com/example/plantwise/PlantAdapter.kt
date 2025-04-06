package com.example.plantwise

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.util.*

class PlantAdapter(
    private var plantList: List<Plant>,
    private val onItemClick: (Plant) -> Unit // <- click callback
) : RecyclerView.Adapter<PlantAdapter.PlantViewHolder>(), Filterable {

    private var filteredPlantList: List<Plant> = plantList

    inner class PlantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val plantImage: ImageView = itemView.findViewById(R.id.plant_image)
        val plantName: TextView = itemView.findViewById(R.id.plant_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.plant_item, parent, false)
        return PlantViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val plant = filteredPlantList[position]

        holder.plantName.text = plant.commonName
        Glide.with(holder.itemView.context).load(plant.image).into(holder.plantImage)

        // 🔥 Click listener!
        holder.itemView.setOnClickListener {
            onItemClick(plant)
        }
    }

    override fun getItemCount(): Int = filteredPlantList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val query = charSequence.toString().lowercase()
                val filtered = if (query.isEmpty()) {
                    plantList
                } else {
                    plantList.filter {
                        it.commonName.lowercase().contains(query)
                    }
                }
                val results = FilterResults()
                results.values = filtered
                return results
            }

            override fun publishResults(charSequence: CharSequence?, results: FilterResults?) {
                filteredPlantList = results?.values as List<Plant>
                notifyDataSetChanged()
            }
        }
    }
}
