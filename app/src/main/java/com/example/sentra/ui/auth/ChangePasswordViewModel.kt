package com.example.sentra.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sentra.data.remote.ChangePasswordRequest
import com.example.sentra.data.repo.AuthRepository
import kotlinx.coroutines.launch

class ChangePasswordViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> get() = _successMessage

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun changePassword(token: String, request: ChangePasswordRequest) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.changePassword(token, request)
                if (response.isSuccessful) {
                    // لو الباك إند باعت رسالة نجاح في الـ response body بنعرضها، لو لأ بنعرض رسالة ثابتة
                    _successMessage.value = response.body()?.message ?: "Password changed successfully"
                } else {
                    _errorMessage.value = "Current password is incorrect"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    class Factory(private val repository: AuthRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ChangePasswordViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ChangePasswordViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}