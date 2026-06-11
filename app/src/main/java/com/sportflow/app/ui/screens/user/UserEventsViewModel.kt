package com.sportflow.app.ui.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportflow.app.data.repository.TournamentsRepository
import com.sportflow.app.model.Tournament
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserEventsUiState(
    val isLoading: Boolean = false,
    val tournaments: List<Tournament> = emptyList(),
    val errorMessage: String? = null
)

class UserEventsViewModel(
    private val tournamentsRepository: TournamentsRepository = TournamentsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserEventsUiState(isLoading = true))
    val uiState: StateFlow<UserEventsUiState> = _uiState.asStateFlow()

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
                tournamentsRepository.getTournaments()
            }.onSuccess { tournaments ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        tournaments = tournaments,
                        errorMessage = null
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Erro ao carregar torneios."
                    )
                }
            }
        }
    }
}