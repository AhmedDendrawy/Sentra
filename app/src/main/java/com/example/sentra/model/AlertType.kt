package com.example.sentra.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AlertItem(
    val id: Int = 0, // لو السيرفر بيبعت ID للحادثة
    val cameraId: Int = 0,
    val cameraName: String? = "Unknown Camera",
    val type: String, // خليناها String بدل Enum عشان السيرفر
    val time: String,
    val confidenceScore: Double, // 🌟 المتغير اللي كان ناقص
    val snapshotUrl: String // 🌟 اللينك بتاع الصورة
) : Parcelable