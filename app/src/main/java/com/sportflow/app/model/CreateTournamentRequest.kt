package com.sportflow.app.model

data class CreateTournamentRequest(
    val name: String,
    val startDate: String,
    val status: String,
    val sport: String,
    val category: String,
    val location: String,
    val maxCapacity: Int,
    val price: Double,
    val organizerId: String? = null
)
