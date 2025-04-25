package com.example.plantwise

import android.Manifest
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
import com.example.plantwise.ReminderUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

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
        setupBottomNav()
        checkNotificationPermission()
        saveFcmTokenToFirestore() // 🔥 Save FCM token if not already saved

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
                        ReminderUtils.scheduleWateringReminder(this, hour, minute, name)
                        startActivity(Intent(this, MyPlantsActivity::class.java))
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

    // 🌟 Save FCM token to Firestore
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
                            // Optional: log it or toast it
                        }
                        .addOnFailureListener { e ->
                            e.printStackTrace()
                        }
                }
            }
    }

    private fun setupBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_nursery -> {
                    val intent = Intent(this, NurseryActivity::class.java)
                    startActivity(intent)
                    true

                }

                R.id.nav_garden -> {
                    // Navigate to ReminderActivity when "My Garden" is tapped
//                    val intent = Intent(this, AddPlantActivity::class.java)
//                    startActivity(intent)
                    true
                }
                R.id.nav_garden1 -> {
                    // Navigate to ReminderActivity when "My Garden" is tapped
                    val intent = Intent(this, MyPlantsActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}
