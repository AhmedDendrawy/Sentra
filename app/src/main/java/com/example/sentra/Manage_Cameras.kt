package com.example.sentra

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ManageCamerasActivity : AppCompatActivity() {

    private lateinit var adapter: ManageCamerasAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_cameras)

        // زر الرجوع
        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        // إعداد القائمة
        val recyclerView = findViewById<RecyclerView>(R.id.rvManageCameras)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // ربط الأدابتـر ببيانات الـ Repository
        adapter = ManageCamerasAdapter(
            CamerasRepository.camerasList,

            onEditClick = { camera ->
                // 1. نجيب رقم الكاميرا في القائمة (Index)
                val index = CamerasRepository.camerasList.indexOf(camera)

                // 2. نفتح شاشة التعديل ونبعتلها الرقم
                val intent = Intent(this, EditCameraActivity::class.java)
                intent.putExtra("CAMERA_INDEX", index)
                startActivity(intent)
            },

            onDeleteClick = { camera ->
                showDeleteConfirmation(camera)
            }
        )

        recyclerView.adapter = adapter
    }

    // دالة لإظهار رسالة التأكيد والحذف الفعلي
    private fun showDeleteConfirmation(camera: CameraItem) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Camera")
        builder.setMessage("Are you sure you want to delete '${camera.name}'?")

        builder.setPositiveButton("Delete") { _, _ ->
            // 1. الحذف من القائمة المشتركة
            CamerasRepository.camerasList.remove(camera)

            // 2. تحديث الشاشة الحالية فوراً
            adapter.notifyDataSetChanged()

            // 3. حفظ التغييرات في الموبايل عشان الكاميرا مترجعش تاني لما نقفل التطبيق (السطر اللي كان ناقص) <--
            CamerasRepository.saveCameras(this)

            Toast.makeText(this, "Deleted Successfully", Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        // تلوين زر الحذف بالأحمر
        val dialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(android.R.color.holo_red_dark))
    }
    // بنضيف الدالة دي عشان تعمل Refresh للقائمة لما ترجع من صفحة التعديل
    override fun onResume() {
        super.onResume()
        if (::adapter.isInitialized) {
            adapter.notifyDataSetChanged()
        }
    }
}