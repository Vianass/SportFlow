package com.sportflow.app.data.repository

import com.sportflow.app.data.remote.SupabaseProvider
import com.sportflow.app.data.remote.dto.GameDto
import com.sportflow.app.data.remote.dto.GameInsertDto
import com.sportflow.app.model.Game
import com.sportflow.app.model.Team
import com.sportflow.app.model.Tournament
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class GamesRepository(
    private val teamsRepository: TeamsRepository = TeamsRepository(),
    private val tournamentsRepository: TournamentsRepository = TournamentsRepository()
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

        val tournament = tournamentsRepository
            .getTournaments()
            .firstOrNull { tournament -> tournament.id == tournamentId }

        return games.map { dto ->
            dto.toGame(
                teamsById = teamsById,
                tournament = tournament
            )
        }
    }

    suspend fun getOngoingGames(): List<Game> {
        val games = SupabaseProvider.client
            .from("jogos")
            .select {
                filter {
                    eq("estado", "EM_DECORRER")
                }
            }
            .decodeList<GameDto>()

        val tournamentsById = tournamentsRepository
            .getTournaments()
            .associateBy { tournament -> tournament.id }

        val teamsCache = mutableMapOf<Long, Map<Long, Team>>()

        return games.map { dto ->
            val tournamentId = dto.tournamentId
            val teamsById = if (tournamentId != null) {
                teamsCache.getOrPut(tournamentId) {
                    teamsRepository
                        .getTeamsForTournament(tournamentId)
                        .associateBy { team -> team.id }
                }
            } else {
                emptyMap()
            }

            dto.toGame(
                teamsById = teamsById,
                tournament = tournamentId?.let { tournamentsById[it] }
            )
        }.sortedBy { game -> game.dateTime }
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

    suspend fun startGame(
        tournamentId: Long,
        gameId: Long
    ) {
        updateGame(
            tournamentId = tournamentId,
            gameId = gameId,
            status = "EM_DECORRER",
            result = null
        )
    }

    suspend fun finishGame(
        tournamentId: Long,
        gameId: Long,
        result: String
    ) {
        require(result.isNotBlank()) {
            "O resultado é obrigatório para terminar o jogo."
        }

        updateGame(
            tournamentId = tournamentId,
            gameId = gameId,
            status = "TERMINADO",
            result = result.trim()
        )
    }

    private suspend fun updateGame(
        tournamentId: Long,
        gameId: Long,
        status: String,
        result: String?
    ) {
        require(status in setOf("NAO_INICIADO", "EM_DECORRER", "TERMINADO")) {
            "Estado de jogo inválido."
        }

        SupabaseProvider.client
            .from("jogos")
            .update(
                buildJsonObject {
                    put("estado", status)
                    if (result != null) {
                        put("resultado", result)
                    }
                }
            ) {
                filter {
                    eq("id", gameId)
                    eq("torneio_id", tournamentId)
                }
            }
    }

    private fun GameDto.toGame(
        teamsById: Map<Long, Team>,
        tournament: Tournament?
    ): Game {
        return Game(
            id = id,
            tournamentId = tournamentId,
            homeTeamId = homeTeamId,
            awayTeamId = awayTeamId,
            dateTime = dateTime,
            result = resultado,
            status = estado,
            homeTeamName = homeTeamId?.let { teamsById[it]?.name },
            awayTeamName = awayTeamId?.let { teamsById[it]?.name },
            tournamentName = tournament?.name,
            tournamentLocation = tournament?.location,
            sport = tournament?.sport
        )
    }
}
