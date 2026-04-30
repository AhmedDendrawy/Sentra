package com.example.sentra.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.sentra.R

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            checkDestination()
        }, 4000)
    }

    private fun checkDestination() {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)


        val isIconboardingFinished = sharedPreferences.getBoolean("isOnboardingFinished", false)

        if (!isIconboardingFinished) {

            startActivity(Intent(this, OnboardingActivity::class.java))
        } else {

            startActivity(Intent(this, LoginActivity::class.java))
        }


        finish()
    }
}

