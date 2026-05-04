package com.example.sentra.data.remote

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    @SerializedName("fcmToken") val fcmToken: String
)