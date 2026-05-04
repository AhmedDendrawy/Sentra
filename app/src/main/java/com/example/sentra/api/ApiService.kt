package com.example.sentra.api

import com.example.sentra.data.model.AddCameraRequest
import com.example.sentra.data.model.AlertItem
import com.example.sentra.data.model.CameraItem
import com.example.sentra.data.model.UpdateCameraRequest
import com.example.sentra.data.remote.ChangePasswordRequest
import com.example.sentra.data.remote.GenericResponse
import com.example.sentra.data.remote.LoginRequest
import com.example.sentra.data.remote.LoginResponse
import com.example.sentra.data.remote.LogoutRequest
import com.example.sentra.data.remote.RefreshRequest
import com.example.sentra.data.remote.RefreshResponse
import com.example.sentra.data.remote.RegisterRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
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

    @POST("api/user/logout")
    suspend fun logout(@Body request: LogoutRequest): Response<Unit>
}