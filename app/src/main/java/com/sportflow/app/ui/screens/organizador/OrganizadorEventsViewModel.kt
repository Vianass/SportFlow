package com.sportflow.app.ui.screens.organizador

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportflow.app.data.repository.EnrollmentsRepository
import com.sportflow.app.data.repository.TournamentsRepository
import com.sportflow.app.model.Enrollment
import com.sportflow.app.model.Tournament
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OrganizadorEventsUiState(
    val isLoading: Boolean = false,
    val tournaments: List<Tournament> = emptyList(),
    val errorMessage: String? = null,
    val isLoadingEnrollments: Boolean = false,
    val selectedTournamentEnrollments: List<Enrollment> = emptyList(),
    val enrollmentsErrorMessage: String? = null,
    val updatingEnrollmentId: Long? = null,
    val actionMessage: String? = null
)

class OrganizadorEventsViewModel(
    private val tournamentsRepository: TournamentsRepository = TournamentsRepository(),
    private val enrollmentsRepository: EnrollmentsRepository = EnrollmentsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrganizadorEventsUiState(isLoading = true))
    val uiState: StateFlow<OrganizadorEventsUiState> = _uiState.asStateFlow()

    init {
        loadTournaments()
    }

    fun loadTournaments() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            runCatching {
                tournamentsRepository.getCurrentOrganizerTournaments()
            }.onSuccess { tournaments ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        tournaments = tournaments.sortedByDescending { tournament -> tournament.createdAt ?: tournament.startDate },
                        errorMessage = null
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Erro ao carregar eventos."
                    )
                }
            }
        }
    }

    fun loadEnrollmentsForTournament(tournamentId: Long) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoadingEnrollments = true,
                    selectedTournamentEnrollments = emptyList(),
                    enrollmentsErrorMessage = null
                )
            }

            runCatching {
                enrollmentsRepository.getEnrollmentsForTournament(tournamentId)
            }.onSuccess { enrollments ->
                _uiState.update {
                    it.copy(
                        isLoadingEnrollments = false,
                        selectedTournamentEnrollments = enrollments.sortedByDescending { enrollment -> enrollment.registeredAt ?: "" },
                        enrollmentsErrorMessage = null
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoadingEnrollments = false,
                        enrollmentsErrorMessage = throwable.message ?: "Erro ao carregar inscrições."
                    )
                }
            }
        }
    }

    fun approveEnrollment(enrollmentId: Long, tournamentId: Long) {
        updateEnrollmentStatus(
            enrollmentId = enrollmentId,
            tournamentId = tournamentId,
            status = "APROVADA",
            successMessage = "Inscrição aprovada com sucesso."
        )
    }

    fun rejectEnrollment(enrollmentId: Long, tournamentId: Long) {
        updateEnrollmentStatus(
            enrollmentId = enrollmentId,
            tournamentId = tournamentId,
            status = "REJEITADA",
            successMessage = "Inscrição rejeitada com sucesso."
        )
    }

    fun clearActionMessage() {
        _uiState.update { it.copy(actionMessage = null) }
    }

    private fun updateEnrollmentStatus(
        enrollmentId: Long,
        tournamentId: Long,
        status: String,
        successMessage: String
    ) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    updatingEnrollmentId = enrollmentId,
                    enrollmentsErrorMessage = null,
                    actionMessage = null
                )
            }

            runCatching {
                enrollmentsRepository.updateEnrollmentStatus(enrollmentId, status)
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        updatingEnrollmentId = null,
                        actionMessage = successMessage
                    )
                }
                loadEnrollmentsForTournament(tournamentId)
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        updatingEnrollmentId = null,
                        enrollmentsErrorMessage = throwable.message ?: "Erro ao atualizar inscrição."
                    )
                }
            }
        }
    }
}
