package com.sportflow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sportflow.app.ui.screens.user.LiveEvent
import com.sportflow.app.ui.theme.SportFlowDarkBlue
import com.sportflow.app.ui.theme.SportFlowGreen

@Composable
fun LiveMatchDialog(
    event: LiveEvent,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header (Live badge)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFDCFCE7))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF22C55E))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "A DECORRER",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF15803D)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = event.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = SportFlowDarkBlue,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = event.category,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (event.type == "MATCH") {
                    val isTennisOrPadel = event.title.contains("Ténis", ignoreCase = true) ||
                            event.title.contains("Padel", ignoreCase = true) ||
                            event.category.contains("Ténis", ignoreCase = true) ||
                            event.category.contains("Padel", ignoreCase = true) ||
                            event.category.contains("Tennis", ignoreCase = true)
                    val playerLabel1 = if (isTennisOrPadel) "Jogador 1" else "Equipa A"
                    val playerLabel2 = if (isTennisOrPadel) "Jogador 2" else "Equipa B"

                    // Scoreboard Area
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Team 1
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEFF6FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Sports, contentDescription = null, tint = SportFlowDarkBlue)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(playerLabel1, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        // Score
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "2 - 1",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Black,
                                color = SportFlowDarkBlue
                            )
                            Text(
                                text = event.statusValue, // e.g. "72'"
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = SportFlowGreen
                            )
                        }

                        // Team 2
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFEF2F2)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Sports, contentDescription = null, tint = Color(0xFFDC2626))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(playerLabel2, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Stats Area
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Estatísticas do Jogo",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = SportFlowDarkBlue
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (isTennisOrPadel) {
                            StatRow("Aces", "4", "2", 0.66f)
                            Spacer(modifier = Modifier.height(12.dp))
                            StatRow("Duplas Faltas", "1", "3", 0.25f)
                            Spacer(modifier = Modifier.height(12.dp))
                            StatRow("Break Points Salvos", "80%", "45%", 0.64f)
                        } else {
                            StatRow("Posse de Bola", "55%", "45%", 0.55f)
                            Spacer(modifier = Modifier.height(12.dp))
                            StatRow("Remates", "12", "8", 0.6f)
                            Spacer(modifier = Modifier.height(12.dp))
                            StatRow("Faltas", "4", "6", 0.4f)
                        }
                    }
                } else {
                    // Race Area
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Classificação em Tempo Real",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = SportFlowDarkBlue
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        LeaderboardRow(position = 1, name = "João Silva", club = "Sporting CP", time = "10.42s", isLeader = true)
                        Spacer(modifier = Modifier.height(8.dp))
                        LeaderboardRow(position = 2, name = "Pedro Santos", club = "SL Benfica", time = "10.51s", isLeader = false)
                        Spacer(modifier = Modifier.height(8.dp))
                        LeaderboardRow(position = 3, name = "Carlos Almeida", club = "SC Braga", time = "10.60s", isLeader = false)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SportFlowDarkBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "FECHAR",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun StatRow(label: String, valLeft: String, valRight: String, ratio: Float) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(valLeft, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SportFlowDarkBlue)
            Text(label, fontSize = 11.sp, color = Color(0xFF64748B))
            Text(valRight, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
        ) {
            Box(
                modifier = Modifier
                    .weight(ratio)
                    .fillMaxHeight()
                    .background(SportFlowDarkBlue)
            )
            Box(
                modifier = Modifier
                    .weight(1f - ratio)
                    .fillMaxHeight()
                    .background(Color(0xFFDC2626))
            )
        }
    }
}

@Composable
private fun LeaderboardRow(position: Int, name: String, club: String, time: String, isLeader: Boolean) {
    val bgColor = if (isLeader) Color(0xFFFEF9C3) else Color(0xFFF1F5F9)
    val posColor = if (isLeader) Color(0xFFCA8A04) else Color(0xFF64748B)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${position}º",
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            color = posColor,
            modifier = Modifier.width(30.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SportFlowDarkBlue)
            Text(text = club, fontSize = 11.sp, color = Color(0xFF64748B))
        }
        Text(
            text = time,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = SportFlowGreen
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LiveMatchDialogPreview() {
    LiveMatchDialog(
        event = LiveEvent(
            category = "LIGA AMADORA DE LISBOA",
            title = "Futebol 7: Final do Torneio",
            statusLabel = "Progresso do Jogo",
            statusValue = "72'",
            progress = 0.8f,
            icon = Icons.Default.SportsFootball
        ),
        onDismiss = {}
    )
}

@Preview(showBackground = true)
@Composable
fun LiveMatchTennisDialogPreview() {
    LiveMatchDialog(
        event = LiveEvent(
            category = "OPEN DE TÉNIS DE BRAGA",
            title = "Mesa 1: Individual Masc.",
            statusLabel = "Set 3/5",
            statusValue = "15 - 40",
            progress = 0.85f,
            icon = Icons.Default.SportsTennis,
            isDarkTheme = false
        ),
        onDismiss = {}
    )
}
