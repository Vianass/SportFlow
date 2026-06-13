package com.sportflow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sportflow.app.data.local.entity.PendingEnrollmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PendingEnrollmentDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPendingEnrollment(enrollment: PendingEnrollmentEntity): Long

    @Query(
        "SELECT * FROM pending_enrollments " +
            "WHERE userId = :userId AND status = 'PENDING_SYNC' ORDER BY createdAt ASC"
    )
    fun observePendingForUser(userId: String): Flow<List<PendingEnrollmentEntity>>

    @Query("SELECT * FROM pending_enrollments WHERE status = 'PENDING_SYNC' ORDER BY createdAt ASC")
    suspend fun getAllPending(): List<PendingEnrollmentEntity>

    @Query(
        "SELECT * FROM pending_enrollments " +
            "WHERE userId = :userId AND status = 'PENDING_SYNC' ORDER BY createdAt ASC"
    )
    suspend fun getPendingForUser(userId: String): List<PendingEnrollmentEntity>

    @Query("DELETE FROM pending_enrollments WHERE localId = :localId")
    suspend fun deleteById(localId: Long): Int

    @Query(
        "DELETE FROM pending_enrollments " +
            "WHERE userId = :userId AND tournamentId = :tournamentId"
    )
    suspend fun deleteByUserAndTournament(userId: String, tournamentId: Long): Int

    @Query("UPDATE pending_enrollments SET lastError = :error WHERE localId = :localId")
    suspend fun updateError(localId: Long, error: String?): Int

    @Query(
        "SELECT EXISTS(SELECT 1 FROM pending_enrollments " +
            "WHERE userId = :userId AND tournamentId = :tournamentId AND status = 'PENDING_SYNC')"
    )
    suspend fun existsPending(userId: String, tournamentId: Long): Boolean
}
