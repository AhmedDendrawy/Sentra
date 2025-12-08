package com.example.sentra

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.Button
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var btnNext: Button
    private lateinit var adapter: OnboardingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
        btnNext = findViewById(R.id.btnNext)

        val onboardingItems = listOf(
            OnboardingItem(
                "Add Your Security Cameras",
                "Connect and manage all your security cameras in one place. Monitor multiple locations effortlessly.",
                R.drawable.camera //
            ),
            OnboardingItem(
                "AI Incident Detection",
                "Advanced AI technology detects violence, fire, and accidents in real-time to keep you safe.",
                R.drawable.shield //
            ),
            OnboardingItem(
                "Real-Time Alerts",
                "Get instant notifications when incidents are detected. Stay informed wherever you are.",
                R.drawable.bell //
            )
        )

        adapter = OnboardingAdapter(onboardingItems)
        viewPager.adapter = adapter

        // ربط النقاط (TabLayout) مع الـ ViewPager
        TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()

        // التحكم في زر Next
        btnNext.setOnClickListener {
            if (viewPager.currentItem + 1 < adapter.itemCount) {
                // الانتقال للصفحة التالية
                viewPager.currentItem += 1
            } else {
                // الانتقال لصفحة تسجيل الدخول
                navigateToLogin()
            }
        }

        // (اختياري) تغيير نص الزر في آخر صفحة
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == adapter.itemCount - 1) {
                    btnNext.text = "Get Started"
                } else {
                    btnNext.text = "Next"
                }
            }
        })
    }

    private fun navigateToLogin() {
       // احفظ أن المستخدم شاهد الـ Onboarding حتى لا يظهر مرة أخرى (SharedPreferences)
        val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        prefs.edit().putBoolean("isOnboardingFinished", true).apply()

        val intent = Intent(this, LoginActivity::class.java) // تأكد من اسم صفحة الدخول عندك
        startActivity(intent)
        finish()
    }
}