package com.example.sentra.api

// اللي هتبعته للسيرفر
data class RefreshRequest(
    val refreshToken: String
)

// اللي السيرفر هيرد بيه
data class RefreshResponse(
    val accessToken: String,
    val refreshToken: String // أحياناً السيرفر بيجدد الريفرش توكن كمان
)