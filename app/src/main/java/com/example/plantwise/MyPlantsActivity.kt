package com.example.plantwise

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

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
            intent.putExtra("plantId", plant.id)  // 🌟 this is the missing piece
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
                        val id = document.id  // get the Firestore document ID
                        val plant = PlantModel(name, desc, hour, minute, id)

                        plantList.add(plant)

                        // 🌟 Here's the magic line that schedules reminders!
                        scheduleWateringReminder(hour, minute, name)
                    }
                    adapter.notifyDataSetChanged()
                    Toast.makeText(this, "Reminders set for all plants 🌱💧", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.e("PlantError", "Failed to fetch plants: ", e)
                    Toast.makeText(this, "Failed to load plants 😢", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in 😔", Toast.LENGTH_SHORT).show()
        }
    }




    private fun scheduleWateringReminder(hour: Int, minute: Int, plantName: String) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        val intent = Intent(this, ReminderReceiver::class.java).apply {
            putExtra("plantName", plantName)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this, plantName.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }




}
