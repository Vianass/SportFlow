package com.sportflow.app.data.repository

import com.sportflow.app.data.remote.SupabaseProvider
import com.sportflow.app.data.remote.dto.TournamentDto
import com.sportflow.app.data.remote.dto.TournamentInsertDto
import com.sportflow.app.model.CreateTournamentRequest
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

    suspend fun createTournament(request: CreateTournamentRequest) {
        val dto = TournamentInsertDto(
            nome = request.name,
            dataInicio = request.startDate,
            estado = request.status,
            organizadorId = request.organizerId,
            modalidade = request.sport,
            categoria = request.category,
            localizacao = request.location,
            capacidadeMaxima = request.maxCapacity,
            preco = request.price
        )

        SupabaseProvider.client
            .from("torneios")
            .insert(dto)
    }
}
