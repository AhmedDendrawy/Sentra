package com.example.sentra.api

// ده الكلاس اللي بيمثل البيانات اللي الباك إند طالبها منك
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)