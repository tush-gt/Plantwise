package com.example.plantwise

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.plantwise.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {


    lateinit var loginBinding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        val view = loginBinding.root
        enableEdgeToEdge()
        setContentView(view)

        // 🔹 Navigate to Home Page on Sign In Button Click
        loginBinding.buttonSignin.setOnClickListener {
            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
            startActivity(intent)
            finish() // Close LoginActivity so user can't go back
        }

        loginBinding.textviewsignup.setOnClickListener {
            // TODO: Add navigation to Sign Up page
        }

        loginBinding.textViewForgotpassword.setOnClickListener {
            // TODO: Add navigation to Forgot Password page
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
