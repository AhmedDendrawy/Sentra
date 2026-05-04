package com.example.sentra.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sentra.adapters.CameraAdapter
import com.example.sentra.api.RetrofitClient
import com.example.sentra.api.TokenManager
import com.example.sentra.data.repo.CamerasRepository
import com.example.sentra.databinding.FragmentHomeBinding
import com.example.sentra.ui.camera.AddCameraActivity
import com.example.sentra.ui.camera.CameraStreamActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CameraAdapter
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val userName = TokenManager.getUserName(requireContext()) ?: "User"
        binding.tvWelcomeName.text = "Welcome, $userName"

        adapter = CameraAdapter(mutableListOf()) { clickedCamera ->
            val intent = Intent(requireContext(), CameraStreamActivity::class.java)
            intent.putExtra("CAMERA_DATA", clickedCamera)
            startActivity(intent)
        }

        binding.rvCameras.layoutManager = LinearLayoutManager(context)
        binding.rvCameras.adapter = adapter

        binding.btnAddCamera.setOnClickListener {
            startActivity(Intent(requireContext(), AddCameraActivity::class.java))
        }

        val apiService = RetrofitClient.getApiService(requireContext())
        val repository = CamerasRepository(apiService)
        val factory = HomeViewModel.Factory(repository)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        setupObservers()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchCameras()
    }

    private fun setupObservers() {
        viewModel.camerasList.observe(viewLifecycleOwner) { cameras ->
            adapter.updateData(cameras)
            updateEmptyState(cameras.isEmpty())
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (isLoading) {
                binding.rvCameras.visibility = View.GONE
                binding.layoutEmptyState.visibility = View.GONE
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                updateEmptyState(adapter.itemCount == 0)
            }
        }

        viewModel.unauthorizedEvent.observe(viewLifecycleOwner) { isUnauthorized ->
            if (isUnauthorized) {
                Toast.makeText(context, "Session expired, please login again", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.layoutEmptyState.visibility = View.VISIBLE
            binding.rvCameras.visibility = View.GONE
        } else {
            binding.layoutEmptyState.visibility = View.GONE
            binding.rvCameras.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}