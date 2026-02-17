package com.example.sentra.api

// ده اللي بنبعته
data class LoginRequest(
    val email: String,
    val password: String
)

// 🌟 ده اللي اتعدل عشان يطابق الباك إند بتاعك 🌟
data class LoginResponse(
    val token: String,
    val userId: Int,
    val name: String,
    val email: String,
    val role: String
)