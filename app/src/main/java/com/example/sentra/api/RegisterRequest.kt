package com.example.sentra.api

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)