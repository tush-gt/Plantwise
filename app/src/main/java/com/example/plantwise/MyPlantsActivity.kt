package com.example.plantwise

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyPlantsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_plants)

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
                    for (document in querySnapshot.documents) {
                        val name = document.getString("name") ?: "Unnamed Plant"
                        val desc = document.getString("desc") ?: "No description"
                        val hour = document.getLong("hour")?.toInt() ?: 0
                        val minute = document.getLong("minute")?.toInt() ?: 0

                        Log.d("Plant", "$name - $desc at $hour:$minute")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("PlantError", "Failed to fetch plants: ", e)
                }
        } else {
            Toast.makeText(this, "User not logged in 😔", Toast.LENGTH_SHORT).show()
        }
    }
}
