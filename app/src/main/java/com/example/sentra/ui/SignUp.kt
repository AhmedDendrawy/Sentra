package com.example.sentra.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
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
import org.json.JSONObject

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
        val passwordInputLayout = findViewById<TextInputLayout>(R.id.passwordInputLayout)

        tvLogin.setOnClickListener {
            finish()
        }

        // 🌟 Live Validation: Email 🌟
        etEmail.addTextChangedListener { text ->
            val email = text.toString().trim()
            if (email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInputLayout.error = "Invalid email format"
            } else {
                emailInputLayout.error = null // Clear error when valid
            }
        }

        // 🌟 Live Validation: Password 🌟
        etPassword.addTextChangedListener { text ->
            val password = text.toString()
            if (password.isNotEmpty() && password.length < 8) {
                passwordInputLayout.error = "At least 8 characters required"
            } else {
                passwordInputLayout.error = null // Clear error when valid
            }
        }

        btnRegister.setOnClickListener {
            val name = etUser.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            // Reset errors for a fresh check
            emailInputLayout.error = null
            passwordInputLayout.error = null

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Final check just in case they bypassed the live validation
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInputLayout.error = "Please enter a valid email address"
                return@setOnClickListener
            }

            if (password.length < 8) {
                passwordInputLayout.error = "Password must be at least 8 characters"
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                passwordInputLayout.error = "Passwords do not match"
                return@setOnClickListener
            }

            btnRegister.isEnabled = false
            btnRegister.text = "Registering..."

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val request = RegisterRequest(name, email, password)
                    val response = RetrofitClient.getApiService(this@SignUp).registerUser(request)
                    withContext(Dispatchers.Main) {
                        btnRegister.isEnabled = true
                        btnRegister.text = "Register"

                        if (response.isSuccessful) {
                            Toast.makeText(this@SignUp, "Account Created Successfully!", Toast.LENGTH_LONG).show()
                            finish()
                        } else {
                            var backendErrorMsg = "Registration failed"
                            try {
                                val errorBodyString = response.errorBody()?.string()
                                if (errorBodyString != null) {
                                    val jsonObject = JSONObject(errorBodyString)
                                    if (jsonObject.has("message")) {
                                        backendErrorMsg = jsonObject.getString("message")
                                    } else if (jsonObject.has("errors")) {
                                        backendErrorMsg = jsonObject.getString("errors")
                                    }
                                }
                            } catch (parseException: Exception) {
                                backendErrorMsg = "Server error code: ${response.code()}"
                            }

                            Toast.makeText(this@SignUp, backendErrorMsg, Toast.LENGTH_LONG).show()
                            emailInputLayout.error = "Check your info"
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        btnRegister.isEnabled = true
                        btnRegister.text = "Register"
                        Toast.makeText(this@SignUp, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}