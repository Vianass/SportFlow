package com.sportflow.app.data.local.repository

import com.sportflow.app.data.local.dao.TournamentDao
import com.sportflow.app.data.local.entity.CachedTournamentEntity
import com.sportflow.app.model.Tournament
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OfflineTournamentsRepository(
    private val tournamentDao: TournamentDao
) {

    suspend fun cacheTournaments(
        tournaments: List<Tournament>,
        cachedAt: Long = System.currentTimeMillis()
    ) {
        tournamentDao.upsertAll(
            tournaments.map { tournament -> tournament.toCachedEntity(cachedAt) }
        )
    }

    fun observeAll(): Flow<List<Tournament>> {
        return tournamentDao.observeAll().map { entities ->
            entities.map { entity -> entity.toTournament() }
        }
    }

    suspend fun getById(id: Long): Tournament? {
        return tournamentDao.getById(id)?.toTournament()
    }

    suspend fun clearOldCache(olderThan: Long): Int {
        return tournamentDao.clearOldCache(olderThan)
    }

    private fun Tournament.toCachedEntity(cachedAt: Long): CachedTournamentEntity {
        return CachedTournamentEntity(
            id = id,
            nome = name,
            modalidade = sport,
            categoria = category,
            localizacao = location,
            dataInicio = startDate,
            estado = status,
            capacidadeMaxima = maxCapacity,
            preco = price,
            cachedAt = cachedAt
        )
    }

    private fun CachedTournamentEntity.toTournament(): Tournament {
        return Tournament(
            id = id,
            name = nome,
            startDate = dataInicio,
            status = estado,
            organizerId = null,
            createdAt = null,
            sport = modalidade,
            category = categoria,
            location = localizacao,
            maxCapacity = capacidadeMaxima,
            price = preco
        )
    }
}
