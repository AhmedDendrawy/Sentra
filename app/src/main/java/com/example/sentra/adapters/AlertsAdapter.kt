package com.example.sentra.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.sentra.R
import com.example.sentra.data.model.AlertItem
import com.example.sentra.databinding.ItemAlertBinding
import java.text.SimpleDateFormat
import java.util.Locale

class AlertsAdapter(
    private var alerts: List<AlertItem>,
    private val onAlertClick: (AlertItem) -> Unit
) : RecyclerView.Adapter<AlertsAdapter.AlertViewHolder>() {

    class AlertViewHolder(val binding: ItemAlertBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val binding = ItemAlertBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlertViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val item = alerts[position]
        val context = holder.itemView.context

        holder.binding.tvAlertTitle.text = item.type
        holder.binding.tvCameraName.text = "Camera: ${item.camera?.name ?: "Unknown"}"
        holder.binding.tvTime.text = formatIncidentTime(item.time)

        val score = (item.confidenceScore * 100).toInt()
        holder.binding.tvConfidence.text = "AI Confidence: $score%"

        val imageUrl = "https://sentra.runasp.net${item.snapshotPath ?: ""}"

        Glide.with(context)
            .load(imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .thumbnail(0.25f)
            .centerCrop()
            .into(holder.binding.ivSnapshot)

        when (item.type.lowercase()) {
            "fire" -> setColors(context, holder, R.color.alert_red_main, R.drawable.fire)
            "violence" -> setColors(context, holder, R.color.alert_orange_main, R.drawable.violence)
            "accident" -> setColors(context, holder, R.color.alert_yellow_main, R.drawable.accident)
            else -> setColors(context, holder, R.color.grey, R.drawable.fire)
        }

        holder.binding.ivSnapshot.setOnClickListener {
            showImagePreviewDialog(context, imageUrl)
        }

        holder.binding.root.setOnClickListener {
            onAlertClick(item)
        }
    }

    private fun setColors(context: Context, holder: AlertViewHolder, mainColorRes: Int, iconRes: Int) {
        val mainColor = ContextCompat.getColor(context, mainColorRes)
        holder.binding.viewIndicator.setBackgroundColor(mainColor)
        holder.binding.imgAlertIcon.setImageResource(iconRes)
    }

    private fun formatIncidentTime(apiTime: String?): String {
        if (apiTime.isNullOrEmpty()) return "Unknown time"
        return try {
            val cleanTime = apiTime.replace("Z", "")
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US)
            val date = parser.parse(cleanTime)
            val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
            formatter.format(date!!)
        } catch (e: Exception) {
            apiTime
        }
    }

    private fun showImagePreviewDialog(context: Context, imageUrl: String) {
        val imageView = ImageView(context).apply {
            adjustViewBounds = true
            scaleType = ImageView.ScaleType.FIT_CENTER
            minimumHeight = 800
        }

        Glide.with(context)
            .load(imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)

        val builder = AlertDialog.Builder(context)
        builder.setView(imageView)
        builder.setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }

        val dialog = builder.create()
        dialog.show()

        dialog.window?.setLayout(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun getItemCount() = alerts.size

    fun updateData(newAlerts: List<AlertItem>) {
        alerts = newAlerts
        notifyDataSetChanged()
    }
}