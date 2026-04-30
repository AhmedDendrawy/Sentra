package com.example.sentra.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sentra.adapters.CameraAdapter
import com.example.sentra.model.CameraItem
import com.example.sentra.R
import com.example.sentra.api.RetrofitClient
import com.example.sentra.api.TokenManager
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private lateinit var adapter: CameraAdapter
    private lateinit var rvCameras: RecyclerView
    private lateinit var layoutEmptyState: LinearLayout
    private lateinit var progressBar: ProgressBar // 🌟 تعريف عجلة التحميل

    private var camerasList = mutableListOf<CameraItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        rvCameras = view.findViewById(R.id.rvCameras)
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState)
        progressBar = view.findViewById(R.id.progressBar) // 🌟 ربط التحميل
        val btnAdd = view.findViewById<MaterialButton>(R.id.btnAddCamera)

        val tvWelcome = view.findViewById<TextView>(R.id.tvWelcomeName)
        val userName = TokenManager.getUserName(requireContext()) ?: "User"
        tvWelcome.text = "Welcome, $userName"

        adapter = CameraAdapter(camerasList) { clickedCamera ->
            val intent = Intent(requireContext(), CameraStreamActivity::class.java)
            intent.putExtra("CAMERA_DATA", clickedCamera)
            startActivity(intent)
        }

        rvCameras.layoutManager = LinearLayoutManager(context)
        rvCameras.adapter = adapter

        btnAdd.setOnClickListener {
            val intent = Intent(requireContext(), AddCameraActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        fetchCameras()
    }

    private fun fetchCameras() {
        val token = TokenManager.getToken(requireContext())

        if (token.isNullOrEmpty()) {
            updateEmptyState()
            return
        }

        // 🌟 إظهار التحميل وإخفاء باقي الشاشة قبل الريكويست
        progressBar.visibility = View.VISIBLE
        rvCameras.visibility = View.GONE
        layoutEmptyState.visibility = View.GONE

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.getApiService(requireContext()).getCameras()

                withContext(Dispatchers.Main) {
                    // 🌟 إخفاء التحميل أول ما الداتا توصل
                    progressBar.visibility = View.GONE

                    if (response.isSuccessful) {
                        val cameras = response.body()
                        camerasList.clear()
                        if (cameras != null) {
                            camerasList.addAll(cameras)
                        }
                        adapter.notifyDataSetChanged()
                        updateEmptyState()
                    } else if (response.code() == 401) {
                        handleUnauthorized()
                        updateEmptyState()
                    } else {
                        if (response.code() != 404) {
                            Toast.makeText(requireContext(), "Server error: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                        updateEmptyState()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // 🌟 إخفاء التحميل لو حصل خطأ في النت
                    progressBar.visibility = View.GONE
                    updateEmptyState()
                }
            }
        }
    }

    private fun handleUnauthorized() {
        // Optional: Clear token and send user back to login activity
    }

    private fun updateEmptyState() {
        if (camerasList.isEmpty()) {
            layoutEmptyState.visibility = View.VISIBLE
            rvCameras.visibility = View.GONE
        } else {
            layoutEmptyState.visibility = View.GONE
            rvCameras.visibility = View.VISIBLE
        }
    }
}