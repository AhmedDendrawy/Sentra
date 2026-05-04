package com.example.sentra.data.remote

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)
data class GenericResponse(
    val message: String
)