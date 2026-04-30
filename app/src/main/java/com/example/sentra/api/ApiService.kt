package com.example.sentra.api

import com.example.sentra.model.AddCameraRequest
import com.example.sentra.model.AlertItem
import com.example.sentra.model.CameraItem
import com.example.sentra.model.UpdateCameraRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("api/auth/register")
    suspend fun registerUser(
        @Body request: RegisterRequest
    ): Response<ResponseBody>

    @POST("api/auth/login")
    suspend fun loginUser(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("api/cameras")
    suspend fun addCamera(
        @Body request: AddCameraRequest
    ): Response<CameraItem>

    @GET("api/cameras")
    suspend fun getCameras(): Response<List<CameraItem>>

    @PATCH("api/cameras/{id}")
    suspend fun updateCamera(
        @Path("id") cameraId: Int,
        @Body request: UpdateCameraRequest
    ): Response<ResponseBody>

    @DELETE("api/cameras/{id}")
    suspend fun deleteCamera(
        @Path("id") cameraId: Int
    ): Response<ResponseBody>

    @GET("api/incidents")
    suspend fun getIncidents(): Response<List<AlertItem>>

    @POST("api/auth/refresh")
    fun refreshToken(@Body request: RefreshRequest): retrofit2.Call<RefreshResponse>

    @POST("api/user/change-password")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Body request: ChangePasswordRequest
    ): Response<GenericResponse>
}