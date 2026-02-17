package com.example.sentra.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // الرابط الأساسي للسيرفر بتاعك
    private const val BASE_URL = "https://sentra.runasp.net/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // عشان يحول الـ JSON للكلاسات بتاعتنا
            .build()
            .create(ApiService::class.java)
    }
}