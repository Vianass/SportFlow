package com.sportflow.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeamPlayerInsertDto(
    @SerialName("equipa_id")
    val teamId: Long,
    @SerialName("jogador_id")
    val playerId: String,
    @SerialName("numero_camisola")
    val shirtNumber: Int? = null
)
