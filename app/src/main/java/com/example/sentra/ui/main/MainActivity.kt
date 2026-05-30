package com.example.sentra.ui.main

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.sentra.R
import com.example.sentra.adapters.MainPagerAdapter
import com.example.sentra.api.RetrofitClient
import com.example.sentra.data.remote.FcmTokenRequest
import com.example.sentra.databinding.ActivityMainBinding
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("SENTRA_FCM", "Permission Granted! Initializing FCM...")
            initializeFcmAndChannel()
        } else {
            Log.d("SENTRA_FCM", "Permission Denied!")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPagerAndBottomNav()
        askNotificationPermission()
    }

    private fun setupViewPagerAndBottomNav() {
        binding.viewPager.adapter = MainPagerAdapter(this)

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> binding.viewPager.currentItem = 0
                R.id.nav_alerts -> binding.viewPager.currentItem = 1
                R.id.nav_settings -> binding.viewPager.currentItem = 2
            }
            true
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> binding.bottomNav.selectedItemId = R.id.nav_home
                    1 -> binding.bottomNav.selectedItemId = R.id.nav_alerts
                    2 -> binding.bottomNav.selectedItemId = R.id.nav_settings
                }
            }
        })
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                initializeFcmAndChannel()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            initializeFcmAndChannel()
        }
    }

    private fun initializeFcmAndChannel() {
        createNotificationChannel()
        checkAndUpdateFcmToken()
    }

    private fun checkAndUpdateFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val fcmToken = task.result
                Log.d("FCM_TOKEN", "MainActivity Token: $fcmToken")

                if (fcmToken.isNotEmpty()) {
                    lifecycleScope.launch {
                        try {
                            val apiService = RetrofitClient.getApiService(this@MainActivity)
                            val response = apiService.updateFcmToken(FcmTokenRequest(fcmToken))

                            if (response.isSuccessful) {
                                Log.d("FCM_TOKEN", "Token updated successfully on backend")
                            } else {
                                Log.e("FCM_TOKEN", "Failed to update token: ${response.code()}")
                            }
                        } catch (e: Exception) {
                            Log.e("FCM_TOKEN", "Error updating token", e)
                        }
                    }
                }
            } else {
                Log.e("FCM_TOKEN", "Fetching FCM registration token failed", task.exception)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "sentra_alerts_channel"
            val channelName = "Sentra Alerts"
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Channel for incident alerts"
                enableVibration(true)
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}