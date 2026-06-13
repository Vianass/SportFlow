package com.sportflow.app.model

data class GameEvent(
    val id: Long,
    val gameId: Long?,
    val playerId: String?,
    val eventType: String,
    val minute: Int,
    val playerName: String? = null,
    val playerEmail: String? = null,
    val teamName: String? = null
)

data class TopPerformer(
    val playerId: String,
    val playerName: String,
    val goals: Int
)
