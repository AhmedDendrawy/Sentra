package com.example.sentra.api

import android.content.Context
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Protocol
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://sentra.runasp.net/"

    fun getApiService(context: Context): ApiService {

        val okHttpClient = OkHttpClient.Builder()
            .protocols(listOf(Protocol.HTTP_1_1))
            .addInterceptor { chain ->
                val token = TokenManager.getToken(context)?.trim()
                val requestBuilder = chain.request().newBuilder()
                    .addHeader("Accept", "application/json")

                if (!token.isNullOrEmpty()) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }

                val request = requestBuilder.build()

                Log.d("SENTRA_DEBUG", "🚀 Sending to: ${request.url()}")
                Log.d("SENTRA_DEBUG", "🔑 Header Auth: ${request.header("Authorization")}")

                val response = chain.proceed(request)

                Log.d("SENTRA_DEBUG", "📥 Response Code: ${response.code()}")
                response
            }
            .authenticator(TokenAuthenticator(context))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}