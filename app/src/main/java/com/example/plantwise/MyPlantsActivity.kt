package com.example.plantwise

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
class MyPlantsActivity : AppCompatActivity() {

    private lateinit var adapter: UserPlantAdapter
    private lateinit var recyclerView: RecyclerView
    private val plantList = mutableListOf<PlantModel>()

    val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            loadUserPlants()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_plants)

        recyclerView = findViewById(R.id.plantsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = UserPlantAdapter(
            userPlants = plantList,
            onEditClick = { plant ->
                val intent = Intent(this, EditPlantActivity::class.java)
                intent.putExtra("plantId", plant.id)
                intent.putExtra("name", plant.name)
                intent.putExtra("desc", plant.desc)
                intent.putExtra("hour", plant.hour)
                intent.putExtra("minute", plant.minute)
                launcher.launch(intent)
            },
            onDeleteClick = { plant ->
                deletePlantFromFirestore(plant)
            }
        )

        recyclerView.adapter = adapter

        loadUserPlants()
        saveFcmTokenToFirestore()
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
                        val id = document.id

                        val plant = PlantModel(name, desc, hour, minute, id)
                        plantList.add(plant)

                        ReminderUtils.scheduleWateringReminder(this, hour, minute, name)
                    }
                    adapter.notifyDataSetChanged()
//                  Toast.makeText(this, "Reminders set for all plants 🌱💧", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.e("PlantError", "Failed to fetch plants: ", e)
                    Toast.makeText(this, "Failed to load plants 😢", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in 😔", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deletePlantFromFirestore(plant: PlantModel) {
        FirebaseFirestore.getInstance()
            .collection("user_plants")
            .document(plant.id)
            .delete()
            .addOnSuccessListener {
                plantList.remove(plant)
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Deleted 🌿", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("DeleteError", "Failed to delete plant 😢", e)
                Toast.makeText(this, "Failed to delete 🥲", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveFcmTokenToFirestore() {
        val user = FirebaseAuth.getInstance().currentUser ?: return

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    val db = FirebaseFirestore.getInstance()
                    val tokenData = hashMapOf("fcmToken" to token)

                    db.collection("user_tokens")
                        .document(user.uid)
                        .set(tokenData)
                        .addOnSuccessListener {
                            Log.d("FCM", "Token saved to Firestore 💾")
                        }
                        .addOnFailureListener { e ->
                            Log.e("FCM", "Failed to save token 😢", e)
                        }
                } else {
                    Log.w("FCM", "Fetching FCM token failed", task.exception)
                }
            }
    }


}
