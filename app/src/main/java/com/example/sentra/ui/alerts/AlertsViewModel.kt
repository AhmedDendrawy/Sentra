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

    private val _alertsList = MutableLiveData<List<AlertItem>>()
    val alertsList: LiveData<List<AlertItem>> get() = _alertsList

    // حالة التحميل العادية (الدايرة اللي في النص)
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // 🌟 حالة السحب للتحديث (عشان الدايرة اللي بتنزل من فوق)
    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> get() = _isRefreshing

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    // 🌟 ضفنا متغير isSwipeRefresh عشان نعرف الريكويست جاي منين
    fun fetchIncidents(isSwipeRefresh: Boolean = false) {
        if (isSwipeRefresh) {
            _isRefreshing.value = true
        } else {
            _isLoading.value = true
        }

        viewModelScope.launch {
            try {
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
                // بنقفل علامات التحميل في كل الحالات
                _isLoading.value = false
                _isRefreshing.value = false
            }
        }
    }

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