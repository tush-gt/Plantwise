package com.example.plantwise

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PlantAdapter(
    private var plantList: List<PlantData>,
    private val onItemClick: (PlantData) -> Unit
) : RecyclerView.Adapter<PlantAdapter.PlantViewHolder>(), Filterable {

    private var filteredList: List<PlantData> = plantList

    inner class PlantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val plantImage: ImageView = itemView.findViewById(R.id.plantImage)
        val plantName: TextView = itemView.findViewById(R.id.plantName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_plant, parent, false)
        return PlantViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val plant = filteredList[position]
        holder.plantName.text = plant.commonName

        // Set image based on image name string from drawable
        val context = holder.itemView.context
        val imageResId = context.resources.getIdentifier(plant.image, "drawable", context.packageName)

        if (imageResId != 0) {
            holder.plantImage.setImageResource(imageResId)
        } else {
            holder.plantImage.setImageResource(R.drawable.aloe_vera) // fallback image
        }

        // Handle click to redirect to detail screen
        holder.itemView.setOnClickListener {
            onItemClick(plant)
        }
    }

    override fun getItemCount(): Int = filteredList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val searchText = constraint?.toString()?.lowercase()?.trim() ?: ""
                val results = if (searchText.isEmpty()) {
                    plantList
                } else {
                    plantList.filter {
                        it.commonName?.lowercase()?.contains(searchText) == true
                    }
                }
                return FilterResults().apply { values = results }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as List<PlantData>
                notifyDataSetChanged()
            }
        }
    }
}
