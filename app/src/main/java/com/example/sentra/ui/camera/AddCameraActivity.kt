package com.example.sentra.ui.camera

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.sentra.api.RetrofitClient
import com.example.sentra.data.model.AddCameraRequest
import com.example.sentra.data.remote.FcmTokenRequest
import com.example.sentra.data.repo.ManageCamerasRepository
import com.example.sentra.databinding.ActivityAddCameraBinding
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

                // 🌟 الخدعة السحرية: نبعت التوكن ونستنى الرد قبل ما نقفل الشاشة
                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val fcmToken = task.result
                        Log.d("SENTRA_TOKEN_TEST", "Sending Token to Backend: $fcmToken")

                        // نستخدم lifecycleScope عشان نضمن إن الريكويست يخلص بأمان
                        lifecycleScope.launch(Dispatchers.IO) {
                            try {
                                val apiService = RetrofitClient.getApiService(applicationContext)
                                val request = FcmTokenRequest(fcmToken)
                                val response = apiService.updateFcmToken(request)

                                if (response.isSuccessful) {
                                    Log.d("SENTRA_FCM", "✅ Token re-patched successfully after adding camera!")
                                } else {
                                    Log.e("SENTRA_FCM", "❌ Failed to re-patch token! Code: ${response.code()}")
                                }
                            } catch (e: Exception) {
                                Log.e("SENTRA_FCM", "⚠️ Crash in re-patch: ${e.message}")
                                e.printStackTrace()
                            } finally {
                                // 🌟 لازم نرجع للـ Main Thread عشان نقفل الشاشة بأمان
                                withContext(Dispatchers.Main) {
                                    setResult(RESULT_OK)
                                    finish()
                                }
                            }
                        }
                    } else {
                        // لو فايربيز نفسه معلق، نقفل الشاشة برضه عشان اليوزر ميفضلش محبوس
                        Log.e("SENTRA_FCM", "❌ Firebase failed to get token")
                        setResult(RESULT_OK)
                        finish()
                    }
                }
                // ⚠️ مسحنا الـ finish() اللي كانت هنا بره عشان الشاشة متقفلش قبل ما الريكويست يخلص
            }
        }

        viewModel.errorMessage.observe(this) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }
}