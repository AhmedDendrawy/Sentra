package com.example.sentra.data.repo

import android.content.Context
import com.example.sentra.api.ApiService
import com.example.sentra.api.TokenManager
import com.example.sentra.data.remote.LogoutRequest

class SettingsRepository(private val context: Context, private val apiService: ApiService) {
    fun getUserName(): String {
        return TokenManager.getUserName(context) ?: "User"
    }

    fun getUserEmail(): String {
        return TokenManager.getUserEmail(context) ?: "user@example.com"
    }

    // 1. دالة بتجيب الريفريش توكن من الخزنة
    fun getRefreshToken(): String {
        return TokenManager.getRefreshToken(context) ?: ""
    }

    // 2. دالة بتكلم الباك إند عشان تعمل Logout
    suspend fun logoutRemote(refreshToken: String) {
        if (refreshToken.isNotEmpty()) {
            val request = LogoutRequest(refreshToken)
            apiService.logout(request)
        }
    }

    // 3. دالة بتمسح كل بيانات اليوزر من الموبايل
    fun clearLocalData() {
        TokenManager.clearData(context)
    }
}