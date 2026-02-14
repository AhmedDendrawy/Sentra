package com.example.sentra.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize // دي السحر اللي بيخلينا ننقل البيانات
data class CameraItem(
    val name: String,
    val location: String,
    val lastIncident: String,
    val isOnline: Boolean,
    var rtspUrl: String = ""
) : Parcelable