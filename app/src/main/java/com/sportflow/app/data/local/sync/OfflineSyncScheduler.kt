package com.sportflow.app.data.local.sync

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit

object OfflineSyncScheduler {

    fun schedule(
        context: Context,
        userId: String,
        replaceExisting: Boolean = false
    ) {
        if (userId.isBlank()) return

        val request = OneTimeWorkRequestBuilder<EnrollmentSyncWorker>()
            .setInputData(workDataOf(EnrollmentSyncWorker.KEY_USER_ID to userId))
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                BACKOFF_DELAY_SECONDS,
                TimeUnit.SECONDS
            )
            .addTag(WORK_TAG)
            .build()

        WorkManager.getInstance(context.applicationContext).enqueueUniqueWork(
            uniqueWorkName(userId),
            if (replaceExisting) ExistingWorkPolicy.REPLACE else ExistingWorkPolicy.KEEP,
            request
        )
    }

    fun observe(context: Context, userId: String): Flow<List<WorkInfo>> {
        return WorkManager.getInstance(context.applicationContext)
            .getWorkInfosForUniqueWorkFlow(uniqueWorkName(userId))
    }

    internal fun uniqueWorkName(userId: String): String {
        return "${UNIQUE_WORK_PREFIX}_$userId"
    }

    private const val UNIQUE_WORK_PREFIX = "offline_enrollment_sync"
    private const val WORK_TAG = "offline_enrollment_sync"
    private const val BACKOFF_DELAY_SECONDS = 30L
}
