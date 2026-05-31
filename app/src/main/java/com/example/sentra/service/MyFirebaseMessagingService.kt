package com.example.sentra.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.sentra.api.RetrofitClient
import com.example.sentra.api.TokenManager
import com.example.sentra.data.remote.FcmTokenRequest
import com.example.sentra.ui.main.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("SENTRA_DEBUG", "FCM Message Received! Data: ${remoteMessage.data}")

        val sharedPrefs = applicationContext.getSharedPreferences("SentraSettings", Context.MODE_PRIVATE)
        val isNotifEnabled = sharedPrefs.getBoolean("enable_notifications", true)

        if (!isNotifEnabled) {
            Log.d("SENTRA_DEBUG", "Notifications are disabled by user. Ignoring message.")
            return
        }

        val title = remoteMessage.data["title"] ?: "Sentra Alert"
        val message = remoteMessage.data["body"] ?: "New incident detected"

        showNotification(title, message)
    }

    private fun showNotification(title: String, message: String) {
        val sharedPrefs = applicationContext.getSharedPreferences("SentraSettings", Context.MODE_PRIVATE)
        val isSoundEnabled = sharedPrefs.getBoolean("enable_sounds", true)

        val channelId = if (isSoundEnabled) "sentra_alerts_channel" else "sentra_alerts_silent_channel"

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra("open_fragment", "alerts")
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        if (isSoundEnabled) {
            builder.setPriority(NotificationCompat.PRIORITY_HIGH)
            builder.setDefaults(NotificationCompat.DEFAULT_SOUND or NotificationCompat.DEFAULT_VIBRATE)
        } else {
            builder.setPriority(NotificationCompat.PRIORITY_HIGH)
            builder.setSilent(true)
        }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel("sentra_alerts_channel") == null) {
                val channel = NotificationChannel(
                    "sentra_alerts_channel",
                    "Sentra Alerts",
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationManager.createNotificationChannel(channel)
            }

            if (notificationManager.getNotificationChannel("sentra_alerts_silent_channel") == null) {
                val silentChannel = NotificationChannel(
                    "sentra_alerts_silent_channel",
                    "Sentra Alerts (Silent)",
                    NotificationManager.IMPORTANCE_HIGH
                )
                silentChannel.setSound(null, null)
                notificationManager.createNotificationChannel(silentChannel)
            }
        }

        notificationManager.notify(Random.nextInt(), builder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("SENTRA_FCM", "New Token Generated: $token")

        val jwtToken = TokenManager.getToken(applicationContext)

        if (!jwtToken.isNullOrEmpty()) {
            val apiService = RetrofitClient.getApiService(applicationContext)
            val request = FcmTokenRequest(token)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = apiService.updateFcmToken(request)
                    if (response.isSuccessful) {
                        Log.d("SENTRA_FCM", "FCM Token updated on backend successfully")
                    } else {
                        Log.e("SENTRA_FCM", "Failed to update token: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.e("SENTRA_FCM", "Exception updating token: ${e.message}")
                }
            }
        }
    }
}