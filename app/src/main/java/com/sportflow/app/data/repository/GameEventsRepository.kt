package com.sportflow.app.data.repository

import com.sportflow.app.data.remote.SupabaseProvider
import com.sportflow.app.data.remote.dto.GameEventDto
import com.sportflow.app.data.remote.dto.GameEventInsertDto
import com.sportflow.app.model.GameEvent
import com.sportflow.app.model.TopPerformer
import io.github.jan.supabase.postgrest.from
import java.util.Locale

class GameEventsRepository(
    private val gamesRepository: GamesRepository = GamesRepository(),
    private val teamsRepository: TeamsRepository = TeamsRepository(),
    private val profilesRepository: ProfilesRepository = ProfilesRepository()
) {

    suspend fun getEventsForTournament(tournamentId: Long): List<GameEvent> {
        val games = gamesRepository.getGamesForTournament(tournamentId)
        if (games.isEmpty()) return emptyList()

        val teams = teamsRepository.getTeamsForTournament(tournamentId)
        val teamByPlayerId = teams
            .flatMap { team -> team.players.map { player -> player.playerId to team.name } }
            .toMap()

        return games.flatMap { game ->
            SupabaseProvider.client
                .from("eventos_jogo")
                .select {
                    filter {
                        eq("jogo_id", game.id)
                    }
                }
                .decodeList<GameEventDto>()
                .map { dto -> dto.toGameEvent(teamByPlayerId) }
        }.sortedWith(compareBy<GameEvent> { it.gameId ?: Long.MAX_VALUE }.thenBy { it.minute })
    }

    suspend fun registerGameEvent(
        tournamentId: Long,
        gameId: Long,
        playerId: String,
        eventType: String,
        minute: Int
    ) {
        val normalizedEventType = eventType.trim().uppercase(Locale.ROOT)
        require(normalizedEventType in ALLOWED_EVENT_TYPES) {
            "Tipo de evento inválido."
        }
        require(playerId.isNotBlank()) {
            "Seleciona um jogador."
        }
        require(minute >= 0) {
            "O minuto do evento tem de ser maior ou igual a zero."
        }
        require(minute <= 200) {
            "O minuto do evento parece inválido."
        }

        val game = gamesRepository
            .getGamesForTournament(tournamentId)
            .firstOrNull { it.id == gameId }
            ?: error("Jogo não encontrado neste torneio.")

        require(game.status.equals("EM_DECORRER", ignoreCase = true)) {
            "Só é possível registar eventos em jogos em curso."
        }

        val teams = teamsRepository.getTeamsForTournament(tournamentId)
        val gameTeamIds = setOfNotNull(game.homeTeamId, game.awayTeamId)
        val playerBelongsToGame = teams
            .filter { team -> team.id in gameTeamIds }
            .flatMap { team -> team.players }
            .any { player -> player.playerId == playerId }

        require(playerBelongsToGame) {
            "O jogador selecionado tem de pertencer a uma das equipas deste jogo."
        }

        SupabaseProvider.client
            .from("eventos_jogo")
            .insert(
                GameEventInsertDto(
                    gameId = gameId,
                    playerId = playerId,
                    eventType = normalizedEventType,
                    minuto = minute
                )
            )
    }

    suspend fun getTopPerformersForTournament(tournamentId: Long): List<TopPerformer> {
        return getEventsForTournament(tournamentId)
            .filter { event -> event.eventType.equals("GOLO", ignoreCase = true) }
            .filter { event -> event.playerId != null }
            .groupBy { event -> event.playerId!! }
            .map { (playerId, events) ->
                TopPerformer(
                    playerId = playerId,
                    playerName = events.firstOrNull()?.playerName ?: "Jogador sem nome",
                    goals = events.size
                )
            }
            .sortedWith(compareByDescending<TopPerformer> { it.goals }.thenBy { it.playerName.lowercase() })
    }

    private suspend fun GameEventDto.toGameEvent(
        teamByPlayerId: Map<String, String>
    ): GameEvent {
        val profile = playerId?.let { id ->
            runCatching { profilesRepository.getProfile(id) }.getOrNull()
        }

        return GameEvent(
            id = id,
            gameId = gameId,
            playerId = playerId,
            eventType = eventType,
            minute = minuto,
            playerName = profile?.nome,
            playerEmail = profile?.email,
            teamName = playerId?.let { teamByPlayerId[it] }
        )
    }

    companion object {
        val ALLOWED_EVENT_TYPES = setOf(
            "GOLO",
            "FALTA",
            "CARTAO_AMARELO",
            "CARTAO_VERMELHO"
        )
    }
}
