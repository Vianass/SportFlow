package com.sportflow.app.data.local.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.sportflow.app.data.local.SportFlowDatabase
import com.sportflow.app.data.local.entity.PendingEnrollmentEntity
import com.sportflow.app.data.remote.SupabaseProvider
import com.sportflow.app.data.remote.dto.EnrollmentDto
import com.sportflow.app.data.remote.dto.EnrollmentInsertDto
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.from
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import java.io.IOException
import java.net.SocketTimeoutException
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class EnrollmentSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val pendingEnrollmentDao = SportFlowDatabase
        .getInstance(appContext)
        .pendingEnrollmentDao()

    override suspend fun doWork(): Result {
        val requestedUserId = inputData.getString(KEY_USER_ID)
            ?: return Result.failure(errorOutput("Utilizador da sincronização em falta."))

        val pendingEnrollments = pendingEnrollmentDao.getPendingForUser(requestedUserId)
        if (pendingEnrollments.isEmpty()) {
            return Result.success(successOutput(syncedCount = 0))
        }

        val session = SupabaseProvider.client.auth.currentSessionOrNull()
        val currentUserId = session?.user?.id
        if (currentUserId == null || currentUserId != requestedUserId) {
            updateErrors(pendingEnrollments, SESSION_ERROR)
            return Result.retry()
        }

        var syncedCount = 0
        var failedCount = 0
        var shouldRetry = false

        pendingEnrollments.forEachIndexed { index, enrollment ->
            setProgress(
                workDataOf(
                    KEY_PROCESSED_COUNT to index + 1,
                    KEY_TOTAL_COUNT to pendingEnrollments.size
                )
            )

            runCatching {
                syncEnrollment(enrollment)
            }.onSuccess {
                deleteSyncedEnrollment(enrollment)
                syncedCount += 1
            }.onFailure { throwable ->
                val alreadyExists = runCatching {
                    remoteEnrollmentExists(enrollment.userId, enrollment.tournamentId)
                }.getOrDefault(false)

                if (alreadyExists) {
                    deleteSyncedEnrollment(enrollment)
                    syncedCount += 1
                } else {
                    pendingEnrollmentDao.updateError(
                        enrollment.localId,
                        throwable.toSafeErrorMessage()
                    )
                    failedCount += 1
                    shouldRetry = shouldRetry || throwable.isRetryable()
                }
            }
        }

        val output = workDataOf(
            KEY_SYNCED_COUNT to syncedCount,
            KEY_FAILED_COUNT to failedCount
        )

        return when {
            shouldRetry -> Result.retry()
            failedCount > 0 -> Result.failure(output)
            else -> Result.success(output)
        }
    }

    private suspend fun syncEnrollment(enrollment: PendingEnrollmentEntity) {
        if (remoteEnrollmentExists(enrollment.userId, enrollment.tournamentId)) {
            return
        }

        SupabaseProvider.client
            .from("inscricoes")
            .insert(
                EnrollmentInsertDto(
                    userId = enrollment.userId,
                    tournamentId = enrollment.tournamentId,
                    registeredAt = Instant.ofEpochMilli(enrollment.createdAt)
                        .atOffset(ZoneOffset.UTC)
                        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                    estado = "PENDENTE",
                    pagamento = "PENDENTE"
                )
            )
    }

    private suspend fun remoteEnrollmentExists(userId: String, tournamentId: Long): Boolean {
        return SupabaseProvider.client
            .from("inscricoes")
            .select {
                filter {
                    eq("utilizador_id", userId)
                    eq("torneio_id", tournamentId)
                }
            }
            .decodeList<EnrollmentDto>()
            .isNotEmpty()
    }

    private suspend fun deleteSyncedEnrollment(enrollment: PendingEnrollmentEntity) {
        val deletedRows = pendingEnrollmentDao.deleteById(enrollment.localId)
        if (deletedRows == 0) {
            pendingEnrollmentDao.deleteByUserAndTournament(
                userId = enrollment.userId,
                tournamentId = enrollment.tournamentId
            )
        }
    }

    private suspend fun updateErrors(
        enrollments: List<PendingEnrollmentEntity>,
        message: String
    ) {
        enrollments.forEach { enrollment ->
            pendingEnrollmentDao.updateError(enrollment.localId, message)
        }
    }

    private fun Throwable.isRetryable(): Boolean {
        return this is IOException ||
            this is SocketTimeoutException ||
            this is ConnectTimeoutException ||
            this is HttpRequestTimeoutException ||
            this is RestException && (statusCode == 401 || statusCode == 408 || statusCode == 429 || statusCode >= 500)
    }

    private fun Throwable.toSafeErrorMessage(): String {
        return when (this) {
            is RestException -> description ?: error
            is IOException,
            is SocketTimeoutException,
            is ConnectTimeoutException,
            is HttpRequestTimeoutException -> "Falha temporária de ligação ao servidor."
            else -> message?.take(250) ?: "Erro ao sincronizar a inscrição."
        }
    }

    private fun successOutput(syncedCount: Int) = workDataOf(
        KEY_SYNCED_COUNT to syncedCount,
        KEY_FAILED_COUNT to 0
    )

    private fun errorOutput(message: String) = workDataOf(KEY_ERROR_MESSAGE to message)

    companion object {
        const val KEY_USER_ID = "user_id"
        const val KEY_SYNCED_COUNT = "synced_count"
        const val KEY_FAILED_COUNT = "failed_count"
        const val KEY_ERROR_MESSAGE = "error_message"
        const val KEY_PROCESSED_COUNT = "processed_count"
        const val KEY_TOTAL_COUNT = "total_count"

        private const val SESSION_ERROR =
            "Sessão inválida. Inicia sessão com a conta que criou a inscrição."
    }
}
