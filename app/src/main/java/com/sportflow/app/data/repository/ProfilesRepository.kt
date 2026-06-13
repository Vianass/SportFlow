package com.sportflow.app.data.repository

import android.util.Log
import com.sportflow.app.data.remote.SupabaseProvider
import com.sportflow.app.data.remote.dto.ProfileDto
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
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
        try {
            Log.d("ProfilesRepository", "Chamando RPC para $status: $id")
            
            val functionName = if (status == "ATIVO") "aprovar_utilizador" else "rejeitar_utilizador"
            
            SupabaseProvider.client.postgrest.rpc(
                function = functionName,
                parameters = buildJsonObject {
                    put("user_id", id)
                }
            )

            Log.d("ProfilesRepository", "RPC executado com sucesso.")
        } catch (e: Exception) {
            Log.e("ProfilesRepository", "Erro ao chamar RPC", e)
            throw e
        }
    }
}
