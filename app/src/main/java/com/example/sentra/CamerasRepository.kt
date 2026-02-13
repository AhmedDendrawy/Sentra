package com.example.sentra

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object CamerasRepository {

    // خليناها var بدل val عشان نقدر نحدث القائمة
    var camerasList = ArrayList<CameraItem>()

    private const val PREFS_NAME = "CameraPrefs"
    private const val KEY_CAMERAS = "CameraList"

    // 1. الدالة دي بنشغلها أول ما التطبيق يفتح عشان تحمل البيانات القديمة
    fun init(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonString = sharedPreferences.getString(KEY_CAMERAS, null)

        if (jsonString != null) {
            // لو فيه بيانات محفوظة، رجعها
            val type = object : TypeToken<ArrayList<CameraItem>>() {}.type
            camerasList = Gson().fromJson(jsonString, type)
        } else {
            // لو التطبيق لسه بيفتح لأول مرة خالص، حط الكاميرتين دول كتجربة
            camerasList.add(CameraItem("Front Door", "Main Entrance", "2 hours ago", true))
            camerasList.add(CameraItem("Parking Lot", "Outside", "No incidents", true))
        }
    }

    // 2. الدالة دي (اللي كان بيطلع عليها إيرور) بنشغلها لما نضيف أو نمسح كاميرا عشان تحفظ التعديل
    fun saveCameras(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // تحويل القائمة لنص عشان تتخزن
        val jsonString = Gson().toJson(camerasList)

        editor.putString(KEY_CAMERAS, jsonString)
        editor.apply()
    }
}