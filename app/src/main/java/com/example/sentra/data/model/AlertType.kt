package com.example.sentra.data.model

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

    @SerializedName("snapshotPath")
    val snapshotPath: String?,

    val camera: CameraInfo?
) : Parcelable

@Parcelize
data class CameraInfo(
    val cameraId: Int,
    val name: String?,
    val location: String?
) : Parcelable