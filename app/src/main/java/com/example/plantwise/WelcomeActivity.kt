package com.example.plantwise

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.plantwise.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {

    private lateinit var splashBinding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        splashBinding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(splashBinding.root)

        enableEdgeToEdge()

        // Load and start animation
        val alphaAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.splah_anim)
        splashBinding.textViewSplash.startAnimation(alphaAnimation)

        // Delay and navigate to LoginActivity3
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@WelcomeActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 5000) // 5-second delay
    }
}
