package com.sportflow.app.data.repository

import com.sportflow.app.data.remote.SupabaseProvider
import com.sportflow.app.data.remote.dto.EnrollmentDto
import com.sportflow.app.data.remote.dto.EnrollmentInsertDto
import com.sportflow.app.model.Enrollment
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class EnrollmentsRepository(
    private val tournamentsRepository: TournamentsRepository = TournamentsRepository(),
    private val profilesRepository: ProfilesRepository = ProfilesRepository()
) {

    suspend fun enrollInTournament(tournamentId: Long) {
        val currentUserId = currentUserId()

        val existingEnrollment = SupabaseProvider.client
            .from("inscricoes")
            .select {
                filter {
                    eq("utilizador_id", currentUserId)
                    eq("torneio_id", tournamentId)
                }
            }
            .decodeList<EnrollmentDto>()

        if (existingEnrollment.isNotEmpty()) {
            error("Já tens uma inscrição neste torneio.")
        }

        val dto = EnrollmentInsertDto(
            userId = currentUserId,
            tournamentId = tournamentId,
            registeredAt = OffsetDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
            estado = "PENDENTE",
            pagamento = "PENDENTE"
        )

        SupabaseProvider.client
            .from("inscricoes")
            .insert(dto)
    }

    suspend fun getCurrentUserEnrollments(): List<Enrollment> {
        val currentUserId = currentUserId()

        val enrollmentDtos = SupabaseProvider.client
            .from("inscricoes")
            .select {
                filter {
                    eq("utilizador_id", currentUserId)
                }
            }
            .decodeList<EnrollmentDto>()

        val tournamentsById = tournamentsRepository
            .getTournaments()
            .associateBy { it.id }

        return enrollmentDtos.map { dto ->
            Enrollment(
                id = dto.id,
                userId = dto.userId,
                tournamentId = dto.tournamentId,
                registeredAt = dto.registeredAt,
                status = dto.estado,
                paymentStatus = dto.pagamento,
                tournament = tournamentsById[dto.tournamentId]
            )
        }
    }

    suspend fun getEnrollmentsForTournament(tournamentId: Long): List<Enrollment> {
        val enrollmentDtos = SupabaseProvider.client
            .from("inscricoes")
            .select {
                filter {
                    eq("torneio_id", tournamentId)
                }
            }
            .decodeList<EnrollmentDto>()

        val tournament = tournamentsRepository
            .getTournaments()
            .firstOrNull { it.id == tournamentId }

        return enrollmentDtos.map { dto ->
            val profile = runCatching { profilesRepository.getProfile(dto.userId) }.getOrNull()

            Enrollment(
                id = dto.id,
                userId = dto.userId,
                tournamentId = dto.tournamentId,
                registeredAt = dto.registeredAt,
                status = dto.estado,
                paymentStatus = dto.pagamento,
                tournament = tournament,
                userName = profile?.nome,
                userEmail = profile?.email
            )
        }
    }

    suspend fun updateEnrollmentStatus(
        enrollmentId: Long,
        status: String
    ) {
        require(status == "APROVADA" || status == "REJEITADA" || status == "PENDENTE") {
            "Estado de inscrição inválido."
        }

        SupabaseProvider.client
            .from("inscricoes")
            .update(
                buildJsonObject {
                    put("estado", status)
                }
            ) {
                filter {
                    eq("id", enrollmentId)
                }
            }
    }

    private fun currentUserId(): String {
        return SupabaseProvider.client.auth.currentUserOrNull()?.id
            ?: error("Utilizador não autenticado. Inicia sessão novamente.")
    }
}
