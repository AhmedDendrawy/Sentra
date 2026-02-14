package com.example.sentra.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sentra.adapters.CameraAdapter
import com.example.sentra.model.CameraItem
import com.example.sentra.data.CamerasRepository
import com.example.sentra.R
import com.google.android.material.button.MaterialButton

class HomeFragment : Fragment() {

    private lateinit var adapter: CameraAdapter

    // --- (أ) استقبال الكاميرا الجديدة ---
    private val addCameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // استخراج الكاميرا
            val newCamera = result.data?.getParcelableExtra<CameraItem>("NEW_CAMERA")

            if (newCamera != null) {
                // 1. الإضافة في المخزن المشترك (عشان تسمع في كل حتة)
                CamerasRepository.camerasList.add(newCamera)

                // 2. تحديث الشاشة
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.rvCameras)
        val btnAdd = view.findViewById<MaterialButton>(R.id.btnAddCamera)

        // ملحوظة: شيلنا كود البيانات التجريبية من هنا لأننا حطيناه خلاص في CamerasRepository

        // --- (ب) إعداد الـ Adapter ---
        // بنبعتله القائمة المشتركة CamerasRepository.camerasList
        adapter = CameraAdapter(CamerasRepository.camerasList) { clickedCamera ->

            // الكود ده هيشتغل لما تضغط على أي كاميرا (يفتح البث)
            val intent = Intent(requireContext(), CameraStreamActivity::class.java)
            intent.putExtra("CAMERA_DATA", clickedCamera)
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        // --- (ج) زر إضافة كاميرا ---
        btnAdd.setOnClickListener {
            val intent = Intent(requireContext(), AddCameraActivity::class.java)
            addCameraLauncher.launch(intent)
        }

        return view
    }

    // --- (د) أهم دالة: تحديث القائمة عند العودة ---
    override fun onResume() {
        super.onResume()
        // لما ترجع من Settings أو ManageCameras، الدالة دي بتشتغل
        // وبتقول للأدابتـر: "بص في الـ Repository وشوف لو حاجة اتمسحت أو اتعدلت"
        adapter.notifyDataSetChanged()
    }
}