package com.example.sentra.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout // 🌟 ضفنا دي عشان الـ Empty State
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sentra.adapters.CameraAdapter
import com.example.sentra.model.CameraItem
import com.example.sentra.data.CamerasRepository
import com.example.sentra.R
import com.example.sentra.api.TokenManager
import com.google.android.material.button.MaterialButton

class HomeFragment : Fragment() {

    private lateinit var adapter: CameraAdapter
    // 🌟 عرفناهم هنا عشان نستخدمهم في الدالة اللي تحت 🌟
    private lateinit var rvCameras: RecyclerView
    private lateinit var layoutEmptyState: LinearLayout

    // --- (أ) استقبال الكاميرا الجديدة ---
    private val addCameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val newCamera = result.data?.getParcelableExtra<CameraItem>("NEW_CAMERA")
            if (newCamera != null) {
                CamerasRepository.camerasList.add(newCamera)
                adapter.notifyDataSetChanged()
                updateEmptyState() // 🌟 بنحدث الشاشة فوراً بعد إضافة الكاميرا 🌟
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // 🌟 ربط العناصر بالـ XML 🌟
        rvCameras = view.findViewById(R.id.rvCameras)
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState)
        val btnAdd = view.findViewById<MaterialButton>(R.id.btnAddCamera)

        // --- قراءة الاسم وعرضه ---
        val tvWelcome = view.findViewById<TextView>(R.id.tvWelcomeName)
        val userName = TokenManager.getUserName(requireContext()) ?: "User"
        tvWelcome.text = "Welcome, $userName"

        // --- (ب) إعداد الـ Adapter ---
        adapter = CameraAdapter(CamerasRepository.camerasList) { clickedCamera ->
            val intent = Intent(requireContext(), CameraStreamActivity::class.java)
            intent.putExtra("CAMERA_DATA", clickedCamera)
            startActivity(intent)
        }

        rvCameras.layoutManager = LinearLayoutManager(context)
        rvCameras.adapter = adapter

        // --- (ج) زر إضافة كاميرا ---
        btnAdd.setOnClickListener {
            val intent = Intent(requireContext(), AddCameraActivity::class.java)
            addCameraLauncher.launch(intent)
        }

        // 🌟 أول ما الشاشة تفتح نعمل الفحص 🌟
        updateEmptyState()

        return view
    }

    // --- (د) تحديث القائمة عند العودة ---
    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
        updateEmptyState() // 🌟 بنعمل فحص تاني تحسباً لأي تغيير حصل 🌟
    }

    // ==========================================
    // 🌟 الدالة السحرية للتحكم في الشاشة الفاضية 🌟
    // ==========================================
    private fun updateEmptyState() {
        if (CamerasRepository.camerasList.isEmpty()) {
            // لو اللستة فاضية: أظهر رسمة "لا توجد كاميرات" واخفي اللستة
            layoutEmptyState.visibility = View.VISIBLE
            rvCameras.visibility = View.GONE
        } else {
            // لو اللستة فيها كاميرات: اخفي الرسمة وأظهر اللستة
            layoutEmptyState.visibility = View.GONE
            rvCameras.visibility = View.VISIBLE
        }
    }
}