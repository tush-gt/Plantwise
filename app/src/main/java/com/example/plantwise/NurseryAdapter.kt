package com.example.plantwise

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NurseryAdapter(private val nurseryList: List<Nursery>) :
    RecyclerView.Adapter<NurseryAdapter.NurseryViewHolder>() {

    class NurseryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.textName)
        val addressTextView: TextView = itemView.findViewById(R.id.textAddress)
        val justdialPhoneTextView: TextView = itemView.findViewById(R.id.textJustdialPhone)
        val businessPhoneTextView: TextView = itemView.findViewById(R.id.textBusinessPhone)
        val ratingTextView: TextView = itemView.findViewById(R.id.textRating)
        val reviewsTextView: TextView = itemView.findViewById(R.id.textReviews)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NurseryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_nursery, parent, false)
        return NurseryViewHolder(view)
    }

    override fun onBindViewHolder(holder: NurseryViewHolder, position: Int) {
        val nursery = nurseryList[position]
        holder.nameTextView.text = "Name: ${nursery.name}"
        holder.addressTextView.text = "Address: ${nursery.address}"
        holder.justdialPhoneTextView.text = "Justdial: ${nursery.justdialPhone}"
        holder.businessPhoneTextView.text = "Phone: ${nursery.businessPhone}"
        holder.ratingTextView.text = "Rating: ${nursery.rating}"
        holder.reviewsTextView.text = "Reviews: ${nursery.reviews}"
    }

    override fun getItemCount(): Int = nurseryList.size
}
