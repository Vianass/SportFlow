package com.sportflow.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import com.sportflow.app.navigation.NavGraph
import com.sportflow.app.ui.theme.SportFlowTheme
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// Inicialização correta com Auth e Postgrest para o SportFlow
val supabase = createSupabaseClient(
    supabaseUrl = "https://gjlbllzruxlvypcngtda.supabase.co",
    supabaseKey = "sb_publishable_R4Y8NPzM8CQsYwdLoWU8TQ_psbdoM2U"
) {

    install(Postgrest)
    install(Auth) {
        // Sessão persistida
    }
}

class MainViewModel : ViewModel() {
    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()

    init {
        kotlin.concurrent.thread {
            Thread.sleep(3000)
            _isReady.value = true
        }
    }
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            !viewModel.isReady.value
        }

        setContent {
            SportFlowTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph()
                }
            }
        }
    }
}
