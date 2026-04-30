package com.example.sentra.api

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)
data class GenericResponse(
    val message: String
)