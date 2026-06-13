package com.sportflow.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TournamentInsertDto(
    val nome: String,
    @SerialName("data_inicio")
    val dataInicio: String,
    val estado: String,
    @SerialName("organizador_id")
    val organizadorId: String,
    val modalidade: String,
    val categoria: String,
    val localizacao: String,
    @SerialName("capacidade_maxima")
    val capacidadeMaxima: Int,
    val preco: Double
)
