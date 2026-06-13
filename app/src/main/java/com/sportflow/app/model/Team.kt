package com.sportflow.app.model

data class Team(
    val id: Long,
    val name: String,
    val tournamentId: Long?,
    val players: List<TeamMember> = emptyList()
)

data class TeamMember(
    val playerId: String,
    val name: String,
    val email: String,
    val shirtNumber: Int?
)

data class EligiblePlayer(
    val playerId: String,
    val name: String,
    val email: String
)
