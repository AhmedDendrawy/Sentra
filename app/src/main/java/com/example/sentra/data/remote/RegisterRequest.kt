package com.example.sentra.data.remote

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("fcmToken") val fcmToken: String // 🌟 رجعنا التوكن هنا
)