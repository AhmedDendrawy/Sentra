package com.example.sentra.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sentra.R
import com.example.sentra.api.RegisterRequest
import com.example.sentra.api.RetrofitClient
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignUp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val etUser = findViewById<TextInputEditText>(R.id.editTextUser)
        val etEmail = findViewById<TextInputEditText>(R.id.editTextEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.editTextPassword)
        val etConfirmPassword = findViewById<TextInputEditText>(R.id.confirmPassword)
        val btnRegister = findViewById<MaterialButton>(R.id.loginButton2)
        val tvLogin = findViewById<TextView>(R.id.tvCreateAccount3)
        val emailInputLayout = findViewById<TextInputLayout>(R.id.EmailInputLayout)

        tvLogin.setOnClickListener {
            finish()
        }

        btnRegister.setOnClickListener {
            val name = etUser.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            // تصفير الخطأ
            emailInputLayout.error = null

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🌟 التعديل الأول: الرسالة بالإنجليزي 🌟
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInputLayout.error = "Please enter a valid email address"
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnRegister.isEnabled = false
            btnRegister.text = "Registering..."

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val request = RegisterRequest(name, email, password)
                    val response = RetrofitClient.apiService.registerUser(request)

                    withContext(Dispatchers.Main) {
                        btnRegister.isEnabled = true
                        btnRegister.text = "Register"

                        if (response.isSuccessful) {
                            val successMsg = response.body()?.string() ?: "Account Created!"
                            Toast.makeText(this@SignUp, successMsg, Toast.LENGTH_LONG).show()
                            finish()
                        } else {
                            // 🌟 التعديل التاني: الرسالة بالإنجليزي 🌟
                            emailInputLayout.error = "This email is already registered"
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        btnRegister.isEnabled = true
                        btnRegister.text = "Register"
                        Toast.makeText(this@SignUp, "Network Error: Please check your internet", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}