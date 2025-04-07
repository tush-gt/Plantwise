package com.example.plantwise


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class PlantModel(
    val name: String = "",
    val desc: String = "",
    val hour: Int = 0,
    val minute: Int = 0
)

class UserPlantAdapter(
    private var userPlants: List<PlantModel>
) : RecyclerView.Adapter<UserPlantAdapter.MyPlantViewHolder>() {

    inner class MyPlantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val plantName: TextView = itemView.findViewById(R.id.userPlantName)
        val plantDesc: TextView = itemView.findViewById(R.id.userPlantDesc)
        val plantTime: TextView = itemView.findViewById(R.id.userPlantTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPlantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_plant, parent, false)
        return MyPlantViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyPlantViewHolder, position: Int) {
        val plant = userPlants[position]
        holder.plantName.text = plant.name
        holder.plantDesc.text = plant.desc
        holder.plantTime.text = "💧 ${"%02d".format(plant.hour)}:${"%02d".format(plant.minute)}"
    }

    override fun getItemCount(): Int = userPlants.size
}
