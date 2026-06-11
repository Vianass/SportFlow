package com.sportflow.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportflow.app.data.remote.dto.ProfileDto
import com.sportflow.app.data.repository.ProfilesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminViewModel(
    private val repository: ProfilesRepository = ProfilesRepository()
) : ViewModel() {

    private val _pendingUsers = MutableStateFlow<List<ProfileDto>>(emptyList())
    val pendingUsers: StateFlow<List<ProfileDto>> = _pendingUsers.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadPendingUsers()
    }

    fun loadPendingUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _pendingUsers.value = repository.getPendingProfiles()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun approveUser(userId: String) {
        viewModelScope.launch {
            try {
                repository.updateProfileStatus(userId, "ATIVO")
                loadPendingUsers()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun rejectUser(userId: String) {
        viewModelScope.launch {
            try {
                repository.updateProfileStatus(userId, "REJEITADO")
                loadPendingUsers()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
