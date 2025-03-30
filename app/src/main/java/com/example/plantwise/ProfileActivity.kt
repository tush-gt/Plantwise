package com.example.plantwise

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var userName: TextView
    private lateinit var userEmail: TextView
    private lateinit var userPassword: EditText
    private lateinit var logoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile) // Ensure this matches your XML file name

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Bind UI elements
        userName = findViewById(R.id.userName)
        userEmail = findViewById(R.id.userEmail)
        userPassword = findViewById(R.id.userPassword)
        logoutButton = findViewById(R.id.logoutButton)

        // Fetch user details
        loadUserData()

        // Logout button click listener
        logoutButton.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun loadUserData() {
        val user: FirebaseUser? = auth.currentUser
        if (user != null) {
            userName.text = user.displayName ?: "No Name"  // Firebase Auth doesn't store name by default
            userEmail.text = user.email ?: "No Email"
            userPassword.setText("********") // Do not show actual password for security
        } else {
            Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show()
        }
    }
}
