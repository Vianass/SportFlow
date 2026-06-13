package com.sportflow.app.model

import com.sportflow.app.data.remote.dto.GameDto
import com.sportflow.app.data.remote.dto.ProfileDto

data class AdminPlatformStats(
    val totalAthletes: Int = 0,
    val totalOrganizers: Int = 0,
    val totalAdmins: Int = 0,
    val totalTournaments: Int = 0,
    val totalGames: Int = 0,
    val pendingOrganizers: Int = 0,
    val totalRevenue: Double? = null
)

data class AdminDashboardData(
    val stats: AdminPlatformStats = AdminPlatformStats(),
    val tournaments: List<Tournament> = emptyList(),
    val games: List<GameDto> = emptyList(),
    val teamNames: Map<Long, String> = emptyMap(),
    val pendingOrganizers: List<ProfileDto> = emptyList()
)

data class AdminUserFilter(
    val role: UserRole? = null,
    val status: ProfileStatus? = null,
    val search: String = ""
)

data class AdminUserActionResult(
    val success: Boolean,
    val userId: String,
    val resultingStatus: ProfileStatus? = null,
    val message: String
)

data class UpdateTournamentRequest(
    val id: Long,
    val name: String,
    val startDate: String,
    val status: String,
    val sport: String,
    val category: String,
    val location: String,
    val maxCapacity: Int,
    val price: Double
)
