package com.example.plantwise

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PlantAdapter(private var plantList: List<PlantData>) :
    RecyclerView.Adapter<PlantAdapter.PlantViewHolder>(), Filterable {

    private var filteredList = plantList.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.plant_item, parent, false)
        return PlantViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val plant = filteredList[position]
        holder.plantName.text = plant.common_name ?: "Unknown Plant" // ✅ Handle null names safely

        // Load image using Glide
        Glide.with(holder.itemView.context)
            .load(plant.image_url ?: "") // ✅ Use safe call with default empty string
            .placeholder(R.drawable.aloe_vera)
            .into(holder.plantImage)
    }

    override fun getItemCount() = filteredList.size

    // Filtering Logic
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.lowercase()?.trim() ?: ""

                Log.d("DEBUG", "Final Query: $query") // Debugging

                filteredList = if (query.isEmpty()) {
                    plantList.toMutableList()
                } else {
                    plantList.filter { it.common_name?.lowercase()?.contains(query) == true }
                        .toMutableList()
                }

                return FilterResults().apply { values = filteredList }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as? MutableList<PlantData> ?: mutableListOf()
                notifyDataSetChanged()
            }
        }
    }

    // ViewHolder Class
    class PlantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val plantName: TextView = itemView.findViewById(R.id.plant_name)
        val plantImage: ImageView = itemView.findViewById(R.id.plant_image)
    }
}
