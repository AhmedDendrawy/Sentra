package com.example.sentra.ui

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sentra.R
import com.example.sentra.api.RetrofitClient
import com.example.sentra.model.CameraItem
import com.example.sentra.model.UpdateCameraRequest
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditCameraActivity : AppCompatActivity() {

    private var cameraItem: CameraItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_camera)

        // 1. استلام بيانات الكاميرا بالكامل (بدل الـ Index)
        cameraItem = intent.getParcelableExtra("CAMERA_DATA")

        if (cameraItem == null) {
            Toast.makeText(this, "Camera not found!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 2. تعريف العناصر (نفس الـ IDs اللي في ملفك)
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val tvBack = findViewById<TextView>(R.id.tvTitle)
        val etName = findViewById<EditText>(R.id.etCameraName)
        val etIp = findViewById<EditText>(R.id.etCameraIp)
        val etLocation = findViewById<EditText>(R.id.etLocation)
        val btnSave = findViewById<MaterialButton>(R.id.btnSaveCamera)

        // 3. عرض البيانات الحالية
        etName.setText(cameraItem?.name)
        etLocation.setText(cameraItem?.location)
        etIp.setText(cameraItem?.streamURL)

        btnBack.setOnClickListener { finish() }
        tvBack.setOnClickListener { finish() }

        // 4. زر الحفظ (التعديل في السيرفر)
        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val location = etLocation.text.toString().trim()
            val streamUrl = etIp.text.toString().trim()

            if (name.isEmpty() || streamUrl.isEmpty()) {
                Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            updateCamera(name, location, streamUrl, btnSave)
        }
    }

    private fun updateCamera(name: String, location: String, streamUrl: String, btn: MaterialButton) {
        // تجهيز الطلب (Request)
        val request = UpdateCameraRequest(name, location, streamUrl)

        btn.isEnabled = false
        btn.text = "Saving..."

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // مناداة الـ API باستخدام الـ ID بتاع الكاميرا
                val response = RetrofitClient.getApiService(this@EditCameraActivity)
                    .updateCamera(cameraItem!!.cameraId, request)

                withContext(Dispatchers.Main) {
                    btn.isEnabled = true
                    btn.text = "Save Changes"

                    if (response.isSuccessful) {
                        Toast.makeText(this@EditCameraActivity, "Updated Successfully!", Toast.LENGTH_SHORT).show()
                        finish() // الرجوع بعد النجاح
                    } else {
                        Toast.makeText(this@EditCameraActivity, "Failed to update", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    btn.isEnabled = true
                    btn.text = "Save Changes"
                    Toast.makeText(this@EditCameraActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}