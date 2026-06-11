package com.sportflow.app.data.repository

import com.sportflow.app.data.remote.SupabaseProvider
import com.sportflow.app.data.remote.dto.TournamentDto
import com.sportflow.app.model.Tournament
import io.github.jan.supabase.postgrest.from

class TournamentsRepository {

    suspend fun getTournaments(): List<Tournament> {
        return SupabaseProvider.client
            .from("torneios")
            .select()
            .decodeList<TournamentDto>()
            .map { dto ->
                Tournament(
                    id = dto.id,
                    name = dto.nome,
                    startDate = dto.dataInicio,
                    status = dto.estado,
                    organizerId = dto.organizadorId,
                    createdAt = dto.criadoEm,
                    sport = dto.modalidade,
                    category = dto.categoria,
                    location = dto.localizacao,
                    maxCapacity = dto.capacidadeMaxima,
                    price = dto.preco
                )
            }
    }
}