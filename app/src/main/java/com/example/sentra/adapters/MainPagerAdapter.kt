package com.example.sentra.adapters

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.sentra.ui.alerts.AlertsFragment
import com.example.sentra.ui.home.HomeFragment
import com.example.sentra.ui.profile.SettingsFragment

class MainPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> AlertsFragment()
            2 -> SettingsFragment()
            else -> HomeFragment()
        }
    }
}