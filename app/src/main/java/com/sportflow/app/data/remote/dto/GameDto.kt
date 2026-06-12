package com.sportflow.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameDto(
    val id: Long,
    @SerialName("torneio_id")
    val tournamentId: Long? = null,
    @SerialName("equipa_casa_id")
    val homeTeamId: Long? = null,
    @SerialName("equipa_fora_id")
    val awayTeamId: Long? = null,
    @SerialName("data_hora")
    val dateTime: String,
    val resultado: String? = null,
    val estado: String
)
