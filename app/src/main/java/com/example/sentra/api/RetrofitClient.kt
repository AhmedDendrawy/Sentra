package com.example.sentra.api

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://sentra.runasp.net/"

    fun getApiService(context: Context): ApiService {

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                // 🌟 ضفنا trim() عشان لو فيه أي مسافة مخفية في أول أو آخر التوكن تتمسح
                val token = TokenManager.getToken(context)?.trim()

                val requestBuilder = chain.request().newBuilder()
                    // 🌟 ضفنا Accept عشان بعض سيرفرات ASP.NET بترفض الريكويست من غيرها
                    .addHeader("Accept", "application/json")

                if (!token.isNullOrEmpty()) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }

                val request = requestBuilder.build()

                // طباعة تفاصيل الريكويست قبل ما يروح
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