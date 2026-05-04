package com.example.sentra.data.repo

import com.example.sentra.api.ApiService
import com.example.sentra.data.model.AddCameraRequest
import com.example.sentra.data.model.CameraItem
import com.example.sentra.data.model.UpdateCameraRequest

class ManageCamerasRepository(private val apiService: ApiService) {

    suspend fun getCameras() = apiService.getCameras()

    suspend fun deleteCamera(cameraId: Int) = apiService.deleteCamera(cameraId)

    suspend fun addCamera(request: AddCameraRequest) = apiService.addCamera(request)

    suspend fun updateCamera(cameraId: Int, request: UpdateCameraRequest) = apiService.updateCamera(cameraId, request)
}