package com.sportflow.app.ui.screens.user

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import com.sportflow.app.data.local.SportFlowDatabase
import com.sportflow.app.data.local.entity.PendingEnrollmentEntity
import com.sportflow.app.data.local.network.NetworkConnectivity
import com.sportflow.app.data.local.repository.OfflineEnrollmentsRepository
import com.sportflow.app.data.local.repository.OfflineTournamentsRepository
import com.sportflow.app.data.local.sync.EnrollmentSyncWorker
import com.sportflow.app.data.local.sync.OfflineSyncScheduler
import com.sportflow.app.data.remote.SupabaseProvider
import com.sportflow.app.data.remote.dto.EnrollmentDto
import com.sportflow.app.data.repository.EnrollmentsRepository
import com.sportflow.app.data.repository.GamesRepository
import com.sportflow.app.data.repository.TournamentsRepository
import com.sportflow.app.model.Game
import com.sportflow.app.model.Tournament
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.from
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import java.io.IOException
import java.net.SocketTimeoutException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserEventsUiState(
    val isLoading: Boolean = false,
    val tournaments: List<Tournament> = emptyList(),
    val errorMessage: String? = null,
    val isEnrolling: Boolean = false,
    val enrollmentSuccessMessage: String? = null,
    val enrollmentErrorMessage: String? = null,
    val isLoadingLiveGames: Boolean = false,
    val liveGames: List<Game> = emptyList(),
    val liveGamesErrorMessage: String? = null,
    val isOfflineMode: Boolean = false,
    val pendingOfflineEnrollments: Set<Long> = emptySet(),
    val offlineMessage: String? = null,
    val isSyncing: Boolean = false,
    val pendingCount: Int = 0,
    val syncMessage: String? = null,
    val isNetworkAvailable: Boolean = true
)

