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
import com.bumptech.glide.load.engine.DiskCacheStrategy // 🌟 الإمبورت ده ضفناه عشان الكاش
import com.example.sentra.R
import com.example.sentra.model.AlertItem
import java.text.SimpleDateFormat
import java.util.Locale

class AlertsAdapter(
    private var alerts: List<AlertItem>,
    private val onAlertClick: (AlertItem) -> Unit
) : RecyclerView.Adapter<AlertsAdapter.AlertViewHolder>() {

    class AlertViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvAlertTitle)
        val tvCamera: TextView = itemView.findViewById(R.id.tvCameraName)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val tvConfidence: TextView = itemView.findViewById(R.id.tvConfidence)
        val imgIcon: ImageView = itemView.findViewById(R.id.imgAlertIcon)
        val ivSnapshot: ImageView = itemView.findViewById(R.id.ivSnapshot)
        val viewIndicator: View = itemView.findViewById(R.id.viewIndicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_alert, parent, false)
        return AlertViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val item = alerts[position]
        val context = holder.itemView.context

        holder.tvTitle.text = item.type

        // 🌟 قراءة اسم الكاميرا
        holder.tvCamera.text = "Camera: ${item.camera?.name ?: "Unknown"}"

        // فرمتة الوقت
        holder.tvTime.text = formatIncidentTime(item.time)

        // نسبة الثقة
        val score = (item.confidenceScore * 100).toInt()
        holder.tvConfidence.text = "AI Confidence: $score%"

        // 🌟 تجميع لينك الصورة وعرضها في الكارت
        val imageUrl = "https://sentra.runasp.net${item.snapshotPath ?: ""}"

        // 🚀 التعديل هنا: تسريع التحميل وتقليل سحب النت والرامات
        Glide.with(context)
            .load(imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL) // بيحفظها في الموبايل عشان متتحملش تاني
            .thumbnail(0.25f) // بيعرض نسخة سريعة (25%) لحد ما الأصلية تحمل
            .centerCrop()
            .into(holder.ivSnapshot)

        // تلوين الأيقونات حسب نوع الحادثة
        when (item.type.lowercase()) {
            "fire" -> setColors(context, holder, R.color.alert_red_main, R.drawable.fire)
            "violence" -> setColors(context, holder, R.color.alert_orange_main, R.drawable.violence)
            "accident" -> setColors(context, holder, R.color.alert_yellow_main, R.drawable.accident)
            else -> setColors(context, holder, R.color.grey, R.drawable.fire)
        }

        // 🌟 الضغط لتكبير الصورة (بنباصي اللينك للدالة)
        holder.ivSnapshot.setOnClickListener {
            showImagePreviewDialog(context, imageUrl)
        }

        // الضغط على الكارت كله
        holder.itemView.setOnClickListener {
            onAlertClick(item)
        }
    }

    private fun setColors(context: Context, holder: AlertViewHolder, mainColorRes: Int, iconRes: Int) {
        val mainColor = ContextCompat.getColor(context, mainColorRes)
        holder.viewIndicator.setBackgroundColor(mainColor)
        holder.imgIcon.setImageResource(iconRes)
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

    // 🌟 الدالة الجديدة النظيفة لتكبير الصورة من اللينك مباشرة
    private fun showImagePreviewDialog(context: Context, imageUrl: String) {
        // 1. إعداد الـ ImageView
        val imageView = ImageView(context).apply {
            adjustViewBounds = true
            scaleType = ImageView.ScaleType.FIT_CENTER
            minimumHeight = 800 // عشان الصورة تاخد مساحة كويسة
        }

        // 2. تحميل الصورة باللينك فوراً
        // 🚀 التعديل هنا: هيقرأ الصورة من الكاش اللي اتحفظت فيه من بره في لمح البصر
        Glide.with(context)
            .load(imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)

        // 3. بناء الديالوج وعرضه
        val builder = AlertDialog.Builder(context)
        builder.setView(imageView)
        builder.setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }

        val dialog = builder.create()
        dialog.show()

        // 4. ضبط أبعاد الديالوج ليملا العرض
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