package com.example.sentra.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CameraItem(
    val cameraId: Int,
    val name: String,
    val location: String,
    val streamURL: String,
    val status: String,
    val createdAt: String?,
    val lastActiveAt: String?
) : Parcelable

data class AddCameraRequest(
    val name: String,
    val location: String,
    val streamURL: String
)

data class UpdateCameraRequest(
    val name: String? = null,
    val location: String? = null,
    val streamURL: String? = null,
    val status: String? = null
)