package com.sportflow.app.data.repository

import com.sportflow.app.data.remote.SupabaseProvider
import com.sportflow.app.data.remote.dto.GameDto
import com.sportflow.app.data.remote.dto.GameInsertDto
import com.sportflow.app.model.Game
import io.github.jan.supabase.postgrest.from

class GamesRepository(
    private val teamsRepository: TeamsRepository = TeamsRepository()
) {

    suspend fun getGamesForTournament(tournamentId: Long): List<Game> {
        val games = SupabaseProvider.client
            .from("jogos")
            .select {
                filter {
                    eq("torneio_id", tournamentId)
                }
            }
            .decodeList<GameDto>()

        val teamsById = teamsRepository.getTeamsForTournament(tournamentId)
            .associateBy { team -> team.id }

        return games.map { dto ->
            Game(
                id = dto.id,
                tournamentId = dto.tournamentId,
                homeTeamId = dto.homeTeamId,
                awayTeamId = dto.awayTeamId,
                dateTime = dto.dateTime,
                result = dto.resultado,
                status = dto.estado,
                homeTeamName = dto.homeTeamId?.let { teamsById[it]?.name },
                awayTeamName = dto.awayTeamId?.let { teamsById[it]?.name }
            )
        }
    }

    suspend fun createGame(
        tournamentId: Long,
        homeTeamId: Long,
        awayTeamId: Long,
        dateTime: String
    ) {
        require(homeTeamId != awayTeamId) {
            "Seleciona duas equipas diferentes."
        }

        require(dateTime.isNotBlank()) {
            "A data/hora do jogo é obrigatória."
        }

        val teams = teamsRepository.getTeamsForTournament(tournamentId)
        val homeBelongsToTournament = teams.any { it.id == homeTeamId }
        val awayBelongsToTournament = teams.any { it.id == awayTeamId }

        require(homeBelongsToTournament && awayBelongsToTournament) {
            "As equipas selecionadas têm de pertencer a este torneio."
        }

        SupabaseProvider.client
            .from("jogos")
            .insert(
                GameInsertDto(
                    tournamentId = tournamentId,
                    homeTeamId = homeTeamId,
                    awayTeamId = awayTeamId,
                    dateTime = dateTime,
                    resultado = null,
                    estado = "NAO_INICIADO"
                )
            )
    }
}
