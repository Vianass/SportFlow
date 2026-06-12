package com.sportflow.app.ui.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportflow.app.data.repository.EnrollmentsRepository
import com.sportflow.app.model.Enrollment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserSubscriptionsUiState(
    val isLoading: Boolean = false,
    val enrollments: List<Enrollment> = emptyList(),
    val errorMessage: String? = null,
    val updatingPaymentEnrollmentId: Long? = null,
    val actionMessage: String? = null
)

class UserSubscriptionsViewModel(
    private val enrollmentsRepository: EnrollmentsRepository = EnrollmentsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserSubscriptionsUiState(isLoading = true))
    val uiState: StateFlow<UserSubscriptionsUiState> = _uiState.asStateFlow()

    init {
        loadEnrollments()
    }

    fun loadEnrollments() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, actionMessage = null) }

            runCatching {
                enrollmentsRepository.getCurrentUserEnrollments()
            }.onSuccess { enrollments ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        enrollments = enrollments,
                        errorMessage = null
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Erro ao carregar inscrições."
                    )
                }
            }
        }
    }

    fun markEnrollmentAsPaid(enrollmentId: Long) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    updatingPaymentEnrollmentId = enrollmentId,
                    errorMessage = null,
                    actionMessage = null
                )
            }

            runCatching {
                enrollmentsRepository.markEnrollmentAsPaid(enrollmentId)
                enrollmentsRepository.getCurrentUserEnrollments()
            }.onSuccess { enrollments ->
                _uiState.update {
                    it.copy(
                        enrollments = enrollments,
                        updatingPaymentEnrollmentId = null,
                        errorMessage = null,
                        actionMessage = "Pagamento confirmado com sucesso."
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        updatingPaymentEnrollmentId = null,
                        errorMessage = throwable.message ?: "Erro ao confirmar pagamento."
                    )
                }
            }
        }
    }

    fun clearActionMessage() {
        _uiState.update { it.copy(actionMessage = null) }
    }
}
