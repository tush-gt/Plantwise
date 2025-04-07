package com.example.plantwise

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class AddPlantActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(this, "Notification permission granted! 🎉", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "You won't get reminders without notification permission 💔", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_plant)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val nameInput: EditText = findViewById(R.id.plantNameInput)
        val descInput: EditText = findViewById(R.id.plantDescInput)
        val timePicker: TimePicker = findViewById(R.id.wateringTimePicker)
        val saveBtn: Button = findViewById(R.id.savePlantBtn)

        checkNotificationPermission()

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
                        Toast.makeText(this, "Plant saved! 🪴", Toast.LENGTH_SHORT).show()
                        scheduleWateringReminder(hour, minute, name)
                        val intent = Intent(this, MyPlantsActivity::class.java)
                        startActivity(intent)
                        finish()

                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to save 🌧️", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
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
