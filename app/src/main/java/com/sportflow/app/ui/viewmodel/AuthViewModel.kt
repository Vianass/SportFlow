package com.sportflow.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportflow.app.data.remote.SupabaseProvider
import com.sportflow.app.data.repository.ProfilesRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val role: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val profilesRepository: ProfilesRepository = ProfilesRepository()
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                SupabaseProvider.client.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
                
                val user = SupabaseProvider.client.auth.currentUserOrNull()
                if (user != null) {
                    val profile = profilesRepository.getProfile(user.id)
                    // Mapeamos de volta para o que o UI espera se necessário
                    val dbRole = profile?.papel ?: "JOGADOR"
                    val uiRole = if (dbRole == "JOGADOR") "ATLETA" else dbRole
                    _authState.value = AuthState.Success(uiRole)
                } else {
                    _authState.value = AuthState.Error("Utilizador não encontrado")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Erro ao iniciar sessão")
            }
        }
    }

    fun signUp(email: String, password: String, name: String, role: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // Mapear ATLETA para JOGADOR para coincidir com o CHECK do SQL
                val dbRole = if (role == "ATLETA") "JOGADOR" else role

                SupabaseProvider.client.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                    // Metadados EXATOS para o teu Trigger public.handle_new_user()
                    data = buildJsonObject {
                        put("nome", name)
                        put("papel", dbRole)
                    }
                }
                
                // O perfil é criado automaticamente pelo Trigger on_auth_user_created
                _authState.value = AuthState.Success(role)
            } catch (e: Exception) {
                e.printStackTrace() // Ver no Logcat
                _authState.value = AuthState.Error(e.message ?: "Erro ao criar conta")
            }
        }
    }
    
    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }
}
