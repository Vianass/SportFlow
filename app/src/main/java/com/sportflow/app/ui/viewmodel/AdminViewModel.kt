package com.sportflow.app.ui.viewmodel

import android.util.Log
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

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadPendingUsers()
    }

    fun loadPendingUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val results = repository.getPendingProfiles()
                _pendingUsers.value = results
                _errorMessage.value = null
                Log.d("AdminViewModel", "Pedidos carregados: ${results.size}")
            } catch (e: Exception) {
                Log.e("AdminViewModel", "Erro ao carregar", e)
                _errorMessage.value = "Erro ao carregar: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun approveUser(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d("AdminViewModel", "Aprovar utilizador: $userId")
                repository.updateProfileStatus(userId, "ATIVO")
                
                // Pequena pausa para o Supabase processar e depois recarregar
                kotlinx.coroutines.delay(500)
                loadPendingUsers()
            } catch (e: Exception) {
                Log.e("AdminViewModel", "Erro ao aprovar", e)
                _errorMessage.value = "Erro ao aprovar: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun rejectUser(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.updateProfileStatus(userId, "REJEITADO")
                kotlinx.coroutines.delay(500)
                loadPendingUsers()
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao rejeitar: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
