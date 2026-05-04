package com.example.sentra.data.repository

import com.example.sentra.api.ApiService

class AlertsRepository(private val apiService: ApiService) {

    suspend fun getIncidents() = apiService.getIncidents()

}