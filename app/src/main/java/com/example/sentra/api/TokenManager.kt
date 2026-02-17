package com.example.sentra.api

import android.content.Context
import android.content.SharedPreferences

object TokenManager {
    private const val PREF_NAME = "SentraAuthPrefs"
    private const val KEY_TOKEN = "jwt_token"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_EMAIL = "user_email"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // 🌟 هنحفظ التوكن والاسم والإيميل مع بعض 🌟
    fun saveUserData(context: Context, token: String, name: String, email: String) {
        getPrefs(context).edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_USER_NAME, name)
            .putString(KEY_USER_EMAIL, email)
            .apply()
    }

    fun getToken(context: Context): String? {
        return getPrefs(context).getString(KEY_TOKEN, null)
    }

    fun getUserName(context: Context): String? {
        return getPrefs(context).getString(KEY_USER_NAME, "User")
    }
    fun getUserEmail(context: Context): String? {
        return getPrefs(context).getString(KEY_USER_EMAIL, "user@example.com")
    }

    fun clearData(context: Context) {
        getPrefs(context).edit().clear().apply()
    }
}