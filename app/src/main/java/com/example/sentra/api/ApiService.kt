package com.example.sentra.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    // 1. رابط التسجيل (بيرجع نص)
    @POST("api/auth/register")
    suspend fun registerUser(
        @Body request: RegisterRequest
    ): Response<ResponseBody>

    // 2. رابط تسجيل الدخول (بيرجع التوكن)
    @POST("api/auth/login")
    suspend fun loginUser(
        @Body request: LoginRequest
    ): Response<LoginResponse>

}