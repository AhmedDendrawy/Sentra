package com.example.sentra.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sentra.model.OnboardingItem
import com.example.sentra.R

class OnboardingAdapter(private val items: List<OnboardingItem>) :
    RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    inner class OnboardingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val ivOnboarding = view.findViewById<ImageView>(R.id.ivOnboarding)
        private val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        private val tvDescription = view.findViewById<TextView>(R.id.tvDescription)

        fun bind(item: OnboardingItem) {
            tvTitle.text = item.title
            tvDescription.text = item.description
            ivOnboarding.setImageResource(item.imageRes)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        return OnboardingViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_onboarding, parent, false)
        )
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}