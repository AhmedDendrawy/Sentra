package com.example.sentra.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
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

    // The real list that will be filled from the backend
    private var camerasList = mutableListOf<CameraItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        rvCameras = view.findViewById(R.id.rvCameras)
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState)
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
        // Fetch cameras from backend every time the fragment becomes visible
        fetchCameras()
    }

    private fun fetchCameras() {
        val token = TokenManager.getToken(requireContext())

        // 1. Check if token exists before making the call
        if (token.isNullOrEmpty()) {
            updateEmptyState()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.getApiService(requireContext()).getCameras()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        // 2. Success! Even if body is empty, we handle it gracefully
                        val cameras = response.body()
                        camerasList.clear()
                        if (cameras != null) {
                            camerasList.addAll(cameras)
                        }
                        adapter.notifyDataSetChanged()
                        updateEmptyState()
                    } else if (response.code() == 401) {
                        // 3. Token expired or invalid - Redirect to login if necessary
                        handleUnauthorized()
                    } else {
                        // 4. Other server errors (only show toast for actual bugs)
                        if (response.code() != 404) {
                            Toast.makeText(requireContext(), "Server error: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                        updateEmptyState()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Network errors (like no internet) - handle silently or with a subtle UI
                    updateEmptyState()
                }
            }
        }
    }

    private fun handleUnauthorized() {
        // Optional: Clear token and send user back to login activity
        // val intent = Intent(requireContext(), LoginActivity::class.java)
        // intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        // startActivity(intent)
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