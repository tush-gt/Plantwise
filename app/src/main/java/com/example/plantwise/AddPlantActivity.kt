package com.example.plantwise

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class AddPlantActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_plant)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val nameInput: EditText = findViewById(R.id.plantNameInput)
        val descInput: EditText = findViewById(R.id.plantDescInput)
        val timePicker: TimePicker = findViewById(R.id.wateringTimePicker)
        val saveBtn: Button = findViewById(R.id.savePlantBtn)

        saveBtn.setOnClickListener {
            val name = nameInput.text.toString()
            val desc = descInput.text.toString()
            val hour = timePicker.hour
            val minute = timePicker.minute

            val uid = auth.currentUser?.uid
            if (uid != null && name.isNotEmpty()) {
                val plant = hashMapOf(
                    "name" to name,
                    "desc" to desc,
                    "hour" to hour,
                    "minute" to minute,
                    "userId" to uid
                )

                firestore.collection("user_plants").add(plant)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Plant saved!", Toast.LENGTH_SHORT).show()
                        scheduleWateringReminder(hour, minute, name)
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to save 🌧️", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun scheduleWateringReminder(hour: Int, minute: Int, plantName: String) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        val intent = Intent(this, ReminderReceiver::class.java).apply {
            putExtra("plantName", plantName)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
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
