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

    private lateinit var adapter: UserPlantAdapter
    private lateinit var recyclerView: RecyclerView
    private val plantList = mutableListOf<PlantModel>()  // store the data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_plants)

        recyclerView = findViewById(R.id.plantsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = UserPlantAdapter(plantList) { plant ->
            // Handle the edit button click
            val intent = Intent(this, EditPlantActivity::class.java)
            intent.putExtra("name", plant.name)
            intent.putExtra("desc", plant.desc)
            intent.putExtra("hour", plant.hour)
            intent.putExtra("minute", plant.minute)
            startActivity(intent)
        }

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

                        val plant = PlantModel(name, desc, hour, minute)
                        plantList.add(plant)
                    }
                    adapter.notifyDataSetChanged() // refresh the list ✨
                }
                .addOnFailureListener { e ->
                    Log.e("PlantError", "Failed to fetch plants: ", e)
                    Toast.makeText(this, "Failed to load plants 😢", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in 😔", Toast.LENGTH_SHORT).show()
        }
    }
}
