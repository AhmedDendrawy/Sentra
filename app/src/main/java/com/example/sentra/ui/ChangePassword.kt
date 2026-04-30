package com.example.sentra.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sentra.R
import com.example.sentra.api.ChangePasswordRequest
import com.example.sentra.api.RetrofitClient
import com.example.sentra.api.TokenManager
import kotlinx.coroutines.launch

class ChangePassword : AppCompatActivity() {

    private lateinit var etCurrentPass: EditText
    private lateinit var etNewPass: EditText
    private lateinit var etConfirmPass: EditText
    private lateinit var btnSave: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

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

    private fun validateAndSubmit() {
        val currentPass = etCurrentPass.text.toString().trim()
        val newPass = etNewPass.text.toString().trim()
        val confirmPass = etConfirmPass.text.toString().trim()

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

        changePasswordApiCall(currentPass, newPass)
    }

    private fun changePasswordApiCall(currentPass: String, newPass: String) {
        progressBar.visibility = View.VISIBLE
        btnSave.isEnabled = false

        val savedToken = TokenManager.getToken(this@ChangePassword) ?: ""
        val requestBody = ChangePasswordRequest(currentPass, newPass)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getApiService(this@ChangePassword).changePassword("Bearer $savedToken", requestBody)

                if (response.isSuccessful) {
                    val successMsg = response.body()?.message ?: "Password changed successfully"
                    Toast.makeText(this@ChangePassword, successMsg, Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@ChangePassword, "Current password is incorrect", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                android.util.Log.e("SENTRA_ERROR", "Request failed: ${e.message}", e)
                Toast.makeText(this@ChangePassword, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                progressBar.visibility = View.GONE
                btnSave.isEnabled = true
            }
        }
    }
}