package com.sportflow.app.ui.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.SportsFootball
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sportflow.app.ui.theme.SportFlowDarkBlue
import com.sportflow.app.ui.theme.SportFlowGreen
import com.sportflow.app.ui.viewmodel.AdminViewModel

@Composable
fun AdminStatsScreen(viewModel: AdminViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    val stats = state.stats

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Estatísticas",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = SportFlowDarkBlue
                    )
                    IconButton(onClick = viewModel::refresh, enabled = !state.isLoading) {
                        if (state.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.height(20.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = "Atualizar estatísticas")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Visão geral atual da plataforma",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AdminStatsRow(
                    leftValue = stats.totalAthletes.toString(),
                    leftLabel = "ATLETAS",
                    leftIcon = Icons.Default.People,
                    rightValue = stats.totalOrganizers.toString(),
                    rightLabel = "ORGANIZADORES",
                    rightIcon = Icons.Default.Groups
                )
                AdminStatsRow(
                    leftValue = stats.totalAdmins.toString(),
                    leftLabel = "ADMINISTRADORES",
                    leftIcon = Icons.Default.AdminPanelSettings,
                    rightValue = stats.pendingOrganizers.toString(),
                    rightLabel = "PENDENTES",
                    rightIcon = Icons.Default.HourglassTop
                )
                AdminStatsRow(
                    leftValue = stats.totalTournaments.toString(),
                    leftLabel = "TORNEIOS",
                    leftIcon = Icons.Default.EmojiEvents,
                    rightValue = stats.totalGames.toString(),
                    rightLabel = "JOGOS",
                    rightIcon = Icons.Default.SportsFootball
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SportFlowDarkBlue),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "RECEITA ESTIMADA",
                            color = SportFlowGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = stats.totalRevenue?.let { "%.2f €".format(it) } ?: "0.00 €",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Inscrições aprovadas e pagas × preço atual do respetivo torneio.",
                            color = Color.White.copy(alpha = 0.65f),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminStatsRow(
    leftValue: String,
    leftLabel: String,
    leftIcon: ImageVector,
    rightValue: String,
    rightLabel: String,
    rightIcon: ImageVector
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        AdminMetricItem(leftIcon, leftValue, leftLabel, Modifier.weight(1f))
        AdminMetricItem(rightIcon, rightValue, rightLabel, Modifier.weight(1f))
    }
}

@Composable
private fun AdminMetricItem(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = SportFlowGreen
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                color = SportFlowDarkBlue,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = label,
                color = Color(0xFF64748B),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AdminStatsScreenPreview() {
    AdminStatsScreen()
}
