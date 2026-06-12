package com.sportflow.app.data.repository

import com.sportflow.app.data.remote.SupabaseProvider
import com.sportflow.app.data.remote.dto.TeamDto
import com.sportflow.app.data.remote.dto.TeamInsertDto
import com.sportflow.app.model.Team
import io.github.jan.supabase.postgrest.from

class TeamsRepository {

    suspend fun getTeamsForTournament(tournamentId: Long): List<Team> {
        return SupabaseProvider.client
            .from("equipas")
            .select {
                filter {
                    eq("torneio_id", tournamentId)
                }
            }
            .decodeList<TeamDto>()
            .map { dto ->
                Team(
                    id = dto.id,
                    name = dto.nome,
                    tournamentId = dto.torneioId
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
}
