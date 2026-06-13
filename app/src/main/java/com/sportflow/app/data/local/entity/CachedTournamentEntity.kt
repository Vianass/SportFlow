package com.sportflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_tournaments")
data class CachedTournamentEntity(
    @PrimaryKey
    val id: Long,
    val nome: String,
    val modalidade: String?,
    val categoria: String?,
    val localizacao: String?,
    val dataInicio: String,
    val estado: String,
    val capacidadeMaxima: Int?,
    val preco: Double?,
    val cachedAt: Long
)
