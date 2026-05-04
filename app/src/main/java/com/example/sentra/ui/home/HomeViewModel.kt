package com.example.sentra.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sentra.data.model.CameraItem
import com.example.sentra.data.repo.CamerasRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: CamerasRepository) : ViewModel() {

    private val _camerasList = MutableLiveData<List<CameraItem>>()
    val camerasList: LiveData<List<CameraItem>> get() = _camerasList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    // 🌟 دي عشان نهندل حالة لو السيشن خلصت (401)
    private val _unauthorizedEvent = MutableLiveData<Boolean>()
    val unauthorizedEvent: LiveData<Boolean> get() = _unauthorizedEvent

    fun fetchCameras() {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                // بنجيب الكاميرات من الـ Repository اللي إنت عامله
                val response = repository.getCameras()

                if (response.isSuccessful) {
                    _camerasList.value = response.body() ?: emptyList()
                } else {
                    when (response.code()) {
                        401 -> _unauthorizedEvent.value = true
                        404 -> _camerasList.value = emptyList() // لو 404 معناها مفيش كاميرات، فبنبعت لستة فاضية
                        else -> _errorMessage.value = "Server error: ${response.code()}"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network Error: Check your connection"
            } finally {
                _isLoading.value = false
            }
        }
    }

    class Factory(private val repository: CamerasRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}