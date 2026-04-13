package com.example.sentra.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sentra.R
import com.example.sentra.model.AlertItem

class AlertsAdapter(
    private var alerts: List<AlertItem>,
    private val onAlertClick: (AlertItem) -> Unit // عشان لو حبيت تفتح شاشة تفاصيل بعدين
) : RecyclerView.Adapter<AlertsAdapter.AlertViewHolder>() {

    class AlertViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvAlertTitle)
        val tvCamera: TextView = itemView.findViewById(R.id.tvCameraName)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val tvConfidence: TextView = itemView.findViewById(R.id.tvConfidence) // 🌟 الجديد
        val imgIcon: ImageView = itemView.findViewById(R.id.imgAlertIcon)
        val ivSnapshot: ImageView = itemView.findViewById(R.id.ivSnapshot) // 🌟 الجديد
        val viewIndicator: View = itemView.findViewById(R.id.viewIndicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_alert, parent, false)
        return AlertViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val item = alerts[position]
        val context = holder.itemView.context

        // 1. ربط النصوص
        holder.tvTitle.text = item.type // السيرفر بيبعت النوع كـ String (Fire, Violence)
        holder.tvCamera.text = "Camera: ${item.cameraName ?: "Unknown"}"
        holder.tvTime.text = item.time
        holder.tvConfidence.text = "AI Confidence: ${item.confidenceScore}%"

        // 2. تحميل صورة الحادثة باستخدام Glide
        Glide.with(context)
            .load(item.snapshotUrl)
            .centerCrop()
            .into(holder.ivSnapshot)

        // 3. تلوين الشريط والأيقونة حسب النوع (بدون Enum)
        // بنستخدم lowercase() عشان لو السيرفر بعت "FIRE" أو "Fire" يشتغل عادي
        when (item.type.lowercase()) {
            "fire" -> setColors(context, holder, R.color.alert_red_main, R.drawable.fire)
            "violence" -> setColors(context, holder, R.color.alert_orange_main, R.drawable.violence) // تأكد من اسم الأيقونة
            "accident" -> setColors(context, holder, R.color.alert_yellow_main, R.drawable.accident) // تأكد من اسم الأيقونة
            else -> setColors(context, holder, R.color.grey, R.drawable.fire) // حالة افتراضية
        }

        // 4. السحر هنا 🌟: تكبير الصورة لما اليوزر يضغط عليها
        holder.ivSnapshot.setOnClickListener {
            showImagePreviewDialog(context, item.snapshotUrl)
        }

        // 5. الضغط على الكارت كله
        holder.itemView.setOnClickListener {
            onAlertClick(item)
        }
    }

    private fun setColors(context: Context, holder: AlertViewHolder, mainColorRes: Int, iconRes: Int) {
        val mainColor = ContextCompat.getColor(context, mainColorRes)
        holder.viewIndicator.setBackgroundColor(mainColor)
        holder.imgIcon.setImageResource(iconRes)
    }

    // دالة إنشاء وإظهار الـ Dialog اللي بيكبر الصورة
    private fun showImagePreviewDialog(context: Context, imageUrl: String) {
        // بنعمل ImageView بالكود بدل ما نعمل ملف XML جديد
        val imageView = ImageView(context).apply {
            scaleType = ImageView.ScaleType.FIT_CENTER
            adjustViewBounds = true
            setPadding(0, 32, 0, 32)
        }

        // بنحمل الصورة جواه
        Glide.with(context)
            .load(imageUrl)
            .into(imageView)

        // بنعرضه في Dialog
        AlertDialog.Builder(context)
            .setView(imageView)
            .setPositiveButton("Close", null)
            .show()
    }

    override fun getItemCount() = alerts.size

    // دالة لتحديث اللستة لما الداتا تيجي من السيرفر
    fun updateData(newAlerts: List<AlertItem>) {
        alerts = newAlerts
        notifyDataSetChanged()
    }
}