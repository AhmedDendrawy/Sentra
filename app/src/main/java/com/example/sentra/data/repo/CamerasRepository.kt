package com.example.sentra.data.repo // تأكد إن المسار صح حسب مشروعك

import com.example.sentra.api.ApiService
import com.example.sentra.data.model.CameraItem
import retrofit2.Response

class CamerasRepository(private val apiService: ApiService) {

    suspend fun getCameras(): Response<List<CameraItem>> {
        return apiService.getCameras()
    }

}