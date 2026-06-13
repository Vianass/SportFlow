package com.sportflow.app.data.local.repository

import com.sportflow.app.data.local.dao.PendingEnrollmentDao
import com.sportflow.app.data.local.entity.PendingEnrollmentEntity
import kotlinx.coroutines.flow.Flow

class OfflineEnrollmentsRepository(
    private val pendingEnrollmentDao: PendingEnrollmentDao
) {

    suspend fun createPendingEnrollment(
        userId: String,
        tournamentId: Long,
        createdAt: Long = System.currentTimeMillis()
    ): Long {
        require(userId.isNotBlank()) { "O utilizador da inscrição é obrigatório." }

        return pendingEnrollmentDao.insertPendingEnrollment(
            PendingEnrollmentEntity(
                userId = userId,
                tournamentId = tournamentId,
                createdAt = createdAt
            )
        )
    }

    fun observePendingForUser(userId: String): Flow<List<PendingEnrollmentEntity>> {
        return pendingEnrollmentDao.observePendingForUser(userId)
    }

    suspend fun getAllPending(): List<PendingEnrollmentEntity> {
        return pendingEnrollmentDao.getAllPending()
    }

    suspend fun getPendingForUser(userId: String): List<PendingEnrollmentEntity> {
        return pendingEnrollmentDao.getPendingForUser(userId)
    }

    suspend fun deleteById(localId: Long): Boolean {
        return pendingEnrollmentDao.deleteById(localId) > 0
    }

    suspend fun deleteByUserAndTournament(userId: String, tournamentId: Long): Boolean {
        return pendingEnrollmentDao.deleteByUserAndTournament(userId, tournamentId) > 0
    }

    suspend fun updateError(localId: Long, error: String?): Boolean {
        return pendingEnrollmentDao.updateError(localId, error) > 0
    }

    suspend fun existsPending(userId: String, tournamentId: Long): Boolean {
        return pendingEnrollmentDao.existsPending(userId, tournamentId)
    }
}
