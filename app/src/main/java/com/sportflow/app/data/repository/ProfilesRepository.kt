package com.sportflow.app.data.repository

import com.sportflow.app.data.remote.SupabaseProvider
import com.sportflow.app.data.remote.dto.ProfileDto
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class ProfilesRepository {

    suspend fun getProfiles(): List<ProfileDto> {
        return SupabaseProvider.client
            .from("perfis")
            .select()
            .decodeList<ProfileDto>()
    }

    suspend fun createProfile(profile: ProfileDto) {
        SupabaseProvider.client
            .from("perfis")
            .insert(profile)
    }

    suspend fun getProfile(id: String): ProfileDto? {
        return SupabaseProvider.client
            .from("perfis")
            .select {
                filter {
                    eq("id", id)
                }
            }.decodeSingleOrNull<ProfileDto>()
    }

    suspend fun getPendingProfiles(): List<ProfileDto> {
        return SupabaseProvider.client
            .from("perfis")
            .select {
                filter { eq("estado", "PENDENTE") }
            }.decodeList<ProfileDto>()
    }

    suspend fun updateProfileStatus(id: String, status: String) {
        SupabaseProvider.client
            .from("perfis")
            .update(
                buildJsonObject {
                    put("estado", status)
                }
            ) {
                filter { eq("id", id) }
            }
    }
}