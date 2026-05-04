package com.example.sentra.ui.auth

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.example.sentra.api.RetrofitClient
import com.example.sentra.data.remote.RegisterRequest
import com.example.sentra.data.repo.AuthRepository
import com.example.sentra.databinding.ActivitySignUpBinding
import com.google.firebase.messaging.FirebaseMessaging // 🌟 الإمبورت الجديد

class SignUp : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupViewModel()
        setupObservers()
    }

    private fun setupUI() {
        binding.tvCreateAccount3.setOnClickListener { finish() }

        binding.editTextEmail.addTextChangedListener { text ->
            val email = text.toString().trim()
            if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.EmailInputLayout.error = "Invalid email format"
            } else {
                binding.EmailInputLayout.error = null
            }
        }

        binding.editTextPassword.addTextChangedListener { text ->
            val password = text.toString()
            if (password.isNotEmpty() && password.length < 8) {
                binding.passwordInputLayout.error = "At least 8 characters required"
            } else {
                binding.passwordInputLayout.error = null
            }
        }

        binding.loginButton2.setOnClickListener { handleRegisterClick() }
    }

    private fun setupViewModel() {
        val apiService = RetrofitClient.getApiService(this)
        val repository = AuthRepository(apiService)
        val factory = AuthViewModel.Factory(repository)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.loginButton2.isEnabled = !isLoading
            binding.loginButton2.text = if (isLoading) "Registering..." else "Register"
        }

        viewModel.registerResponse.observe(this) { response ->
            if (response != null) {
                Toast.makeText(this, "Account Created Successfully!", Toast.LENGTH_LONG).show()
                finish()
            }
        }

        viewModel.errorMessage.observe(this) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                binding.EmailInputLayout.error = "Check your info"
            }
        }
    }

    private fun handleRegisterClick() {
        val name = binding.editTextUser.text.toString().trim()
        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()
        val confirmPassword = binding.confirmPassword.text.toString().trim()

        binding.EmailInputLayout.error = null
        binding.passwordInputLayout.error = null

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.EmailInputLayout.error = "Please enter a valid email address"
            return
        }
        if (password.length < 8) {
            binding.passwordInputLayout.error = "Password must be at least 8 characters"
            return
        }
        if (password != confirmPassword) {
            binding.passwordInputLayout.error = "Passwords do not match"
            return
        }

        // 🌟 التعديل هنا: هنسحب توكن فايربيز قبل ما نبعت الريكويست للـ ViewModel
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            // لو نجحنا نجيب التوكن هناخده، لو فشل هنبعت String فاضي
            val fcmToken = if (task.isSuccessful) task.result else ""

            // نجهز الريكويست بالداتا كلها بما فيها التوكن الجديد ونبعته
            val request = RegisterRequest(name, email, password, fcmToken)
            viewModel.register(request)
        }
    }
}