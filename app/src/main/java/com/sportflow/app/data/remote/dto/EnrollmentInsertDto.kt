package com.sportflow.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EnrollmentInsertDto(
    @SerialName("utilizador_id")
    val userId: String,
    @SerialName("torneio_id")
    val tournamentId: Long,
    @SerialName("data_inscricao")
    val registeredAt: String,
    val estado: String,
    val pagamento: String
)
