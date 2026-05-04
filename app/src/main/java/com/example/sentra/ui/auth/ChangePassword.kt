package com.example.sentra.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.sentra.R
import com.example.sentra.api.RetrofitClient
import com.example.sentra.api.TokenManager
import com.example.sentra.data.remote.ChangePasswordRequest
import com.example.sentra.data.repo.AuthRepository

class ChangePassword : AppCompatActivity() {

    private lateinit var viewModel: ChangePasswordViewModel
    private lateinit var etCurrentPass: EditText
    private lateinit var etNewPass: EditText
    private lateinit var etConfirmPass: EditText
    private lateinit var btnSave: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        initViews()
        setupViewModel()
        setupObservers()
    }

    private fun initViews() {
        val backBtn = findViewById<ImageView>(R.id.btnBack)
        val backTv = findViewById<TextView>(R.id.tvBackLabel)
        backBtn.setOnClickListener { finish() }
        backTv.setOnClickListener { finish() }

        etCurrentPass = findViewById(R.id.etCurrentPassword)
        etNewPass = findViewById(R.id.etNewPassword)
        etConfirmPass = findViewById(R.id.etConfirmPassword)
        btnSave = findViewById(R.id.btnUpdatePassword)
        progressBar = findViewById(R.id.progressBar)

        btnSave.setOnClickListener {
            validateAndSubmit()
        }
    }

    private fun setupViewModel() {
        val apiService = RetrofitClient.getApiService(this)
        val repository = AuthRepository(apiService)
        val factory = ChangePasswordViewModel.Factory(repository)
        viewModel = ViewModelProvider(this, factory)[ChangePasswordViewModel::class.java]
    }

    private fun setupObservers() {
        // مراقبة التحميل
        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            btnSave.isEnabled = !isLoading
            btnSave.text = if (isLoading) "Updating..." else "Update Password"
        }

        // مراقبة النجاح
        viewModel.successMessage.observe(this) { msg ->
            msg?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                finish() // نقفل الشاشة بعد النجاح
            }
        }

        // مراقبة الأخطاء
        viewModel.errorMessage.observe(this) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun validateAndSubmit() {
        val currentPass = etCurrentPass.text.toString().trim()
        val newPass = etNewPass.text.toString().trim()
        val confirmPass = etConfirmPass.text.toString().trim()

        // الـ Validation زي ما هو لأنه مسؤولية الـ UI
        if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPass.length < 8) {
            Toast.makeText(this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPass != confirmPass) {
            Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        // لو كله تمام، بنبعت الطلب للـ ViewModel
        val savedToken = TokenManager.getToken(this) ?: ""
        val requestBody = ChangePasswordRequest(currentPass, newPass)

        viewModel.changePassword("Bearer $savedToken", requestBody)
    }
}