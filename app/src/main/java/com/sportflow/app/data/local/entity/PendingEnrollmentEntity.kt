package com.sportflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pending_enrollments",
    indices = [Index(value = ["userId", "tournamentId"], unique = true)]
)
data class PendingEnrollmentEntity(
    @PrimaryKey(autoGenerate = true)
    val localId: Long = 0,
    val userId: String,
    val tournamentId: Long,
    val createdAt: Long,
    val status: String = STATUS_PENDING_SYNC,
    val lastError: String? = null
) {
    companion object {
        const val STATUS_PENDING_SYNC = "PENDING_SYNC"
    }
}
