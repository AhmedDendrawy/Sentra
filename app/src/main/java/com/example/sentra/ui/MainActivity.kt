package com.example.sentra.ui

import AlertsFragment
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.sentra.data.CamerasRepository
import com.example.sentra.R
import com.example.sentra.adapters.MainPagerAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        CamerasRepository.init(this)
        setContentView(R.layout.activity_main)

        // 1. تعريف العناصر
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager) // تأكد إنك ضفت ViewPager2 في الـ XML

        // 2. تشغيل الموزع (Adapter)
        viewPager.adapter = MainPagerAdapter(this)

        // 3. لما تضغط على زرار تحت -> يغير الشاشة بالسحب
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> viewPager.currentItem = 0
                R.id.nav_alerts -> viewPager.currentItem = 1
                R.id.nav_settings -> viewPager.currentItem = 2
            }
            true
        }

        // 4. لما تسحب الشاشة بصباعك يمين وشمال -> يغير الزرار اللي منور تحت
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> bottomNav.selectedItemId = R.id.nav_home
                    1 -> bottomNav.selectedItemId = R.id.nav_alerts
                    2 -> bottomNav.selectedItemId = R.id.nav_settings
                }
            }
        })
    }
}