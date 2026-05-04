package com.example.sentra.ui.camera

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.sentra.api.RetrofitClient
import com.example.sentra.data.model.AddCameraRequest
import com.example.sentra.data.repo.ManageCamerasRepository
import com.example.sentra.databinding.ActivityAddCameraBinding

class AddCameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddCameraBinding
    private lateinit var viewModel: ManageCamerasViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        setupViewModel()
        setupObservers()
    }

    private fun initViews() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnSaveCamera.setOnClickListener {
            val name = binding.etCameraName.text.toString().trim()
            val location = binding.etLocation.text.toString().trim()
            val streamUrl = binding.etCameraIp.text.toString().trim()

            if (name.isEmpty() || location.isEmpty() || streamUrl.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = AddCameraRequest(name, location, streamUrl)
            viewModel.addCamera(request)
        }
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
            binding.btnSaveCamera.text = if (isLoading) "Adding..." else "Add Camera"
        }

        viewModel.addSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Camera added successfully!", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            }
        }

        viewModel.errorMessage.observe(this) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }
}