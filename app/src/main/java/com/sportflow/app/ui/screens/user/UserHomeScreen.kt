package com.sportflow.app.ui.screens.user

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.rounded.ChevronRight
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

// Data model for Live Events
data class LiveEvent(
    val category: String,
    val title: String,
    val statusLabel: String,
    val statusValue: String,
    val progress: Float,
    val icon: ImageVector,
    val isDarkTheme: Boolean = false
)

// Data model for Upcoming Events
data class UpcomingEvent(
    val day: String,
    val month: String,
    val title: String,
    val location: String,
    val time: String,
    val categoryBadge: String
)

@Composable
fun UserHomeScreen() {
    // Mock data for Live Events
    val liveEvents = remember {
        listOf(
            LiveEvent(
                category = "CAMPEONATO NACIONAL",
                title = "Futebol: Sporting vs Benfica",
                statusLabel = "Progresso do Jogo",
                statusValue = "72'",
                progress = 0.8f,
                icon = Icons.Default.SportsFootball,
                isDarkTheme = false
            ),
            LiveEvent(
                category = "MEETING DE ATLETISMO",
                title = "100m Barreiras - Final",
                statusLabel = "Chamada de Atletas",
                statusValue = "Em curso",
                progress = 0.45f,
                icon = Icons.Default.DirectionsRun,
                isDarkTheme = true
            ),
            LiveEvent(
                category = "OPEN DE TÉNIS DE BRAGA",
                title = "Mesa 1: Individual Masc.",
                statusLabel = "Set 3/5",
                statusValue = "15 - 40",
                progress = 0.85f,
                icon = Icons.Default.SportsTennis,
                isDarkTheme = false
            )
        )
    }

    // Mock data for Upcoming Events
    val upcomingEvents = remember {
        listOf(
            UpcomingEvent(
                day = "24",
                month = "SET",
                title = "Taça de Portugal: Marítimo vs Porto",
                location = "Estádio do Bessa, Porto",
                time = "20:45",
                categoryBadge = "FUTEBOL"
            ),
            UpcomingEvent(
                day = "02",
                month = "OUT",
                title = "Campeonato Regional de Natação",
                location = "Piscina Olímpica do Jamor",
                time = "09:00",
                categoryBadge = "NATAÇÃO"
            ),
            UpcomingEvent(
                day = "15",
                month = "OUT",
                title = "Volta ao Algarve em Ciclismo",
                location = "Etapa 3 - Vilamoura",
                time = "11:30",
                categoryBadge = "CICLISMO"
            )
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // 1. Promo Slogan Banner
        item {
            PromoBanner()
        }

        // 2. Eventos a Decorrer (Live Events) Section
        item {
            SectionHeader(
                title = "Eventos a Decorrer",
                isLive = true,
                onViewAllClick = { /* Handle View All */ }
            )
        }

        items(liveEvents) { event ->
            LiveEventCard(event = event)
        }

        // 3. Próximos Eventos (Upcoming Events) Section
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(
                title = "Próximos Eventos",
                isLive = false,
                onViewAllClick = { /* Handle Filters */ }
            )
        }

        items(upcomingEvents) { event ->
            UpcomingEventCard(event = event)
        }
    }
}

@Composable
fun PromoBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .height(125.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SportFlowDarkBlue)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xFF1E3A8A).copy(alpha = 0.2f),
                                SportFlowGreen.copy(alpha = 0.05f)
                            )
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(130.dp)
                    .align(Alignment.CenterEnd)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.15f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsRun,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.08f),
                    modifier = Modifier.size(90.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "O SEU",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "DESEMPENHO É A",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "NOSSA META.",
                    color = SportFlowGreen,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    isLive: Boolean,
    onViewAllClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = SportFlowDarkBlue
        )

        Spacer(modifier = Modifier.width(8.dp))

        if (isLive) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFDCFCE7))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF22C55E))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "LIVE",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF15803D)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        if (isLive) {
            Text(
                text = "Ver todos",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = SportFlowTextGray,
                modifier = Modifier.clickable { onViewAllClick() }
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onViewAllClick,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFEFF6FF))
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filtrar",
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(
                    onClick = onViewAllClick,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFEFF6FF))
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Calendário",
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LiveEventCard(event: LiveEvent) {
    val cardBg = if (event.isDarkTheme) SportFlowDarkBlue else Color.White
    val textPrimary = if (event.isDarkTheme) Color.White else SportFlowDarkBlue
    val textSecondary = if (event.isDarkTheme) SportFlowGreen else Color(0xFF64748B)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = event.category,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = textSecondary,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = event.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Icon(
                    imageVector = event.icon,
                    contentDescription = null,
                    tint = if (event.isDarkTheme) SportFlowGreen else Color(0xFF16A34A),
                    modifier = Modifier
                        .size(24.dp)
                        .padding(2.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = event.statusLabel,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (event.isDarkTheme) Color.White.copy(alpha = 0.8f) else Color(0xFF475569)
                )
                Text(
                    text = event.statusValue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (event.isDarkTheme) SportFlowGreen else Color(0xFF16A34A)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { event.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = SportFlowGreen,
                trackColor = if (event.isDarkTheme) Color.White.copy(alpha = 0.1f) else Color(0xFFE2E8F0)
            )
        }
    }
}

@Composable
fun UpcomingEventCard(event: UpcomingEvent) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF).copy(alpha = 0.7f)),
        border = BorderStroke(0.5.dp, Color(0xFFDBEAFE))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .size(width = 52.dp, height = 58.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
                    .border(0.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = event.month,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = event.day,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = SportFlowDarkBlue,
                    lineHeight = 22.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = event.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = SportFlowDarkBlue,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = event.location,
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = event.time,
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SportFlowDarkBlue)
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = event.categoryBadge,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(SportFlowGreen)
                            .clickable { /* Handle action */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ChevronRight,
                            contentDescription = "Detalhes",
                            tint = SportFlowDarkBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserHomeScreenPreview() {
    UserHomeScreen()
}
