package com.example.sentra.adapters

import AlertsFragment
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.sentra.ui.HomeFragment
import com.example.sentra.ui.SettingsFragment

class MainPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    // هنا بتحدد عدد الشاشات اللي عندك تحت (غالباً 3)
    override fun getItemCount(): Int = 3

    // هنا بترتب الشاشات (من الشمال لليمين)
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()       // أول أيقونة (الرئيسية)
            1 -> AlertsFragment() // تاني أيقونة (استبدلها باسم شاشتك لو مختلف)
            2 -> SettingsFragment()   // تالت أيقونة (الإعدادات)
            else -> HomeFragment()
        }
    }
}