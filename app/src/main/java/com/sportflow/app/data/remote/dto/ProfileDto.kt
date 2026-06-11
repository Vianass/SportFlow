package com.sportflow.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    val id: String,
    val nome: String,
    val email: String,
    val papel: String,
    val estado: String = "ATIVO",
    @SerialName("metodo_pagamento")
    val metodoPagamento: String? = null,
    @SerialName("criado_em")
    val criadoEm: String? = null
)