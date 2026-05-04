package com.example.sentra.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.sentra.R
import com.example.sentra.adapters.OnboardingAdapter
import com.example.sentra.data.model.OnboardingItem
import com.example.sentra.databinding.ActivityOnboardingBinding
import com.example.sentra.ui.auth.LoginActivity
import com.google.android.material.tabs.TabLayoutMediator

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var adapter: OnboardingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val onboardingItems = listOf(
            OnboardingItem(
                "Add Your Security Cameras",
                "Connect and manage all your security cameras in one place. Monitor multiple locations effortlessly.",
                R.drawable.camera
            ),
            OnboardingItem(
                "AI Incident Detection",
                "Advanced AI technology detects violence, fire, and accidents in real-time to keep you safe.",
                R.drawable.shield
            ),
            OnboardingItem(
                "Real-Time Alerts",
                "Get instant notifications when incidents are detected. Stay informed wherever you are.",
                R.drawable.bell
            )
        )

        adapter = OnboardingAdapter(onboardingItems)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }.attach()

        binding.btnNext.setOnClickListener {
            if (binding.viewPager.currentItem + 1 < adapter.itemCount) {
                binding.viewPager.currentItem += 1
            } else {
                navigateToLogin()
            }
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == adapter.itemCount - 1) {
                    binding.btnNext.text = "Get Started"
                } else {
                    binding.btnNext.text = "Next"
                }
            }
        })
    }

    private fun navigateToLogin() {
        val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        prefs.edit().putBoolean("isOnboardingFinished", true).apply()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}