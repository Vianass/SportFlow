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
import androidx.compose.runtime.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sportflow.app.ui.theme.SportFlowDarkBlue
import com.sportflow.app.ui.theme.SportFlowGreen
import com.sportflow.app.ui.theme.SportFlowTextGray
import com.sportflow.app.ui.viewmodel.AdminViewModel

// Data model for Admin Active Tournaments
data class AdminActiveTournament(
    val title: String,
    val info: String,
    val startsInHours: Int? = null,
    val sportType: String,
    val icon: ImageVector
)

// Data model for Upcoming Matches
data class UpcomingMatch(
    val time: String,
    val homeTeam: String,
    val awayTeam: String
)

// Data model for Admin Alertas
data class AdminNotification(
    val title: String,
    val body: String,
    val timeLabel: String,
    val color: Color
)

@Composable
fun AdminHomeScreen(
    viewModel: AdminViewModel = viewModel(),
    onOpenEvents: () -> Unit = {},
    onCreateEvent: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val allActiveTournaments = state.tournaments
        .filterNot { isAdminFinishedStatus(it.status) }
    val activeTournaments = allActiveTournaments
        .take(5)
        .map { tournament ->
            AdminActiveTournament(
                title = tournament.name,
                info = listOfNotNull(tournament.sport, tournament.category, tournament.location)
                    .joinToString(" • ")
                    .ifBlank { tournament.startDate },
                sportType = tournament.sport.orEmpty().uppercase(),
                icon = adminSportIcon(tournament.sport)
            )
        }

    val upcomingMatches = state.games
        .filter { it.estado.equals("NAO_INICIADO", true) }
        .sortedBy { it.dateTime }
        .take(5)
        .map { game ->
            UpcomingMatch(
                time = game.dateTime.substringAfter("T", game.dateTime).take(5),
                homeTeam = game.homeTeamId?.let { state.teamNames[it] ?: "Equipa #$it" } ?: "Equipa por definir",
                awayTeam = game.awayTeamId?.let { state.teamNames[it] ?: "Equipa #$it" } ?: "Equipa por definir"
            )
        }

    val notifications = state.pendingOrganizers.take(5).map { organizer ->
        AdminNotification(
            title = "Organizador pendente",
            body = "${organizer.nome} aguarda aprovação administrativa.",
            timeLabel = organizer.criadoEm?.take(10).orEmpty(),
            color = Color(0xFFF59E0B)
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // 1. Hero Title & Director Message
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                // Slogan/Label
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFDCFCE7))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "VISÃO GERAL DO SISTEMA",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF15803D),
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Painel de ",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = SportFlowDarkBlue
                    )
                    Text(
                        text = "Controlo",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = SportFlowGreen
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Bem-vindo novamente, Diretor. Existem ${allActiveTournaments.size} torneios ativos e ${state.stats.totalGames} jogos registados.",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 18.sp
                )
            }
        }

        // 2. Action Buttons (+ Novo Evento, Editar Evento)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Button 1: Novo Evento
                Button(
                    onClick = onCreateEvent,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEFF6FF)),
                    border = BorderStroke(0.5.dp, Color(0xFFDBEAFE))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color(0xFF1E40AF),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Novo Evento",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E40AF)
                        )
                    }
                }

                // Button 2: Editar Evento
                Button(
                    // TODO: ligar edição quando existir updateTournament no repository.
                    onClick = onOpenEvents,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC6F6D5)) // Light green
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = Color(0xFF0F5A36),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Editar Evento",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F5A36)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. Torneios Ativos (Active Tournaments) Section
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Torneios Ativos",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SportFlowDarkBlue
                )
                Text(
                    text = "Ver Todos",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF16A34A),
                    modifier = Modifier.clickable(onClick = onOpenEvents)
                )
            }
        }

        items(activeTournaments) { tournament ->
            AdminActiveTournamentCard(tournament = tournament)
        }

        // 4. METRIC GRID (Atletas, Receita)
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Total de atletas registados.
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = null,
                            tint = Color(0xFF16A34A),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.stats.totalAthletes.toString(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF0F5A36)
                        )
                        Text(
                            text = "ATLETAS",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF15803D),
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Card 2: 15k € RECEITA
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = null,
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.stats.totalRevenue?.let { "%.2f €".format(it) } ?: "0.00 €",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1E3A8A)
                        )
                        Text(
                            text = "RECEITA ESTIMADA",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D4ED8),
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }

        // 5. Growth Performance Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SportFlowDarkBlue)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "ORGANIZADORES PENDENTES",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportFlowGreen,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${state.stats.pendingOrganizers} por validar",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = {
                            if (state.stats.totalOrganizers == 0) 0f
                            else (state.stats.pendingOrganizers.toFloat() / state.stats.totalOrganizers).coerceIn(0f, 1f)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(CircleShape),
                        color = SportFlowGreen,
                        trackColor = Color.White.copy(alpha = 0.1f)
                    )
                }
            }
        }

        // 6. Próximos Jogos Section
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Próximos Jogos",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SportFlowDarkBlue
                )
                // TODO: adicionar filtros quando existir um critério funcional definido para este resumo.
                IconButton(onClick = {}, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Filtrar",
                        tint = SportFlowDarkBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        items(upcomingMatches) { match ->
            AdminMatchCard(match = match)
        }

        // 7. Notificações Container Card
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 10.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF).copy(alpha = 0.6f)),
                border = BorderStroke(0.5.dp, Color(0xFFDBEAFE))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "Notificações",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportFlowDarkBlue
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    notifications.forEach { notification ->
                        NotificationRow(notification = notification)
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // VER HISTÓRICO Button/Link
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { /* History */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "VER HISTÓRICO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF16A34A),
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminActiveTournamentCard(tournament: AdminActiveTournament) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 10.dp)
            .height(130.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Gradient base matching cover theme
            val brush = remember {
                when (tournament.sportType) {
                    "BASKETBALL" -> Brush.verticalGradient(listOf(Color(0xFF1E1B4B), Color(0xFF312E81)))
                    else -> Brush.verticalGradient(listOf(Color(0xFF0F172A), Color(0xFF1E293B)))
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(brush)
            ) {
                Icon(
                    imageVector = tournament.icon,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.08f),
                    modifier = Modifier
                        .size(110.dp)
                        .align(Alignment.CenterEnd)
                )
            }

            // Starts in badge for basketball
            if (tournament.startsInHours != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(14.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "INICIA EM ${tournament.startsInHours}H",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        color = SportFlowGreen,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Overlay Text
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(18.dp)
                    .align(Alignment.BottomStart),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = tournament.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = tournament.info,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun AdminMatchCard(match: UpcomingMatch) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp),
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
            // Left vertical green border highlight
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(36.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF22C55E))
            )

            Spacer(modifier = Modifier.width(14.dp))

            // Time column
            Column {
                Text(
                    text = match.time,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = SportFlowDarkBlue
                )
                Text(
                    text = "HOJE",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Match details row (HomeTeam vs AwayTeam)
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = match.homeTeam, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SportFlowDarkBlue)
                    Text(text = "CASA", fontSize = 8.sp, color = Color(0xFF94A3B8))
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFEFF6FF))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = "VS", fontSize = 8.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2563EB))
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = match.awayTeam, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SportFlowDarkBlue)
                    Text(text = "FORA", fontSize = 8.sp, color = Color(0xFF94A3B8))
                }
            }

            // Edit Action buttons
            // TODO: ligar edição quando o fluxo Admin definir os campos editáveis de um jogo.
            IconButton(onClick = {}, modifier = Modifier.size(28.dp)) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar",
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(16.dp)
                )
            }
            // TODO: ligar menu quando existirem ações administrativas definidas para jogos.
            IconButton(onClick = {}, modifier = Modifier.size(28.dp)) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Opções",
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun NotificationRow(notification: AdminNotification) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Bullet circle
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(notification.color)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = notification.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SportFlowDarkBlue
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = notification.body,
                fontSize = 11.sp,
                color = Color(0xFF475569),
                lineHeight = 14.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = notification.timeLabel,
                fontSize = 9.sp,
                color = Color(0xFF94A3B8)
            )
        }
    }
}

private fun adminSportIcon(sport: String?): ImageVector = when (sport?.uppercase()) {
    "BASQUETEBOL", "BASKETBALL" -> Icons.Default.SportsBasketball
    "TÉNIS", "TENIS", "TENNIS" -> Icons.Default.SportsTennis
    else -> Icons.Default.SportsFootball
}

private fun isAdminFinishedStatus(status: String): Boolean {
    val normalized = status.uppercase().replace('Í', 'I').replace('Á', 'A')
    return normalized == "FINALIZADO" || normalized == "CONCLUIDO" || normalized == "TERMINADO"
}

@Preview(showBackground = true)
@Composable
fun AdminHomeScreenPreview() {
    AdminHomeScreen()
}
