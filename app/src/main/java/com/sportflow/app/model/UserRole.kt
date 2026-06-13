package com.sportflow.app.model

enum class UserRole(val displayName: String) {
    JOGADOR("Atleta"),
    ORGANIZADOR("Organizador"),
    ADMIN("Administrador");

    companion object {
        fun fromDatabase(value: String): UserRole? = entries.firstOrNull {
            it.name.equals(value, ignoreCase = true)
        }
    }
}
