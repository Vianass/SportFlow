package com.sportflow.app.data.repository

import com.sportflow.app.data.remote.SupabaseProvider
import com.sportflow.app.data.remote.dto.TournamentDto
import io.github.jan.supabase.postgrest.from

class TournamentsRepository {

    suspend fun getTournaments(): List<TournamentDto> {
        return SupabaseProvider.client
            .from("torneios")
            .select()
            .decodeList<TournamentDto>()
    }
}