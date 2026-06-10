package com.sportflow.app

import com.sportflow.app.navigation.NavGraph
import com.sportflow.app.model.AppLanguageViewModel
import com.sportflow.app.model.LocalLanguageViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.sportflow.app.ui.theme.SportFlowTheme
import com.sportflow.app.ui.theme.SportFlowTextGray
import com.sportflow.app.ui.theme.SportFlowGreen
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.offset

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
    private val languageViewModel: AppLanguageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        // Remover ou comentar isto para que a splash screen nativa não esconda a sua MainScreen()
        // splashScreen.setKeepOnScreenCondition {
        //     !viewModel.isReady.value
        // }

        setContent {
            val context = LocalContext.current
            SideEffect { languageViewModel.loadSavedLanguage(context) }
            val isReady by viewModel.isReady.collectAsState()

            CompositionLocalProvider(LocalLanguageViewModel provides languageViewModel) {
                SportFlowTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        if (isReady) {
                            NavGraph()
                        } else {
                            MainScreen()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .size(256.dp)
                        .offset(y = 25.dp)
                        .offset(y = (-25).dp)
                        .background(
                            color = Color.White.copy(alpha = 0.10f),
                            shape = RoundedCornerShape(64.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.20f),
                            shape = RoundedCornerShape(64.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.sportflow_logo),
                        contentDescription = "SportFlow Logo",
                        modifier = Modifier.size(150.dp)
                    )
                }

                Text(
                    text = "SportFlow",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                )
                Text(
                    text = "PERFORMANCE EM TEMPO REAL",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 60.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .width(120.dp)
                        .height(3.dp),
                    color = SportFlowGreen,
                    trackColor = Color.White.copy(alpha = 0.1f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "A CARREGAR ECOSSISTEMA...",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }

            Text(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                text = "POWERED BY KINETIC ARCHITECT",
                style = MaterialTheme.typography.labelSmall,
                color = SportFlowTextGray
            )
        }
    }
}