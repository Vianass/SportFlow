package com.sportflow.app

import kotlinx.serialization.Serializable

@Serializable
data class Perfil(
    val id: String, // UUID vindo do Supabase Auth
    val nome: String,
    val email: String,
    val papel: String, // 'ADMIN', 'ORGANIZADOR' ou 'JOGADOR'
    val metodo_pagamento: String? = null
)
