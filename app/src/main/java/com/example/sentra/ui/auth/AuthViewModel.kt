package com.example.sentra.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sentra.data.repo.AuthRepository
import com.example.sentra.data.remote.LoginRequest
import com.example.sentra.data.remote.LoginResponse
import com.example.sentra.data.remote.RegisterRequest
import kotlinx.coroutines.launch
import okhttp3.ResponseBody

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _loginResponse = MutableLiveData<LoginResponse?>()
    val loginResponse: LiveData<LoginResponse?> get() = _loginResponse

    private val _registerResponse = MutableLiveData<ResponseBody?>()
    val registerResponse: LiveData<ResponseBody?> get() = _registerResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun login(loginRequest: LoginRequest) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.login(loginRequest)
                if (response.isSuccessful) {
                    _loginResponse.value = response.body()
                } else {
                    _errorMessage.value = "Login Failed: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network Error: Check your connection"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(registerRequest: RegisterRequest) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.register(registerRequest)
                if (response.isSuccessful) {
                    _registerResponse.value = response.body()
                } else {
                    _errorMessage.value = "Registration Failed: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network Error"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 🌟 السطور اللي جاية دي هي اللي كانت ناقصة ومسببة الإيرور:
    class Factory(private val repository: AuthRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}