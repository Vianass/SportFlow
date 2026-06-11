package com.sportflow.app.model

data class Enrollment(
    val id: Long,
    val userId: String,
    val tournamentId: Long,
    val registeredAt: String?,
    val status: String,
    val paymentStatus: String,
    val tournament: Tournament?
)
