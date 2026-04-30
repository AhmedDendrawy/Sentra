package com.example.sentra.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AlertItem(
    val incidentId: Int = 0,
    val type: String,

    @SerializedName("timestamp")
    val time: String,

    val confidenceScore: Double,

    // 🌟 غيرنا اسم الحقل عشان يقرا اللينك اللي جاي من السيرفر
    @SerializedName("snapshotPath")
    val snapshotPath: String?,

    // 🌟 ضفنا الكاميرا كـ Object عشان نقرا الاسم من جواها
    val camera: CameraInfo?
) : Parcelable

// 🌟 كلاس جديد عشان يقرا بيانات الكاميرا اللي جوه الحادثة
@Parcelize
data class CameraInfo(
    val cameraId: Int,
    val name: String?,
    val location: String?
) : Parcelable