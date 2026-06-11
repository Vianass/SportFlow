package com.sportflow.app.data.repository

import com.sportflow.app.data.remote.SupabaseProvider
import com.sportflow.app.data.remote.dto.ProfileDto
import io.github.jan.supabase.postgrest.from

class ProfilesRepository {

    suspend fun getProfiles(): List<ProfileDto> {
        return SupabaseProvider.client
            .from("perfis")
            .select()
            .decodeList<ProfileDto>()
    }
}