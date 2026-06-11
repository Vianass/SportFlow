package com.sportflow.app.data.remote

import com.sportflow.app.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseProvider {

    val client: SupabaseClient by lazy {
        val supabaseUrl = BuildConfig.SUPABASE_URL
        val supabaseKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY

        require(supabaseUrl.isNotBlank()) {
            "SUPABASE_URL is missing. Add it to local.properties."
        }

        require(supabaseKey.isNotBlank()) {
            "SUPABASE_PUBLISHABLE_KEY is missing. Add it to local.properties."
        }

        createSupabaseClient(
            supabaseUrl = supabaseUrl.trim().removeSuffix("/"),
            supabaseKey = supabaseKey.trim()
        ) {
            install(Postgrest)
            install(Auth)
        }
    }
}