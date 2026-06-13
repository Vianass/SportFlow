package com.sportflow.app.data.repository

import com.sportflow.app.data.remote.SupabaseProvider
import com.sportflow.app.data.remote.dto.TournamentDto
import com.sportflow.app.data.remote.dto.TournamentInsertDto
import com.sportflow.app.model.CreateTournamentRequest
import com.sportflow.app.model.Tournament
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class TournamentsRepository {

    suspend fun getTournaments(): List<Tournament> {
        return SupabaseProvider.client
            .from("torneios")
            .select()
            .decodeList<TournamentDto>()
            .map { dto -> dto.toTournament() }
    }

    suspend fun getCurrentOrganizerTournaments(): List<Tournament> {
        val currentUserId = SupabaseProvider.client.auth.currentUserOrNull()?.id
            ?: error("Utilizador não autenticado. Inicia sessão novamente.")

        return SupabaseProvider.client
            .from("torneios")
            .select {
                filter {
                    eq("organizador_id", currentUserId)
                }
            }
            .decodeList<TournamentDto>()
            .map { dto -> dto.toTournament() }
    }

    suspend fun createTournament(request: CreateTournamentRequest) {
        val currentUserId = SupabaseProvider.client.auth.currentUserOrNull()?.id
            ?: error("Utilizador não autenticado. Inicia sessão novamente.")

        val dto = TournamentInsertDto(
            nome = request.name,
            dataInicio = request.startDate,
            estado = request.status,
            organizadorId = currentUserId,
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

    private fun TournamentDto.toTournament(): Tournament {
        return Tournament(
            id = id,
            name = nome,
            startDate = dataInicio,
            status = estado,
            organizerId = organizadorId,
            createdAt = criadoEm,
            sport = modalidade,
            category = categoria,
            location = localizacao,
            maxCapacity = capacidadeMaxima,
            price = preco
        )
    }
}
