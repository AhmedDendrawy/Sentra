package com.example.sentra // اتأكد إن دي نفس الـ package بتاعتك

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.sentra.ui.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class MyFirebaseMessagingService : FirebaseMessagingService() {

    // الدالة دي بتشتغل أول ما الإشعار يوصل للموبايل
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        android.util.Log.d("SENTRA_FCM", "🚨🚨 استلمت حاجة من فايربيز: ${remoteMessage.data}")
        // بنجيب العنوان والنص من الإشعار اللي مبعوت من فايربيز
        val title = remoteMessage.notification?.title ?: "تنبيه من Sentra"
        val message = remoteMessage.notification?.body ?: "يوجد تحديث جديد"

        // بنبعت البيانات دي للدالة اللي بترسم الإشعار على الشاشة
        showNotification(title, message)
    }

    // الدالة دي بترسم شكل الإشعار وتطلعه فوق في الشاشة
    private fun showNotification(title: String, message: String) {
        val channelId = "sentra_alerts_channel"

        // عشان لما اليوزر يدوس على الإشعار، يفتح التطبيق
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert) // أيقونة مؤقتة
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // أولوية عالية عشان ينزل من فوق
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // أندرويد 8 (Oreo) وأعلى بيحتاج حاجة اسمها Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Sentra Alerts",
                NotificationManager.IMPORTANCE_HIGH // صوت عالي
            )
            notificationManager.createNotificationChannel(channel)
        }

        // إظهار الإشعار برقم عشوائي عشان الإشعارات ماتمسحش بعض
        notificationManager.notify(Random.nextInt(), builder.build())
    }

    // الدالة دي بتشتغل لو فايربيز غيرت التوكن بتاع الموبايل لأي سبب
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        android.util.Log.d("SENTRA_FCM", "New Token Generated: $token")
        // المفروض هنا نبعت التوكن الجديد للباك إند
    }
}