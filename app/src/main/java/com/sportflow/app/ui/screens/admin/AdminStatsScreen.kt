package com.sportflow.app.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sportflow.app.ui.theme.SportFlowDarkBlue
import com.sportflow.app.ui.theme.SportFlowGreen
import com.sportflow.app.ui.theme.SportFlowTextGray

// Data model for Top Goalscorers
data class GoalScorer(
    val rank: String,
    val name: String,
    val team: String,
    val goals: Int,
    val isUser: Boolean = false
)

// Data model for Monthly Performance Bar
data class MonthlyPerformance(
    val month: String,
    val goalsValue: Float, // proportional height value
    val winsValue: Float // proportional height value
)

@Composable
fun AdminStatsScreen() {
    val goalScorers = remember {
        listOf(
            GoalScorer(rank = "01", name = "Ricardo Pereira", team = "LEÕES DE ALVALADE", goals = 58),
            GoalScorer(rank = "02", name = "João Silva (Tu)", team = "ELITE FC", goals = 42, isUser = true),
            GoalScorer(rank = "03", name = "Marta Gonçalves", team = "ÁGUIAS DO PORTO", goals = 39)
        )
    }

    val monthlyPerformanceList = remember {
        listOf(
            MonthlyPerformance("JAN", goalsValue = 0.4f, winsValue = 0.55f),
            MonthlyPerformance("FEV", goalsValue = 0.45f, winsValue = 0.7f),
            MonthlyPerformance("MAR", goalsValue = 0.55f, winsValue = 0.65f),
            MonthlyPerformance("ABR", goalsValue = 0.75f, winsValue = 0.9f),
            MonthlyPerformance("MAI", goalsValue = 0.68f, winsValue = 0.8f)
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // 1. Hero Header Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Text(
                    text = "Estatísticas",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = SportFlowDarkBlue
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Desempenho da Temporada 2024",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
            }
        }

        // 2. Financial Summary Card
        item {
            FinancialSummaryCard()
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. Global Ranking Card (Ring indicator)
        item {
            GlobalRankingCard()
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 4. Quick Metrics Grid (2x2)
        item {
            MetricQuickGrid()
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 5. Performance Chart Card
        item {
            PerformanceChartCard(performanceList = monthlyPerformanceList)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 6. Top Goalscorers Section
        item {
            TopScorersSection(scorers = goalScorers)
        }
    }
}

@Composable
fun FinancialSummaryCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SportFlowDarkBlue)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Top Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "RESUMO FINANCEIRO",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportFlowGreen,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "450,00€",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "Receitas Acumuladas",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }

                // Green Wallet Icon Box
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F5A36)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = SportFlowGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color.White.copy(alpha = 0.08f), thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(14.dp))

            // Bottom Section (Saldo + LEVANTAR button)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SALDO EM CARTEIRA",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.5f),
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "85,20€",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                // LEVANTAR button
                Button(
                    onClick = { /* Withdraw balance */ },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SportFlowGreen),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(
                        text = "LEVANTAR",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = SportFlowDarkBlue,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
fun GlobalRankingCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF).copy(alpha = 0.7f)),
        border = BorderStroke(0.5.dp, Color(0xFFDBEAFE))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "RANKING GLOBAL",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Beautiful Circular green ring
            Box(
                modifier = Modifier
                    .size(105.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(5.dp, Color(0xFF16A34A), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "#12",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = SportFlowDarkBlue,
                        lineHeight = 28.sp
                    )
                    Text(
                        text = "DA LIGA",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Top 5% de 1.240 Jogadores",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = SportFlowDarkBlue
            )
        }
    }
}

@Composable
fun MetricQuickGrid() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Item 1: Jogos
            MetricItem(
                icon = Icons.Default.SportsFootball,
                value = "28",
                label = "JOGOS",
                modifier = Modifier.weight(1f)
            )

            // Item 2: Golos
            MetricItem(
                icon = Icons.Default.EmojiEvents,
                value = "42",
                label = "GOLOS",
                modifier = Modifier.weight(1f)
            )
        }

        // Row 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Item 3: Assist
            MetricItem(
                icon = Icons.Default.Handshake, // Handshake representing assist/cooperation
                value = "15",
                label = "ASSIST.",
                modifier = Modifier.weight(1f)
            )

            // Item 4: Vitórias
            MetricItem(
                icon = Icons.Default.TrendingUp,
                value = "84%",
                label = "VITÓRIAS",
                valueColor = Color(0xFF16A34A),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MetricItem(
    icon: ImageVector,
    value: String,
    label: String,
    valueColor: Color = SportFlowDarkBlue,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon in blue circular box
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFEFF6FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF2563EB),
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Value and label
            Column {
                Text(
                    text = value,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = valueColor
                )
                Text(
                    text = label,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

@Composable
fun PerformanceChartCard(performanceList: List<MonthlyPerformance>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF).copy(alpha = 0.7f)),
        border = BorderStroke(0.5.dp, Color(0xFFDBEAFE))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Chart Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "EVOLUÇÃO DE PERFORMANCE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = SportFlowDarkBlue,
                    letterSpacing = 0.5.sp
                )

                // Dot Indicators Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ChartIndicatorDot(color = Color(0xFF16A34A), label = "GOLOS")
                    ChartIndicatorDot(color = SportFlowDarkBlue, label = "VITÓRIAS")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bar Chart Area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                performanceList.forEach { perf ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(44.dp)
                    ) {
                        // Overlapping vertical bars for high fidelity matching figma screenshot
                        Box(
                            modifier = Modifier
                                .width(34.dp)
                                .height(100.dp),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            // Bar 1: Vitórias (Light green / translucent background)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(perf.winsValue)
                                    .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                    .background(Color(0xFF16A34A).copy(alpha = 0.25f))
                            )

                            // Bar 2: Golos (Dark green / solid foreground)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.75f)
                                    .fillMaxHeight(perf.goalsValue)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(Color(0xFF0F5A36))
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Month Label
                        Text(
                            text = perf.month,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChartIndicatorDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF475569)
        )
    }
}

@Composable
fun TopScorersSection(scorers: List<GoalScorer>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Text(
                text = "TOP MARCADORES DA LIGA",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = SportFlowDarkBlue,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            scorers.forEachIndexed { index, scorer ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Position index
                    val indexColor = if (scorer.rank == "01") Color(0xFF16A34A) else Color(0xFF94A3B8)
                    Text(
                        text = scorer.rank,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = indexColor,
                        modifier = Modifier.width(24.dp)
                    )

                    // Avatar Circle
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        if (scorer.isUser) Color(0xFFF97316) else Color(0xFF93C5FD),
                                        if (scorer.isUser) Color(0xFFEA580C) else Color(0xFF2563EB)
                                    )
                                )
                            )
                            .border(1.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Name and Team
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = scorer.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SportFlowDarkBlue
                        )
                        Text(
                            text = scorer.team,
                            fontSize = 9.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    // Score
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = scorer.goals.toString(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = SportFlowDarkBlue
                        )
                        Text(
                            text = "GOLOS",
                            fontSize = 8.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                if (index < scorers.size - 1) {
                    Divider(color = Color(0xFFF1F5F9), thickness = 0.5.dp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // VER RANKING COMPLETO button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* Full ranking */ },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "VER RANKING COMPLETO",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF64748B),
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminStatsScreenPreview() {
    AdminStatsScreen()
}
