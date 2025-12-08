package com.example.sentra

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.postDelayed
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.logging.Handler

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        // تأخير بسيط (مثلاً 2 ثانية) لعرض اللوجو قبل الانتقال
        android.os.Handler(Looper.getMainLooper()).postDelayed({
            checkDestination()
        }, 2000)
    }

    private fun checkDestination() {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        // 1. هل انتهى من الـ Onboarding؟
        val isIconboardingFinished = sharedPreferences.getBoolean("isOnboardingFinished", false)

        if (!isIconboardingFinished) {
            // حالة 1: أول مرة يفتح التطبيق -> يروح Onboarding
            startActivity(Intent(this, OnboardingActivity::class.java))
        } else {
            // حالة 2: شاف الـ Onboarding قبل كدة.. نشوف هل مسجل دخول؟
            // (ممكن تضيف شرط تسجيل الدخول هنا لاحقاً)

            // حالياً هنوديه للـ Login بما إنه خلص Onboarding
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // مهم جداً: نقفل صفحة السبلاش عشان لما يرجع ميرجعلهاش تاني
        finish()
    }
}

