package com.sportflow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.sportflow.app.data.local.entity.CachedTournamentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TournamentDao {

    @Upsert
    suspend fun upsertAll(tournaments: List<CachedTournamentEntity>)

    @Query("SELECT * FROM cached_tournaments ORDER BY dataInicio ASC")
    fun observeAll(): Flow<List<CachedTournamentEntity>>

    @Query("SELECT * FROM cached_tournaments WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): CachedTournamentEntity?

    @Query("DELETE FROM cached_tournaments WHERE cachedAt < :olderThan")
    suspend fun clearOldCache(olderThan: Long): Int
}
