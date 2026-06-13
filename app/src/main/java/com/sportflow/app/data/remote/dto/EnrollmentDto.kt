package com.sportflow.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EnrollmentDto(
    val id: Long,
    @SerialName("utilizador_id")
    val userId: String,
    @SerialName("torneio_id")
    val tournamentId: Long,
    @SerialName("data_inscricao")
    val registeredAt: String? = null,
    val estado: String,
    val pagamento: String
)
