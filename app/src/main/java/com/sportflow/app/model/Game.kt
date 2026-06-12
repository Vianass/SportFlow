package com.sportflow.app.model

data class Game(
    val id: Long,
    val tournamentId: Long?,
    val homeTeamId: Long?,
    val awayTeamId: Long?,
    val dateTime: String,
    val result: String?,
    val status: String,
    val homeTeamName: String? = null,
    val awayTeamName: String? = null,
    val tournamentName: String? = null,
    val tournamentLocation: String? = null,
    val sport: String? = null
)
