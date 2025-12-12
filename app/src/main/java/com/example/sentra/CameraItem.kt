package com.example.sentra

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize // دي السحر اللي بيخلينا ننقل البيانات
data class CameraItem(
    val name: String,
    val location: String,
    val lastIncident: String,
    val isOnline: Boolean
) : Parcelable