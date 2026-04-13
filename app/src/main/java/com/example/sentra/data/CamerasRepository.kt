package com.example.sentra.data

import android.content.Context
import com.example.sentra.model.CameraItem
import com.example.sentra.api.TokenManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object CamerasRepository {

    var camerasList = ArrayList<CameraItem>()

    private const val PREFS_NAME = "CameraPrefs"
    private const val KEY_CAMERAS = "CameraList"

    fun init(context: Context) {
        camerasList.clear()

        val userEmail = TokenManager.getUserEmail(context) ?: "default_user"
        val uniqueUserKey = "${KEY_CAMERAS}_$userEmail"

        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonString = sharedPreferences.getString(uniqueUserKey, null)

        if (jsonString != null && jsonString != "[]") {
            val type = object : TypeToken<ArrayList<CameraItem>>() {}.type
            camerasList = Gson().fromJson(jsonString, type)
        }
        // The default fake cameras have been removed completely
        // New users will now start with an empty list
    }

    fun saveCameras(context: Context) {
        val userEmail = TokenManager.getUserEmail(context) ?: "default_user"
        val uniqueUserKey = "${KEY_CAMERAS}_$userEmail"

        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val jsonString = Gson().toJson(camerasList)

        editor.putString(uniqueUserKey, jsonString)
        editor.apply()
    }
}