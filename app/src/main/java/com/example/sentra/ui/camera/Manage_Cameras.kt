package com.example.sentra.ui.camera

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sentra.adapters.ManageCamerasAdapter
import com.example.sentra.api.RetrofitClient
import com.example.sentra.data.model.CameraItem
import com.example.sentra.data.repo.ManageCamerasRepository
import com.example.sentra.databinding.ActivityManageCamerasBinding

class ManageCamerasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageCamerasBinding
    private lateinit var viewModel: ManageCamerasViewModel
    private lateinit var adapter: ManageCamerasAdapter
    private var camerasList = mutableListOf<CameraItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageCamerasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        setupViewModel()
        setupRecyclerView()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchCameras()
    }

    private fun initViews() {
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setupViewModel() {
        val apiService = RetrofitClient.getApiService(this)
        val repository = ManageCamerasRepository(apiService)
        val factory = ManageCamerasViewModel.Factory(repository)
        viewModel = ViewModelProvider(this, factory)[ManageCamerasViewModel::class.java]
    }

    private fun setupRecyclerView() {
        binding.rvManageCameras.layoutManager = LinearLayoutManager(this)
        adapter = ManageCamerasAdapter(
            camerasList,
            onEditClick = { camera ->
                val intent = Intent(this, EditCameraActivity::class.java)
                intent.putExtra("CAMERA_DATA", camera)
                startActivity(intent)
            },
            onDeleteClick = { camera ->
                showDeleteConfirmation(camera)
            }
        )
        binding.rvManageCameras.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.camerasList.observe(this) { cameras ->
            camerasList.clear()
            if (cameras.isNotEmpty()) {
                camerasList.addAll(cameras)
                binding.layoutEmptyState.visibility = View.GONE
                binding.rvManageCameras.visibility = View.VISIBLE
            } else {
                binding.layoutEmptyState.visibility = View.VISIBLE
                binding.rvManageCameras.visibility = View.GONE
            }
            adapter.notifyDataSetChanged()
        }

        viewModel.deleteSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Deleted Successfully", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.errorMessage.observe(this) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDeleteConfirmation(camera: CameraItem) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Camera")
        builder.setMessage("Are you sure you want to delete '${camera.name}'?")

        builder.setPositiveButton("Delete") { _, _ ->
            viewModel.deleteCamera(camera.cameraId)
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(
            ContextCompat.getColor(this, android.R.color.holo_red_dark)
        )
    }
}