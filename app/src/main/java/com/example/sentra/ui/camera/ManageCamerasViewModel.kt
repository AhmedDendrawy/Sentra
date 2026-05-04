package com.example.sentra.ui.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sentra.data.model.AddCameraRequest
import com.example.sentra.data.model.CameraItem
import com.example.sentra.data.model.UpdateCameraRequest
import com.example.sentra.data.repo.ManageCamerasRepository
import kotlinx.coroutines.launch

class ManageCamerasViewModel(private val repository: ManageCamerasRepository) : ViewModel() {

    private val _camerasList = MutableLiveData<List<CameraItem>>()
    val camerasList: LiveData<List<CameraItem>> get() = _camerasList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _deleteSuccess = MutableLiveData<Boolean>()
    val deleteSuccess: LiveData<Boolean> get() = _deleteSuccess

    private val _updateSuccess = MutableLiveData<Boolean>()
    val updateSuccess: LiveData<Boolean> get() = _updateSuccess

    private val _addSuccess = MutableLiveData<Boolean>()
    val addSuccess: LiveData<Boolean> get() = _addSuccess


    fun fetchCameras() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getCameras()
                if (response.isSuccessful && response.body() != null) {
                    _camerasList.value = response.body()
                } else {
                    _camerasList.value = emptyList() // عشان نظهر الـ Empty State
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                _camerasList.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // تأكد من نوع الـ cameraId (هنا مفترضين إنه Int)
    fun deleteCamera(cameraId: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.deleteCamera(cameraId)
                if (response.isSuccessful) {
                    _deleteSuccess.value = true
                    fetchCameras() // بنعمل Refresh للقائمة أوتوماتيك بعد المسح
                } else {
                    _errorMessage.value = "Failed to delete"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateCamera(cameraId: Int, request: UpdateCameraRequest) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.updateCamera(cameraId, request)
                if (response.isSuccessful) {
                    _updateSuccess.value = true
                } else {
                    _errorMessage.value = "Failed to update"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addCamera(request: AddCameraRequest) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.addCamera(request)
                if (response.isSuccessful) {
                    _addSuccess.value = true
                } else {
                    // نقلنا اللوجيك بتاعك هنا عشان نقرأ الإيرور من الباك إند
                    var backendErrorMsg = "Failed to add camera"
                    try {
                        val errorBodyString = response.errorBody()?.string()
                        if (errorBodyString != null) {
                            val jsonObject = org.json.JSONObject(errorBodyString)
                            if (jsonObject.has("message")) {
                                backendErrorMsg = jsonObject.getString("message")
                            } else if (jsonObject.has("errors")) {
                                backendErrorMsg = jsonObject.getString("errors")
                            }
                        }
                    } catch (e: Exception) {
                        backendErrorMsg = "Server error code: ${response.code()}"
                    }
                    _errorMessage.value = backendErrorMsg
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    class Factory(private val repository: ManageCamerasRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ManageCamerasViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ManageCamerasViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}