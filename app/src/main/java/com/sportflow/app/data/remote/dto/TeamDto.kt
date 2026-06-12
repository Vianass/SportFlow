package com.sportflow.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeamDto(
    val id: Long,
    val nome: String,
    @SerialName("torneio_id")
    val torneioId: Long? = null
)
