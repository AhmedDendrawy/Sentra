package com.example.sentra.data.repo // تأكد إن المسار صح عندك

import com.example.sentra.api.ApiService
import com.example.sentra.data.remote.ChangePasswordRequest
import com.example.sentra.data.remote.LoginRequest
import com.example.sentra.data.remote.RegisterRequest

class AuthRepository(private val apiService: ApiService) {

    suspend fun login(loginRequest: LoginRequest) = apiService.loginUser(loginRequest)

    suspend fun changePassword(token: String, request: ChangePasswordRequest) = apiService.changePassword(token, request)

    suspend fun register(registerRequest: RegisterRequest) = apiService.registerUser(registerRequest)
}