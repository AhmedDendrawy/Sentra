package com.example.sentra.ui

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
        CamerasRepository.init(this)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)

        viewPager.adapter = MainPagerAdapter(this)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> viewPager.currentItem = 0
                R.id.nav_alerts -> viewPager.currentItem = 1
                R.id.nav_settings -> viewPager.currentItem = 2
            }
            true
        }


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