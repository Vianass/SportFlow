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
    data class Success(
        val role: String,
        val isPending: Boolean = false
    ) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val profilesRepository: ProfilesRepository = ProfilesRepository()
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    /**
     * @param selectedRole O cargo selecionado pelo utilizador no UI (ex: "ATLETA", "ORGANIZADOR")
     */
    fun login(email: String, password: String, selectedRole: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // 1. Tentar autenticação básica
                SupabaseProvider.client.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
                
                val user = SupabaseProvider.client.auth.currentUserOrNull()
                if (user != null) {
                    // 2. Procurar o perfil real na BD
                    val profile = profilesRepository.getProfile(user.id)
                    val dbRole = profile?.papel ?: "JOGADOR"
                    
                    // 3. Mapear UI "ATLETA" para DB "JOGADOR" para comparação
                    val normalizedSelectedRole = if (selectedRole == "ATLETA") "JOGADOR" else selectedRole
                    
                    // 4. Validar se os cargos coincidem
                    if (dbRole == normalizedSelectedRole) {
                        _authState.value = AuthState.Success(selectedRole)
                    } else {
                        // Se não coincidir, fazemos logout imediato por segurança
                        SupabaseProvider.client.auth.signOut()
                        _authState.value = AuthState.Error("Acesso negado: o perfil selecionado não é válido para esta conta.")
                    }
                } else {
                    _authState.value = AuthState.Error("Utilizador não encontrado")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _authState.value = AuthState.Error("Email ou palavra-passe incorretos")
            }
        }
    }

    fun signUp(email: String, password: String, name: String, role: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val dbRole = if (role == "ATLETA") "JOGADOR" else role

                SupabaseProvider.client.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                    data = buildJsonObject {
                        put("nome", name)
                        put("papel", dbRole)
                    }
                }

                _authState.value = AuthState.Success(
                    role = role,
                    isPending = true
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _authState.value = AuthState.Error(e.message ?: "Erro ao criar conta")
            }
        }
    }
    
    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }
}
