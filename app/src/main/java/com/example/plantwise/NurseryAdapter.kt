package com.example.plantwise

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class NurseryAdapter(private val nurseryList: List<Nursery>) :
    RecyclerView.Adapter<NurseryAdapter.NurseryViewHolder>() {

    // ViewHolder class remains the same
    class NurseryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textCoordinates: TextView = itemView.findViewById(R.id.textCoordinates)
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

        // Bind data to views
        holder.nameTextView.text = nursery.name
        holder.addressTextView.text = nursery.address
        holder.justdialPhoneTextView.text = nursery.justdialPhone
        holder.businessPhoneTextView.text = nursery.businessPhone
        holder.ratingTextView.text = "Rating: ${nursery.rating}"
        holder.reviewsTextView.text = "Reviews: ${nursery.reviews}"

        // Show coordinates if available
        if (nursery.latitude != 0.0 && nursery.longitude != 0.0) {
            holder.textCoordinates.text = "${nursery.latitude}° N, ${nursery.longitude}° E"
            holder.textCoordinates.visibility = View.VISIBLE
        } else {
            holder.textCoordinates.visibility = View.GONE
        }

        // Set click listener for the entire item
        holder.itemView.setOnClickListener {
            val uri = if (nursery.latitude != 0.0 && nursery.longitude != 0.0) {
                "geo:${nursery.latitude},${nursery.longitude}?q=${Uri.encode(nursery.name)}"
            } else {
                "geo:0,0?q=${Uri.encode(nursery.address + ", Mumbai")}"
            }

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri)).apply {
                setPackage("com.google.android.apps.maps")
            }
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = nurseryList.size

    private fun openGoogleMaps(context: android.content.Context, lat: Double, lon: Double, label: String) {
        try {
            // Create Google Maps intent
            val gmmIntentUri = Uri.parse("geo:$lat,$lon?q=$lat,$lon(${Uri.encode(label)})")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")

            // Verify there's an app to handle the intent
            if (mapIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(mapIntent)
            } else {
                // Fallback to browser with Google Maps URL
                val mapsUrl = "https://www.google.com/maps/search/?api=1&query=$lat,$lon"
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mapsUrl))
                context.startActivity(browserIntent)
            }
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Error opening maps: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}