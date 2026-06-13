package com.sportflow.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sportflow.app.data.local.dao.PendingEnrollmentDao
import com.sportflow.app.data.local.dao.TournamentDao
import com.sportflow.app.data.local.entity.CachedTournamentEntity
import com.sportflow.app.data.local.entity.PendingEnrollmentEntity

@Database(
    entities = [
        CachedTournamentEntity::class,
        PendingEnrollmentEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SportFlowDatabase : RoomDatabase() {

    abstract fun tournamentDao(): TournamentDao

    abstract fun pendingEnrollmentDao(): PendingEnrollmentDao

    companion object {
        private const val DATABASE_NAME = "sportflow_offline.db"

        @Volatile
        private var instance: SportFlowDatabase? = null

        fun getInstance(context: Context): SportFlowDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    SportFlowDatabase::class.java,
                    DATABASE_NAME
                ).build().also { database -> instance = database }
            }
        }
    }
}
