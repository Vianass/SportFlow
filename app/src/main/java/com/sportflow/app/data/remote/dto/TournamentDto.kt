package com.sportflow.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TournamentDto(
    val id: Long,
    val nome: String,
    @SerialName("data_inicio")
    val dataInicio: String,
    val estado: String,
    @SerialName("organizador_id")
    val organizadorId: String? = null,
    @SerialName("criado_em")
    val criadoEm: String? = null
)