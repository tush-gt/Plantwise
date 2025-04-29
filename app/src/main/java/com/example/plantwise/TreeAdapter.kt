package com.example.plantwise

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class TreeAdapter(
    private val trees: MutableList<TreeDataModel>,
    private val isAdmin: Boolean
) : RecyclerView.Adapter<TreeAdapter.TreeViewHolder>() {

    inner class TreeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.treeImage)
        val nameView: TextView = view.findViewById(R.id.treeName)
        val priceView: TextView = view.findViewById(R.id.treePrice)
        val buyButton: Button = view.findViewById(R.id.buyButton)
        val deleteButton: Button = view.findViewById(R.id.deleteButton)
        val editButton: Button = view.findViewById(R.id.editButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tree, parent, false)
        return TreeViewHolder(view)
    }

    override fun onBindViewHolder(holder: TreeViewHolder, position: Int) {
        val tree = trees[position]
        Glide.with(holder.imageView.context).load(tree.imageUrl).into(holder.imageView)
        holder.nameView.text = tree.name
        holder.priceView.text = "₹${tree.price}"

        holder.buyButton.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Buying ${tree.name}", Toast.LENGTH_SHORT).show()
        }

        if (isAdmin) {
            holder.deleteButton.visibility = View.VISIBLE
            holder.editButton.visibility = View.VISIBLE
        } else {
            holder.deleteButton.visibility = View.GONE
            holder.editButton.visibility = View.GONE
        }

        holder.deleteButton.setOnClickListener {
            val context = holder.itemView.context
            val treeId = tree.id

            FirebaseFirestore.getInstance().collection("trees").document(treeId)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(context, "${tree.name} deleted", Toast.LENGTH_SHORT).show()
                    trees.removeAt(holder.adapterPosition)
                    notifyItemRemoved(holder.adapterPosition)
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to delete ${tree.name}", Toast.LENGTH_SHORT).show()
                }
        }

        holder.editButton.setOnClickListener {
            // TODO: Implement edit logic
        }
    }

    override fun getItemCount() = trees.size
}