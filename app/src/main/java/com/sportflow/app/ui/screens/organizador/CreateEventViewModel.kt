package com.sportflow.app.ui.screens.organizador

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportflow.app.data.repository.TournamentsRepository
import com.sportflow.app.model.CreateTournamentRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CreateEventUiState(
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class CreateEventViewModel(
    private val tournamentsRepository: TournamentsRepository = TournamentsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateEventUiState())
    val uiState: StateFlow<CreateEventUiState> = _uiState.asStateFlow()

    fun createTournament(
        name: String,
        sport: String,
        category: String,
        date: String,
        location: String,
        capacity: String,
        price: String
    ) {
        val trimmedName = name.trim()
        val trimmedDate = date.trim()
        val trimmedLocation = location.trim()
        val parsedCapacity = capacity.trim().toIntOrNull()
        val parsedPrice = price.trim().replace(',', '.').toDoubleOrNull()

        val validationError = when {
            trimmedName.isBlank() -> "Indica o nome do evento."
            trimmedDate.isBlank() -> "Indica a data do evento."
            !trimmedDate.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) -> "Usa a data no formato AAAA-MM-DD."
            trimmedLocation.isBlank() -> "Indica a localização."
            parsedCapacity == null || parsedCapacity <= 0 -> "A capacidade tem de ser um número maior que zero."
            parsedPrice == null || parsedPrice < 0.0 -> "O preço tem de ser um número igual ou maior que zero."
            else -> null
        }

        if (validationError != null) {
            _uiState.update {
                it.copy(
                    isSubmitting = false,
                    errorMessage = validationError,
                    successMessage = null
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSubmitting = true,
                    errorMessage = null,
                    successMessage = null
                )
            }

            runCatching {
                tournamentsRepository.createTournament(
                    CreateTournamentRequest(
                        name = trimmedName,
                        startDate = "${trimmedDate}T00:00:00Z",
                        status = "ABERTO",
                        sport = sport,
                        category = category,
                        location = trimmedLocation,
                        maxCapacity = parsedCapacity!!,
                        price = parsedPrice!!,
                        organizerId = null
                    )
                )
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = null,
                        successMessage = "Evento publicado com sucesso."
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = throwable.message ?: "Erro ao publicar evento.",
                        successMessage = null
                    )
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update {
            it.copy(
                errorMessage = null,
                successMessage = null
            )
        }
    }
}
