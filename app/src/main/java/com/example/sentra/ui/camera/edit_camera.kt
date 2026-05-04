package com.example.sentra.ui.camera

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.sentra.api.RetrofitClient
import com.example.sentra.data.model.CameraItem
import com.example.sentra.data.model.UpdateCameraRequest
import com.example.sentra.data.repo.ManageCamerasRepository
import com.example.sentra.databinding.ActivityEditCameraBinding

class EditCameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditCameraBinding
    private lateinit var viewModel: ManageCamerasViewModel
    private var cameraItem: CameraItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraItem = intent.getParcelableExtra("CAMERA_DATA")

        if (cameraItem == null) {
            Toast.makeText(this, "Camera not found!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initViews()
        setupViewModel()
        setupObservers()
        populateData()
    }

    private fun initViews() {
        binding.btnBack.setOnClickListener { finish() }
        binding.tvTitle.setOnClickListener { finish() }

        binding.btnSaveCamera.setOnClickListener {
            val name = binding.etCameraName.text.toString().trim()
            val location = binding.etLocation.text.toString().trim()
            val streamUrl = binding.etCameraIp.text.toString().trim()

            if (name.isEmpty() || streamUrl.isEmpty()) {
                Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = UpdateCameraRequest(name, location, streamUrl)
            viewModel.updateCamera(cameraItem!!.cameraId, request)
        }
    }

    private fun populateData() {
        binding.etCameraName.setText(cameraItem?.name)
        binding.etLocation.setText(cameraItem?.location)
        binding.etCameraIp.setText(cameraItem?.streamURL)
    }

    private fun setupViewModel() {
        val apiService = RetrofitClient.getApiService(this)
        val repository = ManageCamerasRepository(apiService)
        val factory = ManageCamerasViewModel.Factory(repository)
        viewModel = ViewModelProvider(this, factory)[ManageCamerasViewModel::class.java]
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.btnSaveCamera.isEnabled = !isLoading
            binding.btnSaveCamera.text = if (isLoading) "Saving..." else "Save Changes"
        }

        viewModel.updateSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Updated Successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        viewModel.errorMessage.observe(this) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}