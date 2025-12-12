package com.example.sentra

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class AddCameraActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_camera)

        // تعريف العناصر
        val etName = findViewById<EditText>(R.id.etCameraName)
        val etIp = findViewById<EditText>(R.id.etCameraIp)
        val etLocation = findViewById<EditText>(R.id.etLocation)
        val btnSave = findViewById<MaterialButton>(R.id.btnSaveCamera)
        val btnBack = findViewById<ImageView>(R.id.btnBack)

        // زر الرجوع
        btnBack.setOnClickListener { finish() }

        // زر الحفظ
        btnSave.setOnClickListener {
            val name = etName.text.toString()
            val location = etLocation.text.toString()

            if (name.isNotEmpty() && location.isNotEmpty()) {
                // 1. تجهيز الكاميرا الجديدة
                val newCamera = CameraItem(
                    name = name,
                    location = location,
                    lastIncident = "No incidents yet", // قيمة افتراضية
                    isOnline = true
                )

                // 2. تجهيز البيانات للإرسال
                val resultIntent = Intent()
                resultIntent.putExtra("NEW_CAMERA", newCamera) // "NEW_CAMERA" ده المفتاح السري

                // 3. إنهاء الصفحة بنجاح
                setResult(RESULT_OK, resultIntent)
                finish() // قفل الصفحة والرجوع
            } else {
                Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}