package com.example.sentra.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.sentra.api.RetrofitClient
import com.example.sentra.api.TokenManager
import com.example.sentra.data.remote.LoginRequest
import com.example.sentra.data.repo.AuthRepository
import com.example.sentra.databinding.ActivityLoginBinding
import com.example.sentra.ui.main.MainActivity
import com.google.firebase.messaging.FirebaseMessaging

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (TokenManager.getToken(this) != null) {
            navigateToMain()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupViewModel()
        setupObservers()
    }

    private fun setupUI() {
        binding.tvCreateAccount.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
        }

        binding.loginButton.setOnClickListener { handleLoginClick() }
    }

    private fun setupViewModel() {
        val apiService = RetrofitClient.getApiService(this)
        val repository = AuthRepository(apiService)
        val factory = AuthViewModel.Factory(repository)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            setLoadingState(isLoading)
        }

        viewModel.loginResponse.observe(this) { loginData ->
            loginData?.let {
                TokenManager.saveUserData(this, it.accessToken, it.name, it.email)
                TokenManager.saveRefreshToken(this, it.refreshToken)
                Toast.makeText(this, "Welcome ${it.name}!", Toast.LENGTH_SHORT).show()
                navigateToMain()
            }
        }

        viewModel.errorMessage.observe(this) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                binding.EmailInputLayout.error = "Check credentials"
                binding.passwordInputLayout.error = "Check credentials"
            }
        }
    }

    private fun handleLoginClick() {
        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()

        if (validateInputs(email, password)) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                val fcmToken = if (task.isSuccessful) task.result else ""

                // 🌟 السطر ده اللي ضفناه عشان يطبع التوكن الحقيقي
                Log.d("MY_REAL_TOKEN", "FCM Token is: $fcmToken")

                viewModel.login(LoginRequest(email, password, fcmToken))
            }
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        var isValid = true
        binding.EmailInputLayout.error = null
        binding.passwordInputLayout.error = null

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.EmailInputLayout.error = "Please enter a valid email"
            isValid = false
        }
        if (password.isEmpty()) {
            binding.passwordInputLayout.error = "Please enter your password"
            isValid = false
        }
        return isValid
    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.loginButton.isEnabled = !isLoading
        binding.loginButton.text = if (isLoading) "Logging in..." else "Login"
        binding.editTextEmail.isEnabled = !isLoading
        binding.editTextPassword.isEnabled = !isLoading
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}