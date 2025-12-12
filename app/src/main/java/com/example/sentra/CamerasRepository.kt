package com.example.sentra

object CamerasRepository {
    val camerasList = ArrayList<CameraItem>()

    init {
        // بيانات تجريبية (عشان القائمة متبقاش فاضية)
        camerasList.add(CameraItem("Front Door", "Main Entrance", "2 hours ago", true))
        camerasList.add(CameraItem("Parking Lot", "Outside", "No incidents", true))
    }
}