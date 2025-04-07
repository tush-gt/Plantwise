package com.example.plantwise

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyPlantsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserPlantAdapter
    private val plantList = mutableListOf<PlantModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_plants)

        recyclerView = findViewById(R.id.plantsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = UserPlantAdapter(plantList)
        recyclerView.adapter = adapter

        loadUserPlants()
    }

    private fun loadUserPlants() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid != null) {
            FirebaseFirestore.getInstance()
                .collection("user_plants")
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    plantList.clear()
                    for (document in querySnapshot.documents) {
                        val name = document.getString("name") ?: "Unnamed Plant"
                        val desc = document.getString("desc") ?: "No description"
                        val hour = document.getLong("hour")?.toInt() ?: 0
                        val minute = document.getLong("minute")?.toInt() ?: 0

                        plantList.add(PlantModel(name, desc, hour, minute))
                    }
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    Log.e("PlantError", "Failed to fetch plants: ", e)
                    Toast.makeText(this, "Couldn't load plants 😢", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "You're not logged in! 😔", Toast.LENGTH_SHORT).show()
        }
    }
}
