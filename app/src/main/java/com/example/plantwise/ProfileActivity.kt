package com.example.plantwise

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var nameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var logoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Firebase Auth init
        auth = FirebaseAuth.getInstance()

        // Bind UI views to code (make sure IDs match layout)
        nameTextView = findViewById(R.id.userName)
        emailTextView = findViewById(R.id.userEmail)
        logoutButton = findViewById(R.id.logoutBtn) // ✅ Corrected ID

        // Show current user data
        loadUserData()

        // Logout logic
        logoutButton.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Logged out successfully 💨", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun loadUserData() {
        val user = auth.currentUser
        if (user != null) {
            nameTextView.text = user.displayName ?: "No Name Found 😢"
            emailTextView.text = user.email ?: "No Email Found 📭"
        } else {
            Toast.makeText(this, "User not found! 🚨", Toast.LENGTH_SHORT).show()
        }
    }
}
