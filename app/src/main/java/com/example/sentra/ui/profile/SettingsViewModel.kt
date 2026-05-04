package com.example.sentra.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sentra.data.repo.SettingsRepository
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> get() = _userName

    private val _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String> get() = _userEmail

    // عشان نبلغ الشاشة إن الخروج تم بنجاح
    private val _logoutEvent = MutableLiveData<Boolean>()
    val logoutEvent: LiveData<Boolean> get() = _logoutEvent

    fun loadUserData() {
        _userName.value = repository.getUserName()
        _userEmail.value = repository.getUserEmail()
    }

    fun logout() {
        val refreshToken = repository.getRefreshToken()

        viewModelScope.launch {
            try {
                // 1. نبلغ السيرفر بالخروج
                repository.logoutRemote(refreshToken)
            } catch (e: Exception) {
                // حتى لو السيرفر وقع، بنكمل مسح الداتا عشان اليوزر يعرف يخرج
            } finally {
                // 2. نمسح توكن فايربيز من الموبايل
                FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener {
                    // 3. نمسح الـ SharedPreferences ونبلغ الـ Fragment
                    repository.clearLocalData()
                    _logoutEvent.value = true
                }
            }
        }
    }

    class Factory(private val repository: SettingsRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SettingsViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}