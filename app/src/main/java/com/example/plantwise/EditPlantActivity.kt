package com.example.plantwise

import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class EditPlantActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var descEditText: EditText
    private lateinit var timeTextView: TextView
    private lateinit var saveButton: Button

    private var hour = 0
    private var minute = 0
    private var plantId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_plant)

        nameEditText = findViewById(R.id.plantNameInput)
        descEditText = findViewById(R.id.plantDescInput)
        timeTextView = findViewById(R.id.wateringTimePicker)
        saveButton = findViewById(R.id.savePlantBtn)

        // 🌟 Get plant data from intent
        plantId = intent.getStringExtra("plantId") ?: ""

        if (plantId.isEmpty()) {
            Toast.makeText(this, "Oops! Missing plant ID 😖", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        Log.d("EditPlantActivity", "Editing plant with ID: $plantId")

        // 🪴 Pre-fill data from intent
        nameEditText.setText(intent.getStringExtra("name") ?: "")
        descEditText.setText(intent.getStringExtra("desc") ?: "")
        hour = intent.getIntExtra("hour", 0)
        minute = intent.getIntExtra("minute", 0)
        updateTimeText()

        // ⏰ Time picker dialog
        timeTextView.setOnClickListener {
            TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                hour = selectedHour
                minute = selectedMinute
                updateTimeText()
            }, hour, minute, true).show()
        }

        // 💾 Save updated data
        saveButton.setOnClickListener {
            val updatedData = mapOf(
                "name" to nameEditText.text.toString(),
                "desc" to descEditText.text.toString(),
                "hour" to hour,
                "minute" to minute
            )

            FirebaseFirestore.getInstance()
                .collection("user_plants")
                .document(plantId)
                .update(updatedData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Plant updated 🌿", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { error ->
                    Log.e("EditPlantActivity", "Update failed: ${error.message}")
                    Toast.makeText(this, "Update failed 😢", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun updateTimeText() {
        timeTextView.text = String.format("Watering Time: %02d:%02d", hour, minute)
    }
}
