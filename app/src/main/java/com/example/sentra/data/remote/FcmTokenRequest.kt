package com.example.sentra.data.remote

import com.google.gson.annotations.SerializedName

data class FcmTokenRequest(
    @SerializedName("fcmToken")
    val fcmToken: String
)