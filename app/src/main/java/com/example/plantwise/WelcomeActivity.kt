package com.example.plantwise

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.plantwise.databinding.ActivityWelcomeBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

        // Delay and navigate to LoginActivity
        lifecycleScope.launch {
            delay(3000)
            val intent = Intent(this@WelcomeActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
