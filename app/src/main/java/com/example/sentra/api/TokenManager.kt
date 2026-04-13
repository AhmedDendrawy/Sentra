package com.example.sentra.api

import android.content.Context
import android.content.SharedPreferences

object TokenManager {
    private const val PREF_NAME = "SentraAuthPrefs"
    private const val KEY_TOKEN = "jwt_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token" // 🌟 درج جديد للريفريش توكن
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_EMAIL = "user_email"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // الدالة بتاعتك زي ما هي بتاعة اللوجين
    fun saveUserData(context: Context, token: String?, name: String?, email: String?) {
        getPrefs(context).edit()
            .putString(KEY_TOKEN, token ?: "")
            .putString(KEY_USER_NAME, name ?: "User")
            .putString(KEY_USER_EMAIL, email ?: "user@example.com")
            .apply()
    }

    // 🌟 دالة جديدة: نحفظ بيها الريفريش توكن لوحده أو مع اللوجين
    fun saveRefreshToken(context: Context, refreshToken: String?) {
        getPrefs(context).edit()
            .putString(KEY_REFRESH_TOKEN, refreshToken ?: "")
            .apply()
    }

    // 🌟 دالة جديدة: المفتش السري هيستخدمها عشان يحفظ التوكنين الجداد مع بعض
    fun saveTokens(context: Context, accessToken: String, refreshToken: String) {
        getPrefs(context).edit()
            .putString(KEY_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .apply()
    }

    fun getToken(context: Context): String? {
        return getPrefs(context).getString(KEY_TOKEN, null)
    }

    // 🌟 دالة جديدة: بنجيب بيها الريفريش توكن عشان نبعته للسيرفر
    fun getRefreshToken(context: Context): String? {
        return getPrefs(context).getString(KEY_REFRESH_TOKEN, null)
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