package com.sportflow.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sportflow.app.ui.theme.SportFlowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SportFlowTheme {
                AppContent()
            }
        }
    }
}

@Composable
fun AppContent(modifier: Modifier = Modifier) {
    Text(text = "SportFlow - Gestão de Eventos Desportivos", modifier = modifier)
}