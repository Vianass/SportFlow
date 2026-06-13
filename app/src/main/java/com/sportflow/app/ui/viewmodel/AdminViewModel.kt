package com.sportflow.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportflow.app.data.remote.dto.ProfileDto
import com.sportflow.app.data.remote.dto.GameDto
import com.sportflow.app.data.repository.AdminRepository
import com.sportflow.app.model.AdminPlatformStats
import com.sportflow.app.model.AdminUserFilter
import com.sportflow.app.model.CreateTournamentRequest
import com.sportflow.app.model.Enrollment
import com.sportflow.app.model.ProfileStatus
import com.sportflow.app.model.Tournament
import com.sportflow.app.model.UserRole
import com.sportflow.app.model.UpdateTournamentRequest
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val pendingOrganizers: List<ProfileDto> = emptyList(),
    val users: List<ProfileDto> = emptyList(),
    val tournaments: List<Tournament> = emptyList(),
    val games: List<GameDto> = emptyList(),
    val teamNames: Map<Long, String> = emptyMap(),
    val stats: AdminPlatformStats = AdminPlatformStats(),
    val currentAdminId: String? = null,
    val selectedTournamentId: Long? = null,
    val selectedTournamentEnrollments: List<Enrollment> = emptyList(),
    val isLoadingEnrollments: Boolean = false,
    val enrollmentsErrorMessage: String? = null,
    val enrollmentSuccessMessage: String? = null,
    val updatingEnrollmentId: Long? = null,
    val operationInProgress: String? = null,
    val successMessage: String? = null,
    val isAuthorized: Boolean = false,
    val accessChecked: Boolean = false
)

