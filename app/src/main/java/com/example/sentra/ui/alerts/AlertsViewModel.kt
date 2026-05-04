package com.example.sentra.ui.alerts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sentra.data.model.AlertItem
import com.example.sentra.data.repository.AlertsRepository
import kotlinx.coroutines.launch

class AlertsViewModel(private val repository: AlertsRepository) : ViewModel() {

    // 1. الداتا (لستة الإشعارات)
    private val _alertsList = MutableLiveData<List<AlertItem>>()
    val alertsList: LiveData<List<AlertItem>> get() = _alertsList

    // 2. حالة التحميل (عشان الـ ProgressBar)
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // 3. رسائل الخطأ (زي الـ Toast)
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun fetchIncidents() {
        _isLoading.value = true // شغل التحميل

        viewModelScope.launch {
            try {
                // بيكلم الـ Repository اللي إحنا لسه عاملينه
                val response = repository.getIncidents()

                if (response.isSuccessful && response.body() != null) {
                    _alertsList.value = response.body()
                } else {
                    if (response.code() == 401) {
                        _errorMessage.value = "Session expired, please login again"
                    } else {
                        _errorMessage.value = "Error: ${response.code()}"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network Error: Check your connection"
            } finally {
                _isLoading.value = false // اقفل التحميل في كل الحالات
            }
        }
    }

    // الـ Factory ده ضروري عشان نقدر نبعت الـ Repository للـ ViewModel
    class Factory(private val repository: AlertsRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AlertsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AlertsViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}