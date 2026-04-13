package com.example.sentra.api

data class LoginRequest(
    val email: String,
    val password: String,
    val fcmToken: String // 🌟 الإضافة الجديدة بتاعة فايربيز 🌟
)

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String, // لو الباك إند بيبعته
    val userId: Int,
    val name: String,
    val email: String,
    val role: String
)