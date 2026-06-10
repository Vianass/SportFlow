package com.sportflow.app.model

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppLanguageViewModel : ViewModel() {

    // ── Language ─────────────────────────────────────────────────────────────
    private val _language = MutableStateFlow(AppLanguage.PT)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    fun setLanguage(lang: AppLanguage, context: Context) {
        _language.value = lang
        prefs(context).edit().putString("app_language", lang.name).apply()
    }

    // ── Notifications ─────────────────────────────────────────────────────────
    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    fun setNotificationsEnabled(enabled: Boolean, context: Context) {
        _notificationsEnabled.value = enabled
        prefs(context).edit().putBoolean("notifications_enabled", enabled).apply()
    }

    // ── Privacy (Atleta) ──────────────────────────────────────────────────────
    private val _profilePublic = MutableStateFlow(true)
    val profilePublic: StateFlow<Boolean> = _profilePublic.asStateFlow()

    private val _showInRankings = MutableStateFlow(true)
    val showInRankings: StateFlow<Boolean> = _showInRankings.asStateFlow()

    private val _locationEnabled = MutableStateFlow(false)
    val locationEnabled: StateFlow<Boolean> = _locationEnabled.asStateFlow()

    fun setProfilePublic(v: Boolean, context: Context) {
        _profilePublic.value = v; prefs(context).edit().putBoolean("profile_public", v).apply()
    }
    fun setShowInRankings(v: Boolean, context: Context) {
        _showInRankings.value = v; prefs(context).edit().putBoolean("show_in_rankings", v).apply()
    }
    fun setLocationEnabled(v: Boolean, context: Context) {
        _locationEnabled.value = v; prefs(context).edit().putBoolean("location_enabled", v).apply()
    }

    // ── Privacy (Organizador) ─────────────────────────────────────────────────
    private val _shareContactWithAthletes = MutableStateFlow(true)
    val shareContactWithAthletes: StateFlow<Boolean> = _shareContactWithAthletes.asStateFlow()

    fun setShareContactWithAthletes(v: Boolean, context: Context) {
        _shareContactWithAthletes.value = v
        prefs(context).edit().putBoolean("share_contact_athletes", v).apply()
    }

    // ── Init ──────────────────────────────────────────────────────────────────
    fun loadSavedLanguage(context: Context) {
        val p = prefs(context)
        val savedLang = p.getString("app_language", AppLanguage.PT.name)
        _language.value = try {
            AppLanguage.valueOf(savedLang ?: AppLanguage.PT.name)
        } catch (e: IllegalArgumentException) { AppLanguage.PT }
        _notificationsEnabled.value = p.getBoolean("notifications_enabled", true)
        _profilePublic.value = p.getBoolean("profile_public", true)
        _showInRankings.value = p.getBoolean("show_in_rankings", true)
        _locationEnabled.value = p.getBoolean("location_enabled", false)
        _shareContactWithAthletes.value = p.getBoolean("share_contact_athletes", true)
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences("sportflow_prefs", Context.MODE_PRIVATE)
}
