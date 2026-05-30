package com.example.sentra.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
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
        // 🌟 التعديل هنا: بنقرأ من remoteMessage.data مباشرة
        val title = remoteMessage.data["title"] ?: "Sentra Alert"
        val message = remoteMessage.data["body"] ?: "New incident detected"

        // الدالة بتاعتك اللي بتبني الإشعار وتعرضه
        showNotification(title, message)
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "sentra_alerts_channel"

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Sentra Alerts",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(Random.nextInt(), builder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("SENTRA_FCM", "New Token Generated: $token")

        // 1. نتأكد إن اليوزر عامل Login عشان منبعتش توكن لليوزر وهو بره
        val jwtToken = TokenManager.getToken(applicationContext)

        if (!jwtToken.isNullOrEmpty()) {
            // 2. نجهز الـ ApiService
            val apiService = RetrofitClient.getApiService(applicationContext)
            val request = FcmTokenRequest(token)

            // 3. بنستخدم Coroutine Scope عشان نبعت الريكويست في الخلفية (Background Thread)
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