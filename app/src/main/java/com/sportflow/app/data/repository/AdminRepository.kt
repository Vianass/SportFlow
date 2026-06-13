package com.sportflow.app.data.repository

import com.sportflow.app.data.remote.SupabaseProvider
import com.sportflow.app.data.remote.dto.GameDto
import com.sportflow.app.data.remote.dto.EnrollmentDto
import com.sportflow.app.data.remote.dto.ProfileDto
import com.sportflow.app.data.remote.dto.TeamDto
import com.sportflow.app.model.AdminDashboardData
import com.sportflow.app.model.AdminPlatformStats
import com.sportflow.app.model.AdminUserActionResult
import com.sportflow.app.model.AdminUserFilter
import com.sportflow.app.model.CreateTournamentRequest
import com.sportflow.app.model.Enrollment
import com.sportflow.app.model.ProfileStatus
import com.sportflow.app.model.Tournament
import com.sportflow.app.model.UserRole
import com.sportflow.app.model.UpdateTournamentRequest
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AdminRepository(
    private val profilesRepository: ProfilesRepository = ProfilesRepository(),
    private val tournamentsRepository: TournamentsRepository = TournamentsRepository(),
    private val enrollmentsRepository: EnrollmentsRepository = EnrollmentsRepository()
) {
    suspend fun getPendingOrganizers(): List<ProfileDto> {
        requireAdmin()
        return SupabaseProvider.client
            .from("perfis")
            .select {
                filter {
                    eq("papel", UserRole.ORGANIZADOR.name)
                    eq("estado", ProfileStatus.PENDENTE.name)
                }
            }
            .decodeList<ProfileDto>()
            .sortedBy { it.nome.lowercase() }
    }

    suspend fun getAllUsers(): List<ProfileDto> = getUsers()

    suspend fun getUsers(
        role: UserRole? = null,
        status: ProfileStatus? = null,
        search: String = ""
    ): List<ProfileDto> = getUsers(AdminUserFilter(role, status, search))

    suspend fun getUsers(filter: AdminUserFilter): List<ProfileDto> {
        requireAdmin()
        val normalizedSearch = filter.search.trim()

        return SupabaseProvider.client
            .from("perfis")
            .select()
            .decodeList<ProfileDto>()
            .asSequence()
            .filter { filter.role == null || it.role == filter.role }
            .filter { filter.status == null || it.status == filter.status }
            .filter {
                normalizedSearch.isBlank() ||
                    it.nome.contains(normalizedSearch, ignoreCase = true) ||
                    it.email.contains(normalizedSearch, ignoreCase = true)
            }
            .sortedBy { it.nome.lowercase() }
            .toList()
    }

    suspend fun getPlatformStats(): AdminPlatformStats {
        requireAdmin()
        val profiles = profilesRepository.getProfiles()
        val tournaments = tournamentsRepository.getTournaments()
        val games = getAllGamesInternal()
        val enrollments = getAllEnrollmentsInternal()

        return AdminPlatformStats(
            totalAthletes = profiles.count { it.role == UserRole.JOGADOR },
            totalOrganizers = profiles.count { it.role == UserRole.ORGANIZADOR },
            totalAdmins = profiles.count { it.role == UserRole.ADMIN },
            totalTournaments = tournaments.size,
            totalGames = games.size,
            pendingOrganizers = profiles.count {
                it.role == UserRole.ORGANIZADOR && it.status == ProfileStatus.PENDENTE
            },
            totalRevenue = calculateEstimatedRevenue(tournaments, enrollments)
        )
    }

    suspend fun getAdminTournaments(): List<Tournament> {
        requireAdmin()
        return tournamentsRepository.getTournaments()
            .sortedByDescending { it.startDate }
    }

    suspend fun createTournament(request: CreateTournamentRequest) {
        requireAdmin()
        tournamentsRepository.createTournament(request)
    }

    suspend fun updateTournament(request: UpdateTournamentRequest) {
        requireAdmin()
        SupabaseProvider.client.postgrest.rpc(
            function = "editar_torneio_admin",
            parameters = buildJsonObject {
                put("p_torneio_id", request.id)
                put("p_nome", request.name)
                put("p_modalidade", request.sport)
                put("p_categoria", request.category)
                put("p_localizacao", request.location)
                put("p_data_inicio", request.startDate)
                put("p_capacidade_maxima", request.maxCapacity)
                put("p_preco", request.price)
                put("p_estado", request.status)
            }
        )
    }

    suspend fun getEnrollmentsForTournament(tournamentId: Long): List<Enrollment> {
        requireAdminOwnedTournament(tournamentId)
        return enrollmentsRepository.getEnrollmentsForTournament(tournamentId)
    }

    suspend fun updateEnrollmentStatus(
        tournamentId: Long,
        enrollmentId: Long,
        status: String
    ) {
        requireAdminOwnedTournament(tournamentId)
        enrollmentsRepository.updateEnrollmentStatus(enrollmentId, status)
    }

    suspend fun getDashboardData(): AdminDashboardData {
        requireAdmin()
        val profiles = profilesRepository.getProfiles()
        val tournaments = tournamentsRepository.getTournaments()
            .sortedByDescending { it.startDate }
        val games = getAllGamesInternal()
        val enrollments = getAllEnrollmentsInternal()
        val teamNames = runCatching { getTeamNamesInternal() }
            .getOrDefault(emptyMap())
        val pending = profiles
            .filter { it.role == UserRole.ORGANIZADOR && it.status == ProfileStatus.PENDENTE }
            .sortedBy { it.nome.lowercase() }

        return AdminDashboardData(
            stats = AdminPlatformStats(
                totalAthletes = profiles.count { it.role == UserRole.JOGADOR },
                totalOrganizers = profiles.count { it.role == UserRole.ORGANIZADOR },
                totalAdmins = profiles.count { it.role == UserRole.ADMIN },
                totalTournaments = tournaments.size,
                totalGames = games.size,
                pendingOrganizers = pending.size,
                totalRevenue = calculateEstimatedRevenue(tournaments, enrollments)
            ),
            tournaments = tournaments,
            games = games,
            teamNames = teamNames,
            pendingOrganizers = pending
        )
    }

    suspend fun approveOrganizer(userId: String): AdminUserActionResult =
        runUserAction(userId, "aprovar_utilizador", ProfileStatus.ATIVO, "Organizador aprovado com sucesso.")

    suspend fun rejectOrganizer(userId: String): AdminUserActionResult =
        runUserAction(userId, "rejeitar_utilizador", ProfileStatus.REJEITADO, "Pedido de organizador rejeitado.")

    suspend fun blockUser(userId: String): AdminUserActionResult =
        runUserAction(userId, "bloquear_utilizador", ProfileStatus.BLOQUEADO, "Utilizador bloqueado com sucesso.")

    suspend fun unblockUser(userId: String): AdminUserActionResult =
        runUserAction(userId, "desbloquear_utilizador", ProfileStatus.ATIVO, "Utilizador desbloqueado com sucesso.")

    suspend fun requireAdmin(): ProfileDto {
        val userId = SupabaseProvider.client.auth.currentUserOrNull()?.id
            ?: error("Utilizador não autenticado.")
        val profile = profilesRepository.getProfile(userId)
            ?: error("Perfil do utilizador não encontrado.")

        require(profile.role == UserRole.ADMIN && profile.status == ProfileStatus.ATIVO) {
            "Acesso reservado a administradores ativos."
        }
        return profile
    }

    private suspend fun getAllGamesInternal(): List<GameDto> =
        SupabaseProvider.client.from("jogos").select().decodeList<GameDto>()

    private suspend fun requireAdminOwnedTournament(tournamentId: Long) {
        val admin = requireAdmin()
        val tournament = tournamentsRepository.getTournaments()
            .firstOrNull { it.id == tournamentId }
            ?: error("Torneio não encontrado.")

        require(tournament.organizerId == admin.id) {
            "Só podes gerir inscrições de torneios criados pela tua conta de administrador."
        }
    }

    private suspend fun getAllEnrollmentsInternal(): List<EnrollmentDto> =
        SupabaseProvider.client.from("inscricoes").select().decodeList<EnrollmentDto>()

    private fun calculateEstimatedRevenue(
        tournaments: List<Tournament>,
        enrollments: List<EnrollmentDto>
    ): Double {
        val tournamentPrices = tournaments.associate { it.id to (it.price ?: 0.0) }
        return enrollments
            .asSequence()
            .filter { it.estado.equals("APROVADA", ignoreCase = true) }
            .filter { it.pagamento.equals("PAGO", ignoreCase = true) }
            .sumOf { tournamentPrices[it.tournamentId] ?: 0.0 }
    }

    private suspend fun getTeamNamesInternal(): Map<Long, String> =
        SupabaseProvider.client
            .from("equipas")
            .select()
            .decodeList<TeamDto>()
            .associate { it.id to it.nome }

    private suspend fun runUserAction(
        userId: String,
        functionName: String,
        resultingStatus: ProfileStatus,
        successMessage: String
    ): AdminUserActionResult {
        val admin = requireAdmin()
        require(userId.isNotBlank()) { "Identificador de utilizador inválido." }
        require(userId != admin.id) { "Não podes alterar o estado da tua própria conta." }

        SupabaseProvider.client.postgrest.rpc(
            function = functionName,
            parameters = buildJsonObject { put("user_id", userId) }
        )

        return AdminUserActionResult(
            success = true,
            userId = userId,
            resultingStatus = resultingStatus,
            message = successMessage
        )
    }
}
