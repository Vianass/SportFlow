package com.sportflow.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameEventDto(
    val id: Long,
    @SerialName("jogo_id")
    val gameId: Long? = null,
    @SerialName("jogador_id")
    val playerId: String? = null,
    @SerialName("tipo_evento")
    val eventType: String,
    val minuto: Int
)
