package com.example.sentra.model

enum class AlertType { FIRE, VIOLENCE, ACCIDENT }
data class AlertItem(
    val title: String,
    val cameraName: String,
    val time: String,
    val type: AlertType
)