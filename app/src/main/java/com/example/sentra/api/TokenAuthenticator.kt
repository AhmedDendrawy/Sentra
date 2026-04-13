package com.example.sentra.api

import android.content.Context
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(private val context: Context) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // 1. لو الريكويست اللي رجع 401 هو أصلاً ريكويست التجديد (Refresh)،
        // ده معناه إن الريفريش توكن نفسه مات! ولازم اليوزر يعمل لوجين من أول وجديد.
        if (response.request().url().encodedPath().contains("api/auth/refresh")) {
            TokenManager.clearData(context)
            // يفضل هنا تبعت Intent تودي اليوزر لشاشة اللوجين
            return null
        }

        // 2. استخدمنا synchronized عشان لو كذا ريكويست ضربوا 401 في نفس اللحظة،
        // الموبايل مايبعتش ريكويست تجديد لكل واحد، هو يجدد مرة واحدة بس.
        synchronized(this) {
            val refreshToken = TokenManager.getRefreshToken(context)

            // لو مفيش ريفريش توكن متسجل، مفيش حاجة نقدر نعملها
            if (refreshToken.isNullOrEmpty()) return null

            val refreshRequest = RefreshRequest(refreshToken)

            try {
                // 3. بنطلب التوكن الجديد بشكل متزامن (execute مش enqueue)
                // عشان نوقف الريكويست القديم لحد ما ده يخلص
                val apiResponse = RetrofitClient.getApiService(context).refreshToken(refreshRequest).execute()

                if (apiResponse.isSuccessful && apiResponse.body() != null) {
                    val newAccessToken = apiResponse.body()!!.accessToken
                    val newRefreshToken = apiResponse.body()!!.refreshToken

                    // 4. نحفظ التوكنات الجديدة في SharedPreferences
                    TokenManager.saveTokens(context, newAccessToken, newRefreshToken)

                    // 5. السحر بيحصل هنا: بنعيد الريكويست القديم اللي فشل، بس بالتوكن الجديد! 🚀
                    return response.request().newBuilder()
                        .header("Authorization", "Bearer $newAccessToken")
                        .build()
                } else {
                    // لو التجديد فشل لأي سبب
                    TokenManager.clearData(context)
                    return null
                }
            } catch (e: Exception) {
                return null
            }
        }
    }
}