package com.sportflow.app.model

enum class ProfileStatus {
    PENDENTE,
    ATIVO,
    REJEITADO,
    BLOQUEADO;

    companion object {
        fun fromDatabase(value: String): ProfileStatus? = entries.firstOrNull {
            it.name.equals(value, ignoreCase = true)
        }
    }
}
