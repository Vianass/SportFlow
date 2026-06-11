package com.sportflow.app.model

data class Tournament(
    val id: Long,
    val name: String,
    val startDate: String,
    val status: String,
    val organizerId: String?,
    val createdAt: String?
)