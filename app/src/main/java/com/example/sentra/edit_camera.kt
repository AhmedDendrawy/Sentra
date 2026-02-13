package com.example.sentra

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class EditCameraActivity : AppCompatActivity() {

    private var cameraIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // تأكد إن اسم ملف الـ XML بتاعك هو نفسه ده
        setContentView(R.layout.activity_edit_camera)

        // 1. استقبال الـ Index بتاع الكاميرا من الصفحة اللي فاتت
        cameraIndex = intent.getIntExtra("CAMERA_INDEX", -1)

        // لو فيه خطأ ومفيش كاميرا اتبعتت، اقفل الصفحة
        if (cameraIndex == -1 || cameraIndex >= CamerasRepository.camerasList.size) {
            Toast.makeText(this, "Camera not found!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 2. نجيب الكاميرا من المخزن
        val currentCamera = CamerasRepository.camerasList[cameraIndex]

        // 3. تعريف العناصر (استخدمنا الـ IDs بتاعتك بالظبط)
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val tvBack = findViewById<TextView>(R.id.tvTitle) // لاحظ إن الـ ID بتاع كلمة Back عندك اسمه tvTitle

        val etName = findViewById<EditText>(R.id.etCameraName)
        val etIp = findViewById<EditText>(R.id.etCameraIp) // عدلناها حسب الـ XML
        val etLocation = findViewById<EditText>(R.id.etLocation)
        val btnSave = findViewById<MaterialButton>(R.id.btnSaveCamera) // عدلناها حسب الـ XML

        // 4. عرض البيانات الحالية للكاميرا في الحقول
        etName.setText(currentCamera.name)
        etLocation.setText(currentCamera.location)
        etIp.setText(currentCamera.rtspUrl)

        // زر الرجوع (سواء داس على السهم أو كلمة Back)
        btnBack.setOnClickListener { finish() }
        tvBack.setOnClickListener { finish() }

        // 5. زر الحفظ
        btnSave.setOnClickListener {
            val newName = etName.text.toString().trim()
            val newUrl = etIp.text.toString().trim()
            val newLocation = etLocation.text.toString().trim()

            // التأكد إن الاسم مش فاضي
            if (newName.isEmpty()) {
                etName.error = "Name is required"
                return@setOnClickListener
            }

            // إنشاء نسخة جديدة من الكاميرا بالبيانات المتحدثة
            val updatedCamera = currentCamera.copy(
                name = newName,
                location = newLocation,
                rtspUrl = newUrl
            )

            // استبدال القديمة بالجديدة في المخزن
            CamerasRepository.camerasList[cameraIndex] = updatedCamera

            // حفظ التغييرات في الموبايل للأبد
            CamerasRepository.saveCameras(this)

            Toast.makeText(this, "Camera updated successfully", Toast.LENGTH_SHORT).show()

            // قفل شاشة التعديل والرجوع للصفحة اللي قبلها
            finish()
        }
    }
}