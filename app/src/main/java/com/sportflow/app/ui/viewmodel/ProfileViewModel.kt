package com.sportflow.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportflow.app.data.remote.SupabaseProvider
import com.sportflow.app.data.remote.dto.ProfileDto
import com.sportflow.app.data.repository.ProfilesRepository
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val profile: ProfileDto) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

class ProfileViewModel(
    private val repository: ProfilesRepository = ProfilesRepository()
) : ViewModel() {

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    init {
        fetchProfile()
    }

    fun fetchProfile() {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            try {
                val user = SupabaseProvider.client.auth.currentUserOrNull()
                if (user != null) {
                    val profile = repository.getProfile(user.id)
                    if (profile != null) {
                        _profileState.value = ProfileState.Success(profile)
                    } else {
                        _profileState.value = ProfileState.Error("Perfil não encontrado")
                    }
                } else {
                    _profileState.value = ProfileState.Error("Utilizador não autenticado")
                }
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(e.message ?: "Erro ao carregar perfil")
            }
        }
    }
}