class UserEventsViewModel @JvmOverloads constructor(
    application: Application,
    private val tournamentsRepository: TournamentsRepository = TournamentsRepository(),
    private val enrollmentsRepository: EnrollmentsRepository = EnrollmentsRepository(),
    private val gamesRepository: GamesRepository = GamesRepository(),
    private val offlineTournamentsRepository: OfflineTournamentsRepository =
        OfflineTournamentsRepository(SportFlowDatabase.getInstance(application).tournamentDao()),
    private val offlineEnrollmentsRepository: OfflineEnrollmentsRepository =
        OfflineEnrollmentsRepository(SportFlowDatabase.getInstance(application).pendingEnrollmentDao()),
    private val networkConnectivity: NetworkConnectivity = NetworkConnectivity(application)
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(UserEventsUiState(isLoading = true))
    val uiState: StateFlow<UserEventsUiState> = _uiState.asStateFlow()

    init {
        observePendingOfflineEnrollments()
        observeSyncState()
        schedulePendingSync()
        loadTournaments()
        loadLiveGames()
    }

    fun loadTournaments() {
        viewModelScope.launch {
            val isOnline = networkConnectivity.isOnline()
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    isNetworkAvailable = isOnline
                )
            }

            if (!isOnline) {
                loadCachedTournaments()
                return@launch
            }

            schedulePendingSync()
            refreshPendingEnrollments(reconcileRemote = true)

            val remoteResult = runCatching {
                tournamentsRepository.getTournaments()
            }

            remoteResult.onSuccess { tournaments ->
                runCatching {
                    offlineTournamentsRepository.cacheTournaments(tournaments)
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        tournaments = tournaments,
                        errorMessage = null,
                        isOfflineMode = false,
                        offlineMessage = null,
                        isNetworkAvailable = true
                    )
                }
            }.onFailure { throwable ->
                loadCachedTournaments(throwable)
            }
        }
    }

    fun enrollInTournament(tournamentId: Long) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isEnrolling = true,
                    enrollmentSuccessMessage = null,
                    enrollmentErrorMessage = null
                )
            }

            val userId = currentUserId()
            if (userId == null) {
                _uiState.update {
                    it.copy(
                        isEnrolling = false,
                        enrollmentErrorMessage = "Utilizador não autenticado. Inicia sessão novamente."
                    )
                }
                return@launch
            }

            if (offlineEnrollmentsRepository.existsPending(userId, tournamentId)) {
                showEnrollmentError("Esta inscrição já está pendente de sincronização.")
                return@launch
            }

            if (!networkConnectivity.isOnline()) {
                savePendingEnrollment(userId, tournamentId)
                return@launch
            }

            runCatching {
                enrollmentsRepository.enrollInTournament(tournamentId)
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isEnrolling = false,
                        enrollmentSuccessMessage = "Inscrição criada com sucesso. Aguarda aprovação do organizador.",
                        enrollmentErrorMessage = null
                    )
                }
            }.onFailure { throwable ->
                if (!networkConnectivity.isOnline() || throwable.isNetworkFailure()) {
                    savePendingEnrollment(userId, tournamentId)
                } else {
                    showEnrollmentError(throwable.toFriendlyEnrollmentMessage())
                }
            }
        }
    }

    fun clearEnrollmentFeedback() {
        _uiState.update {
            it.copy(
                enrollmentSuccessMessage = null,
                enrollmentErrorMessage = null
            )
        }
    }

    fun syncNow() {
        val userId = currentUserId()
        val isOnline = networkConnectivity.isOnline()

        if (userId == null) {
            _uiState.update {
                it.copy(syncMessage = "Inicia sessão novamente para sincronizar as inscrições.")
            }
            return
        }

        if (!isOnline) {
            _uiState.update {
                it.copy(
                    isNetworkAvailable = false,
                    syncMessage = "Sem ligação à internet. A sincronização será feita automaticamente."
                )
            }
            return
        }

        OfflineSyncScheduler.schedule(
            context = getApplication(),
            userId = userId,
            replaceExisting = true
        )
        _uiState.update {
            it.copy(
                isSyncing = true,
                isNetworkAvailable = true,
                syncMessage = "A sincronizar inscrições pendentes..."
            )
        }
        refreshPendingEnrollments(reconcileRemote = true)
    }

    fun clearSyncMessage() {
        _uiState.update { it.copy(syncMessage = null) }
    }

    fun refreshPendingEnrollments(reconcileRemote: Boolean = false) {
        val userId = currentUserId() ?: return

        viewModelScope.launch {
            if (reconcileRemote && networkConnectivity.isOnline()) {
                reconcilePendingWithRemote(userId)
            }

            updatePendingState(
                offlineEnrollmentsRepository.getPendingForUser(userId)
            )
        }
    }

    fun loadLiveGames() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoadingLiveGames = true,
                    liveGamesErrorMessage = null
                )
            }

            if (!networkConnectivity.isOnline()) {
                _uiState.update {
                    it.copy(
                        isLoadingLiveGames = false,
                        liveGames = emptyList(),
                        liveGamesErrorMessage = null
                    )
                }
                return@launch
            }

            runCatching {
                gamesRepository.getOngoingGames()
            }.onSuccess { games ->
                _uiState.update {
                    it.copy(
                        isLoadingLiveGames = false,
                        liveGames = games,
                        liveGamesErrorMessage = null
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoadingLiveGames = false,
                        liveGamesErrorMessage = throwable.message ?: "Erro ao carregar eventos a decorrer."
                    )
                }
            }
        }
    }

    private suspend fun loadCachedTournaments(remoteError: Throwable? = null) {
        val cachedTournaments = runCatching {
            offlineTournamentsRepository.observeAll().first()
        }.getOrElse { emptyList() }

        val offlineMessage = if (networkConnectivity.isOnline()) {
            "Não foi possível atualizar os torneios. A mostrar dados guardados localmente."
        } else {
            "Sem ligação à internet. A mostrar torneios guardados localmente."
        }

        _uiState.update {
            it.copy(
                isLoading = false,
                tournaments = cachedTournaments,
                errorMessage = if (cachedTournaments.isEmpty()) {
                    remoteError?.message
                        ?: "Sem ligação à internet e sem torneios guardados neste dispositivo."
                } else {
                    null
                },
                isOfflineMode = true,
                offlineMessage = offlineMessage,
                isNetworkAvailable = networkConnectivity.isOnline()
            )
        }
    }

    private fun observePendingOfflineEnrollments() {
        val userId = currentUserId() ?: return

        viewModelScope.launch {
            offlineEnrollmentsRepository.observePendingForUser(userId).collect { enrollments ->
                updatePendingState(enrollments)
            }
        }
    }

    private fun updatePendingState(
        enrollments: List<PendingEnrollmentEntity>
    ) {
        _uiState.update {
            it.copy(
                pendingOfflineEnrollments = enrollments
                    .map { enrollment -> enrollment.tournamentId }
                    .toSet(),
                pendingCount = enrollments.size
            )
        }
    }

    private suspend fun reconcilePendingWithRemote(userId: String) {
        val localPending = offlineEnrollmentsRepository.getPendingForUser(userId)
        if (localPending.isEmpty()) return

        val remoteTournamentIds = runCatching {
            SupabaseProvider.client
                .from("inscricoes")
                .select {
                    filter {
                        eq("utilizador_id", userId)
                    }
                }
                .decodeList<EnrollmentDto>()
                .map { enrollment -> enrollment.tournamentId }
                .toSet()
        }.getOrElse { return }

        localPending
            .filter { enrollment -> enrollment.tournamentId in remoteTournamentIds }
            .forEach { enrollment ->
                val deleted = offlineEnrollmentsRepository.deleteById(enrollment.localId)
                if (!deleted) {
                    offlineEnrollmentsRepository.deleteByUserAndTournament(
                        userId = enrollment.userId,
                        tournamentId = enrollment.tournamentId
                    )
                }
            }
    }

    private suspend fun savePendingEnrollment(userId: String, tournamentId: Long) {
        runCatching {
            if (offlineEnrollmentsRepository.existsPending(userId, tournamentId)) {
                error("Esta inscrição já está pendente de sincronização.")
            }

            val localId = offlineEnrollmentsRepository.createPendingEnrollment(
                userId = userId,
                tournamentId = tournamentId
            )

            if (localId == -1L) {
                error("Esta inscrição já está pendente de sincronização.")
            }

            OfflineSyncScheduler.schedule(getApplication(), userId)
        }.onSuccess {
            _uiState.update {
                it.copy(
                    isEnrolling = false,
                    enrollmentSuccessMessage = OFFLINE_ENROLLMENT_MESSAGE,
                    enrollmentErrorMessage = null,
                    isOfflineMode = true,
                    offlineMessage = OFFLINE_FRIENDLY_MESSAGE,
                    pendingOfflineEnrollments = it.pendingOfflineEnrollments + tournamentId,
                    pendingCount = it.pendingOfflineEnrollments.plus(tournamentId).size,
                    isNetworkAvailable = networkConnectivity.isOnline(),
                    syncMessage = "A inscrição será sincronizada automaticamente quando existir ligação."
                )
            }
        }.onFailure { throwable ->
            showEnrollmentError(
                if (throwable.message == PENDING_DUPLICATE_MESSAGE) {
                    PENDING_DUPLICATE_MESSAGE
                } else {
                    "Não foi possível guardar a inscrição offline. Tenta novamente."
                }
            )
        }
    }

    private fun showEnrollmentError(message: String) {
        _uiState.update {
            it.copy(
                isEnrolling = false,
                enrollmentSuccessMessage = null,
                enrollmentErrorMessage = message
            )
        }
    }

    private fun currentUserId(): String? {
        return SupabaseProvider.client.auth.currentUserOrNull()?.id
    }

    private fun Throwable.isNetworkFailure(): Boolean {
        return generateSequence(this) { throwable -> throwable.cause }.any { throwable ->
            throwable is IOException ||
                throwable is SocketTimeoutException ||
                throwable is ConnectTimeoutException ||
                throwable is HttpRequestTimeoutException ||
                throwable.message.orEmpty().contains("timeout", ignoreCase = true) ||
                throwable.message.orEmpty().contains("timed out", ignoreCase = true) ||
                throwable.message.orEmpty().contains("unable to resolve host", ignoreCase = true) ||
                throwable.message.orEmpty().contains("failed to connect", ignoreCase = true)
        }
    }

    private fun Throwable.toFriendlyEnrollmentMessage(): String {
        val messages = generateSequence(this) { throwable -> throwable.cause }
            .mapNotNull { throwable -> throwable.message }
            .toList()

        return when {
            messages.any { message ->
                message.contains("Já tens uma inscrição", ignoreCase = true)
            } -> "Já tens uma inscrição neste torneio."
            this is RestException && statusCode == 401 ->
                "A tua sessão terminou. Inicia sessão novamente."
            else -> "Não foi possível concluir a inscrição. Tenta novamente."
        }
    }

    private fun schedulePendingSync() {
        currentUserId()?.let { userId ->
            OfflineSyncScheduler.schedule(getApplication(), userId)
        }
    }

    private fun observeSyncState() {
        val userId = currentUserId() ?: return

        viewModelScope.launch {
            OfflineSyncScheduler.observe(getApplication(), userId).collect { workInfos ->
                val activeWork = workInfos.firstOrNull { workInfo ->
                    workInfo.state == WorkInfo.State.RUNNING ||
                        workInfo.state == WorkInfo.State.ENQUEUED ||
                        workInfo.state == WorkInfo.State.BLOCKED
                }
                val runningWork = workInfos.firstOrNull { workInfo ->
                    workInfo.state == WorkInfo.State.RUNNING
                }
                val completedWork = workInfos.lastOrNull { workInfo ->
                    workInfo.state.isFinished
                }

                _uiState.update { state ->
                    val completedMessage = if (activeWork != null) {
                        if (runningWork != null) {
                            "A sincronizar inscrições pendentes..."
                        } else {
                            state.syncMessage
                        }
                    } else when (completedWork?.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            val syncedCount = completedWork.outputData.getInt(
                                EnrollmentSyncWorker.KEY_SYNCED_COUNT,
                                0
                            )
                            if (syncedCount > 0) {
                                "$syncedCount inscrição(ões) sincronizada(s) com sucesso."
                            } else {
                                state.syncMessage
                            }
                        }
                        WorkInfo.State.FAILED -> {
                            val failedCount = completedWork.outputData.getInt(
                                EnrollmentSyncWorker.KEY_FAILED_COUNT,
                                0
                            )
                            if (failedCount > 0) {
                                "Não foi possível sincronizar $failedCount inscrição(ões)."
                            } else {
                                completedWork.outputData.getString(
                                    EnrollmentSyncWorker.KEY_ERROR_MESSAGE
                                ) ?: state.syncMessage
                            }
                        }
                        else -> state.syncMessage
                    }

                    state.copy(
                        isSyncing = runningWork != null,
                        isNetworkAvailable = if (activeWork != null) {
                            networkConnectivity.isOnline()
                        } else {
                            state.isNetworkAvailable
                        },
                        syncMessage = completedMessage
                    )
                }

                if (activeWork == null && completedWork?.state == WorkInfo.State.SUCCEEDED) {
                    refreshPendingEnrollments(reconcileRemote = true)
                }
            }
        }
    }

    companion object {
        private const val PENDING_DUPLICATE_MESSAGE =
            "Esta inscrição já está pendente de sincronização."
        private const val OFFLINE_FRIENDLY_MESSAGE =
            "Sem ligação. A inscrição foi guardada offline."
        const val OFFLINE_ENROLLMENT_MESSAGE =
            "Inscrição guardada offline. Será sincronizada quando voltar a existir ligação."
    }
}
