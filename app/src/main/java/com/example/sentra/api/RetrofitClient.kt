package com.example.sentra.api

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://sentra.runasp.net/"

    fun getApiService(context: Context): ApiService {

        val okHttpClient = OkHttpClient.Builder()
            .protocols(listOf(okhttp3.Protocol.HTTP_1_1))
            .addInterceptor { chain ->

                val token = TokenManager.getToken(context)?.trim()

                val requestBuilder = chain.request().newBuilder()

                    .addHeader("Accept", "application/json")

                if (!token.isNullOrEmpty()) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }

                val request = requestBuilder.build()


                android.util.Log.d("SENTRA_DEBUG", "🚀 Sending to: ${request.url()}")
                android.util.Log.d("SENTRA_DEBUG", "🔑 Header Auth: ${request.header("Authorization")}")

                // إرسال الريكويست واستقبال الرد
                val response = chain.proceed(request)

                // طباعة كود الرد عشان نتأكد
                android.util.Log.d("SENTRA_DEBUG", "📥 Response Code: ${response.code()}")
                response
            }
            // 🌟 التعديل هنا: ربط المفتش السري بالـ Client 🌟
            .authenticator(TokenAuthenticator(context))
            .build()

        // 2. بناء الـ Retrofit باستخدام الـ Client الجديد
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // هنا الـ Client بقى جواه الإنترسبتور والمفتش مع بعض
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}