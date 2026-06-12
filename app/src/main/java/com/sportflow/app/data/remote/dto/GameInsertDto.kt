package com.sportflow.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameInsertDto(
    @SerialName("torneio_id")
    val tournamentId: Long,
    @SerialName("equipa_casa_id")
    val homeTeamId: Long,
    @SerialName("equipa_fora_id")
    val awayTeamId: Long,
    @SerialName("data_hora")
    val dateTime: String,
    val resultado: String? = null,
    val estado: String = "NAO_INICIADO"
)
