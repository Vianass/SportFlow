package com.sportflow.app.data.repository

import com.sportflow.app.data.remote.SupabaseProvider
import com.sportflow.app.data.remote.dto.EnrollmentDto
import com.sportflow.app.data.remote.dto.TeamDto
import com.sportflow.app.data.remote.dto.TeamInsertDto
import com.sportflow.app.data.remote.dto.TeamPlayerDto
import com.sportflow.app.data.remote.dto.TeamPlayerInsertDto
import com.sportflow.app.model.EligiblePlayer
import com.sportflow.app.model.Team
import com.sportflow.app.model.TeamMember
import io.github.jan.supabase.postgrest.from

class TeamsRepository(
    private val profilesRepository: ProfilesRepository = ProfilesRepository()
) {

    suspend fun getTeamsForTournament(tournamentId: Long): List<Team> {
        val teams = SupabaseProvider.client
            .from("equipas")
            .select {
                filter {
                    eq("torneio_id", tournamentId)
                }
            }
            .decodeList<TeamDto>()

        return teams.map { dto ->
            Team(
                id = dto.id,
                name = dto.nome,
                tournamentId = dto.torneioId,
                players = getTeamMembers(dto.id)
            )
        }
    }

    suspend fun createTeam(
        tournamentId: Long,
        name: String
    ) {
        val trimmedName = name.trim()
        require(trimmedName.isNotBlank()) {
            "O nome da equipa é obrigatório."
        }

        val existingTeams = getTeamsForTournament(tournamentId)
        val alreadyExists = existingTeams.any { team ->
            team.name.equals(trimmedName, ignoreCase = true)
        }

        if (alreadyExists) {
            error("Já existe uma equipa com esse nome neste torneio.")
        }

        SupabaseProvider.client
            .from("equipas")
            .insert(
                TeamInsertDto(
                    nome = trimmedName,
                    torneioId = tournamentId
                )
            )
    }

    suspend fun getEligiblePlayersForTournament(tournamentId: Long): List<EligiblePlayer> {
        val confirmedEnrollmentDtos = SupabaseProvider.client
            .from("inscricoes")
            .select {
                filter {
                    eq("torneio_id", tournamentId)
                    eq("estado", "APROVADA")
                    eq("pagamento", "PAGO")
                }
            }
            .decodeList<EnrollmentDto>()

        val assignedPlayerIds = getTeamsForTournament(tournamentId)
            .flatMap { team -> team.players }
            .map { player -> player.playerId }
            .toSet()

        return confirmedEnrollmentDtos
            .filter { dto -> dto.userId !in assignedPlayerIds }
            .mapNotNull { dto ->
                val profile = runCatching { profilesRepository.getProfile(dto.userId) }.getOrNull()
                profile?.let {
                    EligiblePlayer(
                        playerId = dto.userId,
                        name = it.nome,
                        email = it.email
                    )
                }
            }
            .sortedBy { player -> player.name.lowercase() }
    }

    suspend fun associatePlayerToTeam(
        teamId: Long,
        playerId: String,
        shirtNumber: Int?
    ) {
        require(playerId.isNotBlank()) {
            "Seleciona um jogador."
        }

        require(shirtNumber == null || shirtNumber > 0) {
            "O número da camisola tem de ser maior que zero."
        }

        val existingRows = SupabaseProvider.client
            .from("jogadores_equipas")
            .select {
                filter {
                    eq("equipa_id", teamId)
                    eq("jogador_id", playerId)
                }
            }
            .decodeList<TeamPlayerDto>()

        if (existingRows.isNotEmpty()) {
            error("Este jogador já está associado a esta equipa.")
        }

        SupabaseProvider.client
            .from("jogadores_equipas")
            .insert(
                TeamPlayerInsertDto(
                    teamId = teamId,
                    playerId = playerId,
                    shirtNumber = shirtNumber
                )
            )
    }

    private suspend fun getTeamMembers(teamId: Long): List<TeamMember> {
        val playerRows = SupabaseProvider.client
            .from("jogadores_equipas")
            .select {
                filter {
                    eq("equipa_id", teamId)
                }
            }
            .decodeList<TeamPlayerDto>()

        return playerRows.mapNotNull { row ->
            val profile = runCatching { profilesRepository.getProfile(row.playerId) }.getOrNull()
            profile?.let {
                TeamMember(
                    playerId = row.playerId,
                    name = it.nome,
                    email = it.email,
                    shirtNumber = row.shirtNumber
                )
            }
        }
    }
}