class AdminViewModel(
    private val repository: AdminRepository = AdminRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    // Compatibility for the existing profile composables while Admin screens
    // migrate to the consolidated AdminUiState.
    val pendingUsers: StateFlow<List<ProfileDto>> = uiState
        .map { it.pendingOrganizers }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val errorMessage: StateFlow<String?> = uiState
        .map { it.errorMessage }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val isLoading: StateFlow<Boolean> = uiState
        .map { it.isLoading }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            startLoading()
            try {
                val admin = repository.requireAdmin()
                val (dashboard, users) = coroutineScope {
                    val dashboardRequest = async { repository.getDashboardData() }
                    val usersRequest = async { repository.getAllUsers() }
                    dashboardRequest.await() to usersRequest.await()
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = null,
                        pendingOrganizers = dashboard.pendingOrganizers,
                        users = users,
                        tournaments = dashboard.tournaments,
                        games = dashboard.games,
                        teamNames = dashboard.teamNames,
                        stats = dashboard.stats,
                        currentAdminId = admin.id,
                        isAuthorized = true,
                        accessChecked = true
                    )
                }
            } catch (exception: Exception) {
                failLoading(exception, accessChecked = true, revokeAccess = true)
            }
        }
    }

    fun loadUsers(
        role: UserRole? = null,
        status: ProfileStatus? = null,
        search: String = ""
    ) {
        viewModelScope.launch {
            startLoading()
            try {
                val users = repository.getUsers(AdminUserFilter(role, status, search))
                _uiState.update { it.copy(isLoading = false, errorMessage = null, users = users) }
            } catch (exception: Exception) {
                failLoading(exception)
            }
        }
    }

    fun loadPendingOrganizers() {
        viewModelScope.launch {
            startLoading()
            try {
                val pending = repository.getPendingOrganizers()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = null,
                        pendingOrganizers = pending,
                        stats = it.stats.copy(pendingOrganizers = pending.size)
                    )
                }
            } catch (exception: Exception) {
                failLoading(exception)
            }
        }
    }

    fun loadPendingUsers() = loadPendingOrganizers()

    fun loadStatistics() {
        viewModelScope.launch {
            startLoading()
            try {
                val stats = repository.getPlatformStats()
                _uiState.update { it.copy(isLoading = false, errorMessage = null, stats = stats) }
            } catch (exception: Exception) {
                failLoading(exception)
            }
        }
    }

    fun loadTournaments() {
        viewModelScope.launch {
            startLoading()
            try {
                val tournaments = repository.getAdminTournaments()
                _uiState.update { it.copy(isLoading = false, errorMessage = null, tournaments = tournaments) }
            } catch (exception: Exception) {
                failLoading(exception)
            }
        }
    }

    fun loadEnrollmentsForTournament(tournamentId: Long) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    selectedTournamentId = tournamentId,
                    selectedTournamentEnrollments = emptyList(),
                    isLoadingEnrollments = true,
                    enrollmentsErrorMessage = null,
                    enrollmentSuccessMessage = null
                )
            }

            try {
                val enrollments = repository.getEnrollmentsForTournament(tournamentId)
                _uiState.update {
                    if (it.selectedTournamentId == tournamentId) {
                        it.copy(
                            selectedTournamentEnrollments = enrollments.sortedByDescending { enrollment ->
                                enrollment.registeredAt ?: ""
                            },
                            isLoadingEnrollments = false,
                            enrollmentsErrorMessage = null
                        )
                    } else {
                        it
                    }
                }
            } catch (exception: Exception) {
                _uiState.update {
                    if (it.selectedTournamentId == tournamentId) {
                        it.copy(
                            isLoadingEnrollments = false,
                            enrollmentsErrorMessage = exception.message ?: "Erro ao carregar inscrições."
                        )
                    } else {
                        it
                    }
                }
            }
        }
    }

    fun approveEnrollment(enrollmentId: Long) {
        updateEnrollmentStatus(enrollmentId, "APROVADA", "Inscrição aprovada com sucesso.")
    }

    fun rejectEnrollment(enrollmentId: Long) {
        updateEnrollmentStatus(enrollmentId, "REJEITADA", "Inscrição rejeitada com sucesso.")
    }

    fun clearEnrollmentSelection() {
        _uiState.update {
            it.copy(
                selectedTournamentId = null,
                selectedTournamentEnrollments = emptyList(),
                isLoadingEnrollments = false,
                enrollmentsErrorMessage = null,
                enrollmentSuccessMessage = null,
                updatingEnrollmentId = null
            )
        }
    }

    fun createTournament(
        name: String,
        sport: String,
        category: String,
        date: String,
        location: String,
        capacity: String,
        price: String
    ) {
        if (_uiState.value.operationInProgress != null) return

        val trimmedName = name.trim()
        val trimmedDate = date.trim()
        val trimmedLocation = location.trim()
        val parsedCapacity = capacity.trim().toIntOrNull()
        val parsedPrice = price.trim().replace(',', '.').toDoubleOrNull()
        val validationError = when {
            trimmedName.isBlank() -> "Indica o nome do evento."
            !trimmedDate.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) -> "Usa a data no formato AAAA-MM-DD."
            trimmedLocation.isBlank() -> "Indica a localização."
            parsedCapacity == null || parsedCapacity <= 0 -> "A capacidade tem de ser maior que zero."
            parsedPrice == null || parsedPrice < 0.0 -> "O preço tem de ser igual ou maior que zero."
            else -> null
        }

        if (validationError != null) {
            _uiState.update { it.copy(errorMessage = validationError, successMessage = null) }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    operationInProgress = "createTournament",
                    errorMessage = null,
                    successMessage = null
                )
            }
            try {
                repository.createTournament(
                    CreateTournamentRequest(
                        name = trimmedName,
                        startDate = "${trimmedDate}T00:00:00Z",
                        status = "ABERTO",
                        sport = sport,
                        category = category,
                        location = trimmedLocation,
                        maxCapacity = parsedCapacity!!,
                        price = parsedPrice!!
                    )
                )
                val dashboard = repository.getDashboardData()
                _uiState.update {
                    it.copy(
                        operationInProgress = null,
                        tournaments = dashboard.tournaments,
                        games = dashboard.games,
                        teamNames = dashboard.teamNames,
                        stats = dashboard.stats,
                        successMessage = "Torneio criado com sucesso."
                    )
                }
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        operationInProgress = null,
                        errorMessage = exception.message ?: "Erro ao criar o torneio."
                    )
                }
            }
        }
    }

    fun updateTournament(
        tournamentId: Long,
        name: String,
        sport: String,
        category: String,
        date: String,
        location: String,
        capacity: String,
        price: String,
        status: String
    ) {
        if (_uiState.value.operationInProgress != null) return

        val trimmedName = name.trim()
        val trimmedDate = date.trim()
        val trimmedLocation = location.trim()
        val parsedCapacity = capacity.trim().toIntOrNull()
        val parsedPrice = price.trim().replace(',', '.').toDoubleOrNull()
        val normalizedStatus = status.trim().uppercase()
        val validationError = when {
            trimmedName.isBlank() -> "Indica o nome do evento."
            !trimmedDate.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) -> "Usa a data no formato AAAA-MM-DD."
            trimmedLocation.isBlank() -> "Indica a localização."
            parsedCapacity == null || parsedCapacity <= 0 -> "A capacidade tem de ser maior que zero."
            parsedPrice == null || parsedPrice < 0.0 -> "O preço tem de ser igual ou maior que zero."
            normalizedStatus !in setOf("ABERTO", "ATIVO", "CONCLUIDO", "FINALIZADO") -> "Seleciona um estado válido."
            else -> null
        }

        if (validationError != null) {
            _uiState.update { it.copy(errorMessage = validationError, successMessage = null) }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    operationInProgress = "updateTournament:$tournamentId",
                    errorMessage = null,
                    successMessage = null
                )
            }
            try {
                repository.updateTournament(
                    UpdateTournamentRequest(
                        id = tournamentId,
                        name = trimmedName,
                        startDate = "${trimmedDate}T00:00:00Z",
                        status = normalizedStatus,
                        sport = sport,
                        category = category,
                        location = trimmedLocation,
                        maxCapacity = parsedCapacity!!,
                        price = parsedPrice!!
                    )
                )
                val dashboard = repository.getDashboardData()
                _uiState.update {
                    it.copy(
                        operationInProgress = null,
                        tournaments = dashboard.tournaments,
                        games = dashboard.games,
                        teamNames = dashboard.teamNames,
                        stats = dashboard.stats,
                        successMessage = "Torneio atualizado com sucesso."
                    )
                }
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        operationInProgress = null,
                        errorMessage = exception.message ?: "Erro ao editar o torneio."
                    )
                }
            }
        }
    }

    fun approveOrganizer(id: String) = runUserOperation(id, "approve:$id") {
        repository.approveOrganizer(id)
    }

    fun approveUser(id: String) = approveOrganizer(id)

    fun rejectOrganizer(id: String) = runUserOperation(id, "reject:$id") {
        repository.rejectOrganizer(id)
    }

    fun rejectUser(id: String) = rejectOrganizer(id)

    fun blockUser(id: String) = runUserOperation(id, "block:$id") {
        repository.blockUser(id)
    }

    fun unblockUser(id: String) = runUserOperation(id, "unblock:$id") {
        repository.unblockUser(id)
    }

    fun refresh() = loadDashboard()

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearSuccess() {
        _uiState.update { it.copy(successMessage = null) }
    }

    private fun updateEnrollmentStatus(
        enrollmentId: Long,
        status: String,
        successMessage: String
    ) {
        val tournamentId = _uiState.value.selectedTournamentId ?: return
        if (_uiState.value.updatingEnrollmentId != null) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    updatingEnrollmentId = enrollmentId,
                    enrollmentsErrorMessage = null,
                    enrollmentSuccessMessage = null
                )
            }

            try {
                repository.updateEnrollmentStatus(tournamentId, enrollmentId, status)
                val (enrollments, dashboard) = coroutineScope {
                    val enrollmentsRequest = async {
                        repository.getEnrollmentsForTournament(tournamentId)
                    }
                    val dashboardRequest = async { repository.getDashboardData() }
                    enrollmentsRequest.await() to dashboardRequest.await()
                }

                _uiState.update {
                    it.copy(
                        updatingEnrollmentId = null,
                        selectedTournamentEnrollments = enrollments.sortedByDescending { enrollment ->
                            enrollment.registeredAt ?: ""
                        },
                        tournaments = dashboard.tournaments,
                        games = dashboard.games,
                        teamNames = dashboard.teamNames,
                        pendingOrganizers = dashboard.pendingOrganizers,
                        stats = dashboard.stats,
                        enrollmentSuccessMessage = successMessage
                    )
                }
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        updatingEnrollmentId = null,
                        enrollmentsErrorMessage = exception.message ?: "Erro ao atualizar inscrição."
                    )
                }
            }
        }
    }

    private fun runUserOperation(
        userId: String,
        operationKey: String,
        action: suspend () -> com.sportflow.app.model.AdminUserActionResult
    ) {
        if (_uiState.value.operationInProgress != null) return
        viewModelScope.launch {
            _uiState.update {
                it.copy(operationInProgress = operationKey, errorMessage = null, successMessage = null)
            }
            try {
                val result = action()
                val pending = repository.getPendingOrganizers()
                val users = repository.getAllUsers()
                val stats = repository.getPlatformStats()
                _uiState.update {
                    it.copy(
                        operationInProgress = null,
                        pendingOrganizers = pending,
                        users = users,
                        stats = stats,
                        successMessage = result.message
                    )
                }
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        operationInProgress = null,
                        errorMessage = exception.message ?: "Erro ao atualizar o utilizador."
                    )
                }
            }
        }
    }

    private fun startLoading() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
    }

    private fun failLoading(
        exception: Exception,
        accessChecked: Boolean = _uiState.value.accessChecked,
        revokeAccess: Boolean = false
    ) {
        _uiState.update {
            it.copy(
                isLoading = false,
                errorMessage = exception.message ?: "Erro ao carregar dados administrativos.",
                isAuthorized = if (revokeAccess) false else it.isAuthorized,
                accessChecked = accessChecked
            )
        }
    }
}
