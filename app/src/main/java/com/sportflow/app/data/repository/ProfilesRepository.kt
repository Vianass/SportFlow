package com.sportflow.app.data.repository

import android.util.Log
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
        try {
            Log.d("ProfilesRepository", "Tentando atualizar perfil: $id para $status")
            
            // Usando buildJsonObject para garantir compatibilidade máxima
            val updateData = buildJsonObject {
                put("estado", status)
            }

            SupabaseProvider.client
                .from("perfis")
                .update(updateData) {
                    filter {
                        eq("id", id)
                    }
                }
            
            Log.d("ProfilesRepository", "Pedido de atualização enviado.")
        } catch (e: Exception) {
            Log.e("ProfilesRepository", "Erro na comunicação com Supabase", e)
            throw e
        }
    }
}
