package com.example.sentra.ui

import android.content.Intent
import android.os.Bundle
import android.view.View // 🌟 ضفنا دي عشان الـ Visibility
import android.widget.ImageView
import android.widget.LinearLayout // 🌟 ضفنا دي عشان الـ Empty State
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sentra.R
import com.example.sentra.adapters.ManageCamerasAdapter
import com.example.sentra.data.CamerasRepository
import com.example.sentra.model.CameraItem

class ManageCamerasActivity : AppCompatActivity() {

    private lateinit var adapter: ManageCamerasAdapter

    // 🌟 عرفناهم هنا عشان نستخدمهم في الدالة اللي تحت 🌟
    private lateinit var rvManageCameras: RecyclerView
    private lateinit var layoutEmptyState: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_cameras)

        // 🌟 ربط العناصر بالـ XML 🌟
        rvManageCameras = findViewById(R.id.rvManageCameras)
        layoutEmptyState = findViewById(R.id.layoutEmptyState)

        // زر الرجوع
        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        // إعداد القائمة
        rvManageCameras.layoutManager = LinearLayoutManager(this)

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

        rvManageCameras.adapter = adapter

        // 🌟 فحص الشاشة الفاضية أول ما نفتح 🌟
        updateEmptyState()
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

            // 3. حفظ التغييرات في الموبايل
            CamerasRepository.saveCameras(this)

            // 🌟 4. السحر هنا: لو مسحنا آخر كاميرا، الرسمة الفاضية تظهر فوراً 🌟
            updateEmptyState()

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
            updateEmptyState() // 🌟 بنحدث الشاشة الفاضية تحسباً لأي تغيير 🌟
        }
    }

    // ==========================================
    // 🌟 الدالة السحرية للتحكم في الشاشة الفاضية 🌟
    // ==========================================
    private fun updateEmptyState() {
        if (CamerasRepository.camerasList.isEmpty()) {
            // لو مفيش كاميرات: أظهر الرسمة الباهتة واخفي اللستة
            layoutEmptyState.visibility = View.VISIBLE
            rvManageCameras.visibility = View.GONE
        } else {
            // لو فيه كاميرات: اخفي الرسمة وأظهر اللستة
            layoutEmptyState.visibility = View.GONE
            rvManageCameras.visibility = View.VISIBLE
        }
    }
}