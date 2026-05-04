package com.example.sentra.api

import android.content.Context
import com.example.sentra.data.remote.RefreshRequest
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(private val context: Context) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {

        if (response.request().url().encodedPath().contains("api/auth/refresh")) {
            TokenManager.clearData(context)
            return null
        }

        synchronized(this) {
            val refreshToken = TokenManager.getRefreshToken(context)

            if (refreshToken.isNullOrEmpty()) return null

            val refreshRequest = RefreshRequest(refreshToken)

            try {
                val apiResponse = RetrofitClient.getApiService(context).refreshToken(refreshRequest).execute()

                if (apiResponse.isSuccessful && apiResponse.body() != null) {
                    val newAccessToken = apiResponse.body()!!.accessToken
                    val newRefreshToken = apiResponse.body()!!.refreshToken

                    TokenManager.saveTokens(context, newAccessToken, newRefreshToken)

                    return response.request().newBuilder()
                        .header("Authorization", "Bearer $newAccessToken")
                        .build()
                } else {
                    TokenManager.clearData(context)
                    return null
                }
            } catch (e: Exception) {
                return null
            }
        }
    }
}