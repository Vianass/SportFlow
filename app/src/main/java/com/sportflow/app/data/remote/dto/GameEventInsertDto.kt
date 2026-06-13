package com.sportflow.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameEventInsertDto(
    @SerialName("jogo_id")
    val gameId: Long,
    @SerialName("jogador_id")
    val playerId: String,
    @SerialName("tipo_evento")
    val eventType: String,
    val minuto: Int
)
