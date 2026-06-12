package com.sportflow.app.ui.screens.organizador

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sportflow.app.R
import com.sportflow.app.model.EligiblePlayer
import com.sportflow.app.model.Enrollment
import com.sportflow.app.model.Game
import com.sportflow.app.model.GameEvent
import com.sportflow.app.model.Team
import com.sportflow.app.model.TopPerformer
import com.sportflow.app.model.Tournament
import com.sportflow.app.ui.theme.SportFlowDarkBlue
import com.sportflow.app.ui.theme.SportFlowGreen
import com.sportflow.app.ui.theme.SportFlowTextGray
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun OrganizadorEventsScreen(
    viewModel: OrganizadorEventsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTournament by remember { mutableStateOf<Tournament?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadTournaments()
    }

    selectedTournament?.let { tournament ->
        LaunchedEffect(tournament.id) {
            viewModel.loadManagementDataForTournament(tournament.id)
        }

        OrganizadorEventDetailScreen(
            tournament = tournament,
            uiState = uiState,
            onBack = {
                viewModel.clearSelectionData()
                selectedTournament = null
            },
            onRetryEnrollments = { viewModel.loadEnrollmentsForTournament(tournament.id) },
            onRetryTeams = { viewModel.loadTeamsForTournament(tournament.id) },
            onRetryGames = { viewModel.loadGamesForTournament(tournament.id) },
            onRetryGameEvents = { viewModel.loadGameEventsForTournament(tournament.id) },
            onCreateTeam = { teamName -> viewModel.createTeam(tournament.id, teamName) },
            onCreateGame = { homeTeamId, awayTeamId, dateTime ->
                viewModel.createGame(
                    tournamentId = tournament.id,
                    homeTeamId = homeTeamId,
                    awayTeamId = awayTeamId,
                    dateTime = dateTime
                )
            },
            onStartGame = { gameId ->
                viewModel.startGame(
                    tournamentId = tournament.id,
                    gameId = gameId
                )
            },
            onFinishGame = { gameId, result ->
                viewModel.finishGame(
                    tournamentId = tournament.id,
                    gameId = gameId,
                    result = result
                )
            },
            onRegisterGameEvent = { gameId, playerId, eventType, minute ->
                viewModel.registerGameEvent(
                    tournamentId = tournament.id,
                    gameId = gameId,
                    playerId = playerId,
                    eventType = eventType,
                    minute = minute
                )
            },
            onLoadEligiblePlayers = { viewModel.loadEligiblePlayersForTournament(tournament.id) },
            onAssociatePlayer = { teamId, playerId, shirtNumber ->
                viewModel.associatePlayerToTeam(
                    tournamentId = tournament.id,
                    teamId = teamId,
                    playerId = playerId,
                    shirtNumber = shirtNumber
                )
            },
            onApproveEnrollment = { enrollmentId ->
                viewModel.approveEnrollment(enrollmentId, tournament.id)
            },
            onRejectEnrollment = { enrollmentId ->
                viewModel.rejectEnrollment(enrollmentId, tournament.id)
            }
        )
        return
    }

    EventsList(
        uiState = uiState,
        onRetry = viewModel::loadTournaments,
        onManageEventClick = { tournament -> selectedTournament = tournament }
    )
}

@Composable
fun EventsList(
    uiState: OrganizadorEventsUiState,
    onRetry: () -> Unit,
    onManageEventClick: (Tournament) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(top = 24.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "Gestão de\nEventos",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 38.sp,
                    color = SportFlowDarkBlue
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .width(135.dp)
                        .height(4.dp)
                        .background(Color(0xFF16A34A))
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Seleciona um evento para gerir inscrições, equipas e calendário.",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 20.sp
                )
            }
        }

        when {
            uiState.isLoading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = SportFlowGreen)
                    }
                }
            }

            uiState.errorMessage != null -> {
                item {
                    ErrorState(
                        message = uiState.errorMessage,
                        onRetry = onRetry
                    )
                }
            }

            uiState.tournaments.isEmpty() -> {
                item {
                    EmptyState()
                }
            }

            else -> {
                items(
                    items = uiState.tournaments,
                    key = { it.id }
                ) { tournament ->
                    val statusStyle = tournament.status.toStatusStyle()

                    EventCard(
                        imageRes = tournament.sport.toImageRes(),
                        statusText = tournament.status.uppercase(Locale.ROOT),
                        statusColor = statusStyle.textColor,
                        statusBg = statusStyle.backgroundColor,
                        title = tournament.name,
                        date = tournament.startDate.formatTournamentDate(),
                        location = tournament.location ?: "Local a definir",
                        buttonText = "Gerir Evento  →",
                        buttonBg = Color(0xFF67FF9A),
                        buttonContentColor = SportFlowDarkBlue,
                        hasDot = tournament.status.equals("ABERTO", ignoreCase = true),
                        onButtonClick = { onManageEventClick(tournament) }
                    )
                }
            }
        }
    }
}

@Composable
fun OrganizadorEventDetailScreen(
    tournament: Tournament,
    uiState: OrganizadorEventsUiState,
    onBack: () -> Unit,
    onRetryEnrollments: () -> Unit,
    onRetryTeams: () -> Unit,
    onRetryGames: () -> Unit,
    onRetryGameEvents: () -> Unit,
    onCreateTeam: (String) -> Unit,
    onCreateGame: (Long, Long, String) -> Unit,
    onStartGame: (Long) -> Unit,
    onFinishGame: (Long, String) -> Unit,
    onRegisterGameEvent: (Long, String, String, Int) -> Unit,
    onLoadEligiblePlayers: () -> Unit,
    onAssociatePlayer: (Long, String, Int?) -> Unit,
    onApproveEnrollment: (Long) -> Unit,
    onRejectEnrollment: (Long) -> Unit
) {
    var showCreateTeamDialog by remember { mutableStateOf(false) }
    var showCreateGameDialog by remember { mutableStateOf(false) }
    var gameToFinish by remember { mutableStateOf<Game?>(null) }
    var teamForPlayerAssociation by remember { mutableStateOf<Team?>(null) }
    val confirmedPlayers = uiState.selectedTournamentEnrollments.count { enrollment ->
        enrollment.status.equals("APROVADA", ignoreCase = true) &&
                enrollment.paymentStatus.equals("PAGO", ignoreCase = true)
    }

    if (showCreateTeamDialog) {
        CreateTeamDialog(
            isCreating = uiState.isCreatingTeam,
            onDismiss = { showCreateTeamDialog = false },
            onConfirm = { teamName ->
                onCreateTeam(teamName)
                showCreateTeamDialog = false
            }
        )
    }

    if (showCreateGameDialog) {
        CreateGameDialog(
            teams = uiState.selectedTournamentTeams,
            isCreating = uiState.isCreatingGame,
            onDismiss = { showCreateGameDialog = false },
            onConfirm = { homeTeamId, awayTeamId, dateTime ->
                onCreateGame(homeTeamId, awayTeamId, dateTime)
                showCreateGameDialog = false
            }
        )
    }

    gameToFinish?.let { game ->
        FinishGameDialog(
            game = game,
            isUpdating = uiState.updatingGameId == game.id,
            onDismiss = { gameToFinish = null },
            onConfirm = { result ->
                onFinishGame(game.id, result)
                gameToFinish = null
            }
        )
    }

    teamForPlayerAssociation?.let { team ->
        LaunchedEffect(team.id) {
            onLoadEligiblePlayers()
        }

        AssociatePlayerDialog(
            team = team,
            eligiblePlayers = uiState.eligiblePlayers,
            isLoading = uiState.isLoadingEligiblePlayers,
            isAssociating = uiState.associatingTeamId == team.id,
            errorMessage = uiState.eligiblePlayersErrorMessage,
            onRetry = onLoadEligiblePlayers,
            onDismiss = { teamForPlayerAssociation = null },
            onConfirm = { playerId, shirtNumber ->
                onAssociatePlayer(team.id, playerId, shirtNumber)
                teamForPlayerAssociation = null
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(top = 24.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Voltar",
                    tint = SportFlowDarkBlue,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .clickable { onBack() }
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = tournament.name,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 34.sp,
                    color = SportFlowDarkBlue
                )
            }
        }

        item {
            EventStatusCard(
                teamsCount = uiState.selectedTournamentTeams.size,
                maxTeams = tournament.maxCapacity,
                isLoadingTeams = uiState.isLoadingTeams
            )
        }

        item {
            PlayersRegisteredCard(
                confirmedPlayers = confirmedPlayers,
                isLoadingEnrollments = uiState.isLoadingEnrollments
            )
        }

        item {
            LocationAndNewTeamCard(
                location = tournament.location ?: "Local a definir",
                onCreateTeamClick = { showCreateTeamDialog = true }
            )
        }

        item {
            TeamsSection(
                uiState = uiState,
                onRetry = onRetryTeams,
                onCreateTeamClick = { showCreateTeamDialog = true },
                onAssociatePlayerClick = { team -> teamForPlayerAssociation = team }
            )
        }

        item {
            CalendarGamesSection(
                uiState = uiState,
                onRetry = onRetryGames,
                onCreateGameClick = { showCreateGameDialog = true },
                onStartGame = onStartGame,
                onFinishGameClick = { game -> gameToFinish = game }
            )
        }

        item {
            MatchRegistrationSection(
                uiState = uiState,
                onRetry = onRetryGameEvents,
                onRegisterEvent = onRegisterGameEvent
            )
        }

        item {
            EnrollmentsSection(
                uiState = uiState,
                onRetry = onRetryEnrollments,
                onApproveEnrollment = onApproveEnrollment,
                onRejectEnrollment = onRejectEnrollment
            )
        }
    }
}

@Composable
private fun EventStatusCard(
    teamsCount: Int,
    maxTeams: Int?,
    isLoadingTeams: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SportFlowDarkBlue)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "STATUS GERAL",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (isLoadingTeams) "--" else "$teamsCount/${maxTeams ?: "-"}",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF67FF9A)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "EQUIPAS CRIADAS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8)
                )
            }

            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF67FF9A))
                    .align(Alignment.TopEnd)
            )

            Row(
                modifier = Modifier.align(Alignment.BottomEnd),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Box(modifier = Modifier.size(width = 6.dp, height = 12.dp).background(Color(0xFF334155), RoundedCornerShape(2.dp)))
                Box(modifier = Modifier.size(width = 6.dp, height = 18.dp).background(Color(0xFF334155), RoundedCornerShape(2.dp)))
                Box(modifier = Modifier.size(width = 6.dp, height = 24.dp).background(Color(0xFF67FF9A), RoundedCornerShape(2.dp)))
                Box(modifier = Modifier.size(width = 6.dp, height = 30.dp).background(Color(0xFF67FF9A), RoundedCornerShape(2.dp)))
            }
        }
    }
}

@Composable
private fun PlayersRegisteredCard(
    confirmedPlayers: Int,
    isLoadingEnrollments: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "JOGADORES CONFIRMADOS",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isLoadingEnrollments) "--" else confirmedPlayers.toString(),
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = SportFlowDarkBlue
            )
        }
    }
}

@Composable
private fun LocationAndNewTeamCard(
    location: String,
    onCreateTeamClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(0.5.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFEFF6FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = SportFlowGreen,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = location,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = SportFlowDarkBlue
                )
                Text(
                    text = "LOCALIZAÇÃO DO TORNEIO",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )
            }

            Button(
                onClick = onCreateTeamClick,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF67FF9A)),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = SportFlowDarkBlue,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "NOVA EQUIPA",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportFlowDarkBlue
                    )
                }
            }
        }
    }
}

@Composable
private fun TeamsSection(
    uiState: OrganizadorEventsUiState,
    onRetry: () -> Unit,
    onCreateTeamClick: () -> Unit,
    onAssociatePlayerClick: (Team) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "EQUIPAS E ELENCOS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF94A3B8),
            letterSpacing = 1.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 4.dp)
        )

        if (uiState.teamsErrorMessage != null) {
            ErrorCard(
                message = uiState.teamsErrorMessage,
                onRetry = onRetry
            )
            return
        }

        if (uiState.isLoadingTeams) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(0.5.dp, Color(0xFFE2E8F0))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = SportFlowGreen)
                }
            }
            return
        }

        if (uiState.selectedTournamentTeams.isEmpty()) {
            EmptyTeamsCard(onCreateTeamClick = onCreateTeamClick)
            return
        }

        uiState.selectedTournamentTeams.forEach { team ->
            TeamRosterCard(
                team = team,
                onAssociatePlayerClick = { onAssociatePlayerClick(team) }
            )
        }

        DashedAddTeamBox(onClick = onCreateTeamClick)
    }
}

@Composable
private fun EmptyTeamsCard(onCreateTeamClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(0.5.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Groups,
                contentDescription = null,
                tint = SportFlowTextGray,
                modifier = Modifier.size(42.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Ainda não existem equipas neste torneio.",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = SportFlowDarkBlue
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onCreateTeamClick,
                colors = ButtonDefaults.buttonColors(containerColor = SportFlowGreen),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Criar primeira equipa",
                    color = SportFlowDarkBlue,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun TeamRosterCard(
    team: Team,
    onAssociatePlayerClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(0.5.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFEFF6FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = team.name.initials(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF047857)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = team.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportFlowDarkBlue
                    )
                    Text(
                        text = if (team.players.isEmpty()) "Jogadores ainda não associados" else "${team.players.size} jogador(es) associado(s)",
                        fontSize = 10.sp,
                        color = Color(0xFF64748B)
                    )
                }

                val isComplete = team.players.isNotEmpty()
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isComplete) Color(0xFFD1FAE5) else Color(0xFFFFF7ED))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isComplete) "VALIDADO" else "INCOMPLETO",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isComplete) Color(0xFF047857) else Color(0xFFEA580C)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(12.dp))

            if (team.players.isEmpty()) {
                Text(
                    text = "Nenhum jogador associado a esta equipa.",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.height(12.dp))
            } else {
                team.players.forEach { player ->
                    TeamPlayerRow(
                        name = player.name,
                        email = player.email,
                        shirtNumber = player.shirtNumber
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .border(
                        border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onAssociatePlayerClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+ ASSOCIAR JOGADOR",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }
}


@Composable
private fun TeamPlayerRow(
    name: String,
    email: String,
    shirtNumber: Int?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF1F5F9)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.initials(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = SportFlowDarkBlue
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = SportFlowDarkBlue
            )
            Text(
                text = email,
                fontSize = 10.sp,
                color = Color(0xFF64748B),
                lineHeight = 13.sp
            )
        }

        Text(
            text = shirtNumber?.let { "#$it" } ?: "S/N",
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = SportFlowGreen
        )
    }
}

@Composable
private fun AssociatePlayerDialog(
    team: Team,
    eligiblePlayers: List<EligiblePlayer>,
    isLoading: Boolean,
    isAssociating: Boolean,
    errorMessage: String?,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (String, Int?) -> Unit
) {
    var selectedPlayerId by remember(team.id, eligiblePlayers) {
        mutableStateOf(eligiblePlayers.firstOrNull()?.playerId)
    }
    var shirtNumberText by remember(team.id) { mutableStateOf("") }
    val parsedShirtNumber = shirtNumberText.trim().takeIf { it.isNotBlank() }?.toIntOrNull()
    val isShirtNumberInvalid = shirtNumberText.isNotBlank() && parsedShirtNumber == null

    AlertDialog(
        onDismissRequest = { if (!isAssociating) onDismiss() },
        title = {
            Text(
                text = "Associar jogador",
                fontWeight = FontWeight.Bold,
                color = SportFlowDarkBlue
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Equipa: ${team.name}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SportFlowDarkBlue
                )

                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 22.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = SportFlowGreen)
                        }
                    }

                    errorMessage != null -> {
                        Text(
                            text = errorMessage,
                            fontSize = 12.sp,
                            color = Color(0xFFDC2626)
                        )
                        OutlinedButton(onClick = onRetry) {
                            Text("Tentar novamente")
                        }
                    }

                    eligiblePlayers.isEmpty() -> {
                        Text(
                            text = "Não há jogadores aprovados e pagos disponíveis para associar.",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B),
                            lineHeight = 18.sp
                        )
                    }

                    else -> {
                        Text(
                            text = "Seleciona um atleta aprovado e com pagamento confirmado.",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 220.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            eligiblePlayers.forEach { player ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable(enabled = !isAssociating) {
                                            selectedPlayerId = player.playerId
                                        }
                                        .background(
                                            if (selectedPlayerId == player.playerId) Color(0xFFEFF6FF) else Color(0xFFF8FAFC)
                                        )
                                        .border(
                                            width = 0.5.dp,
                                            color = if (selectedPlayerId == player.playerId) SportFlowGreen else Color(0xFFE2E8F0),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selectedPlayerId == player.playerId,
                                        onClick = { selectedPlayerId = player.playerId },
                                        enabled = !isAssociating
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = player.name,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SportFlowDarkBlue
                                        )
                                        Text(
                                            text = player.email,
                                            fontSize = 10.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }
                            }
                        }

                        OutlinedTextField(
                            value = shirtNumberText,
                            onValueChange = { value ->
                                shirtNumberText = value.filter { char -> char.isDigit() }.take(3)
                            },
                            label = { Text("Número da camisola opcional") },
                            singleLine = true,
                            enabled = !isAssociating,
                            isError = isShirtNumberInvalid,
                            supportingText = if (isShirtNumberInvalid) {
                                { Text("Insere um número válido.") }
                            } else null,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val playerId = selectedPlayerId ?: return@Button
                    onConfirm(playerId, parsedShirtNumber)
                },
                enabled = !isLoading && !isAssociating && selectedPlayerId != null && !isShirtNumberInvalid,
                colors = ButtonDefaults.buttonColors(containerColor = SportFlowGreen)
            ) {
                if (isAssociating) {
                    CircularProgressIndicator(
                        color = SportFlowDarkBlue,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Text(
                        text = "Associar",
                        color = SportFlowDarkBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isAssociating
            ) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun DashedAddTeamBox(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(74.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(
                border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Criar equipa",
            tint = Color(0xFF94A3B8),
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
private fun CreateTeamDialog(
    isCreating: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var teamName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { if (!isCreating) onDismiss() },
        title = {
            Text(
                text = "Nova equipa",
                fontWeight = FontWeight.Bold,
                color = SportFlowDarkBlue
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Cria uma equipa real neste torneio. Os jogadores serão associados na próxima fase.",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )
                OutlinedTextField(
                    value = teamName,
                    onValueChange = { teamName = it },
                    label = { Text("Nome da equipa") },
                    singleLine = true,
                    enabled = !isCreating,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(teamName) },
                enabled = !isCreating && teamName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = SportFlowGreen)
            ) {
                if (isCreating) {
                    CircularProgressIndicator(
                        color = SportFlowDarkBlue,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Text(
                        text = "Criar equipa",
                        color = SportFlowDarkBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isCreating
            ) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun CalendarGamesSection(
    uiState: OrganizadorEventsUiState,
    onRetry: () -> Unit,
    onCreateGameClick: () -> Unit,
    onStartGame: (Long) -> Unit,
    onFinishGameClick: (Game) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(0.5.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Gestão de Calendário",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportFlowDarkBlue
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Cria jogos reais entre equipas do torneio.",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }

                IconButton(onClick = onRetry) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Atualizar jogos",
                        tint = SportFlowGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onCreateGameClick,
                enabled = uiState.selectedTournamentTeams.size >= 2 && !uiState.isCreatingGame,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEFF6FF),
                    contentColor = Color(0xFF2563EB),
                    disabledContainerColor = Color(0xFFF1F5F9),
                    disabledContentColor = Color(0xFF94A3B8)
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Adicionar Jogo",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (uiState.selectedTournamentTeams.size < 2) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Cria pelo menos duas equipas antes de adicionar jogos.",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            when {
                uiState.gamesErrorMessage != null -> {
                    Text(
                        text = uiState.gamesErrorMessage,
                        fontSize = 12.sp,
                        color = Color(0xFFDC2626),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFFFF1F2))
                            .padding(12.dp)
                    )
                }

                uiState.isLoadingGames -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = SportFlowGreen)
                    }
                }

                uiState.selectedTournamentGames.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFF8FAFC))
                            .border(0.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = null,
                            tint = SportFlowTextGray,
                            modifier = Modifier.size(42.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Ainda não existem jogos neste torneio.",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SportFlowDarkBlue
                        )
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        uiState.selectedTournamentGames.forEachIndexed { index, game ->
                            CalendarGameItem(
                                jornada = "JOGO ${index + 1}",
                                game = game,
                                isUpdating = uiState.updatingGameId == game.id,
                                onStartGame = { onStartGame(game.id) },
                                onFinishGame = { onFinishGameClick(game) }
                            )
                        }

                        DashedAddGameBox(onClick = onCreateGameClick)
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarGameItem(
    jornada: String,
    game: Game,
    isUpdating: Boolean,
    onStartGame: () -> Unit,
    onFinishGame: () -> Unit
) {
    val statusStyle = game.status.toGameStatusStyle()
    val normalizedStatus = game.status.uppercase(Locale.ROOT)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(
                width = 1.dp,
                color = statusStyle.borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = jornada,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF64748B),
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${game.homeTeamName ?: "Equipa casa"} vs ${game.awayTeamName ?: "Equipa fora"}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = SportFlowDarkBlue
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${game.dateTime.formatShortDate()} • ${game.dateTime.formatGameTime()}",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
                game.result?.takeIf { it.isNotBlank() }?.let { result ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Resultado: $result",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF047857)
                    )
                }
            }

            Text(
                text = statusStyle.label,
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(statusStyle.badgeColor)
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            )
        }

        if (normalizedStatus == "NAO_INICIADO" || normalizedStatus == "EM_DECORRER") {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = if (normalizedStatus == "NAO_INICIADO") onStartGame else onFinishGame,
                    enabled = !isUpdating,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, if (normalizedStatus == "NAO_INICIADO") SportFlowGreen else Color(0xFFEF4444)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (normalizedStatus == "NAO_INICIADO") SportFlowGreen else Color(0xFFEF4444)
                    ),
                    modifier = Modifier.height(34.dp)
                ) {
                    if (isUpdating) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(14.dp),
                            color = if (normalizedStatus == "NAO_INICIADO") SportFlowGreen else Color(0xFFEF4444)
                        )
                    } else {
                        Text(
                            text = if (normalizedStatus == "NAO_INICIADO") "INICIAR" else "TERMINAR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DashedAddGameBox(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(
                border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Criar jogo",
            tint = Color(0xFF94A3B8),
            modifier = Modifier.size(26.dp)
        )
    }
}

@Composable
private fun FinishGameDialog(
    game: Game,
    isUpdating: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var resultText by remember(game.id) { mutableStateOf(game.result.orEmpty()) }
    val canSubmit = resultText.trim().isNotBlank()

    AlertDialog(
        onDismissRequest = { if (!isUpdating) onDismiss() },
        title = {
            Text(
                text = "Terminar jogo",
                fontWeight = FontWeight.Bold,
                color = SportFlowDarkBlue
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "${game.homeTeamName ?: "Equipa casa"} vs ${game.awayTeamName ?: "Equipa fora"}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = SportFlowDarkBlue
                )
                Text(
                    text = "Indica o resultado final antes de terminar o jogo.",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
                OutlinedTextField(
                    value = resultText,
                    onValueChange = { resultText = it.take(20) },
                    label = { Text("Resultado, ex: 2-1") },
                    singleLine = true,
                    enabled = !isUpdating,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(resultText.trim()) },
                enabled = !isUpdating && canSubmit,
                colors = ButtonDefaults.buttonColors(containerColor = SportFlowGreen)
            ) {
                if (isUpdating) {
                    CircularProgressIndicator(
                        color = SportFlowDarkBlue,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Text(
                        text = "Terminar jogo",
                        color = SportFlowDarkBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isUpdating
            ) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun CreateGameDialog(
    teams: List<Team>,
    isCreating: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (Long, Long, String) -> Unit
) {
    var homeTeamId by remember(teams) { mutableStateOf(teams.firstOrNull()?.id) }
    var awayTeamId by remember(teams) { mutableStateOf(teams.drop(1).firstOrNull()?.id) }
    var dateText by remember { mutableStateOf("") }
    var timeText by remember { mutableStateOf("") }

    val isDateValid = Regex("\\d{4}-\\d{2}-\\d{2}").matches(dateText.trim())
    val isTimeValid = Regex("\\d{2}:\\d{2}").matches(timeText.trim())
    val selectedDifferentTeams = homeTeamId != null && awayTeamId != null && homeTeamId != awayTeamId

    AlertDialog(
        onDismissRequest = { if (!isCreating) onDismiss() },
        title = {
            Text(
                text = "Adicionar jogo",
                fontWeight = FontWeight.Bold,
                color = SportFlowDarkBlue
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (teams.size < 2) {
                    Text(
                        text = "É necessário criar pelo menos duas equipas antes de criar jogos.",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )
                } else {
                    Text(
                        text = "Seleciona as equipas e define a data/hora do jogo.",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )

                    TeamSelectorBlock(
                        title = "Equipa casa",
                        teams = teams,
                        selectedTeamId = homeTeamId,
                        enabled = !isCreating,
                        onSelect = { homeTeamId = it }
                    )

                    TeamSelectorBlock(
                        title = "Equipa fora",
                        teams = teams,
                        selectedTeamId = awayTeamId,
                        enabled = !isCreating,
                        onSelect = { awayTeamId = it }
                    )

                    OutlinedTextField(
                        value = dateText,
                        onValueChange = { dateText = it.take(10) },
                        label = { Text("Data AAAA-MM-DD") },
                        singleLine = true,
                        enabled = !isCreating,
                        isError = dateText.isNotBlank() && !isDateValid,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = timeText,
                        onValueChange = { timeText = it.take(5) },
                        label = { Text("Hora HH:mm") },
                        singleLine = true,
                        enabled = !isCreating,
                        isError = timeText.isNotBlank() && !isTimeValid,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (!selectedDifferentTeams) {
                        Text(
                            text = "As equipas têm de ser diferentes.",
                            fontSize = 11.sp,
                            color = Color(0xFFDC2626)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val home = homeTeamId ?: return@Button
                    val away = awayTeamId ?: return@Button
                    val dateTime = "${dateText.trim()}T${timeText.trim()}:00Z"
                    onConfirm(home, away, dateTime)
                },
                enabled = teams.size >= 2 && !isCreating && selectedDifferentTeams && isDateValid && isTimeValid,
                colors = ButtonDefaults.buttonColors(containerColor = SportFlowGreen)
            ) {
                if (isCreating) {
                    CircularProgressIndicator(
                        color = SportFlowDarkBlue,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Text(
                        text = "Criar jogo",
                        color = SportFlowDarkBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isCreating
            ) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun TeamSelectorBlock(
    title: String,
    teams: List<Team>,
    selectedTeamId: Long?,
    enabled: Boolean,
    onSelect: (Long) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF64748B)
        )
        teams.forEach { team ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(enabled = enabled) { onSelect(team.id) }
                    .background(if (selectedTeamId == team.id) Color(0xFFEFF6FF) else Color(0xFFF8FAFC))
                    .border(
                        width = 0.5.dp,
                        color = if (selectedTeamId == team.id) SportFlowGreen else Color(0xFFE2E8F0),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedTeamId == team.id,
                    onClick = { onSelect(team.id) },
                    enabled = enabled
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = team.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SportFlowDarkBlue
                )
            }
        }
    }
}

private data class GameStatusStyle(
    val label: String,
    val badgeColor: Color,
    val borderColor: Color
)

private fun String.toGameStatusStyle(): GameStatusStyle {
    return when (uppercase(Locale.ROOT)) {
        "EM_DECORRER" -> GameStatusStyle(
            label = "EM DECORRER",
            badgeColor = Color(0xFF16A34A),
            borderColor = Color(0xFF16A34A)
        )
        "TERMINADO" -> GameStatusStyle(
            label = "TERMINADO",
            badgeColor = Color(0xFFEF4444),
            borderColor = Color(0xFFEF4444)
        )
        else -> GameStatusStyle(
            label = "AGENDADO",
            badgeColor = Color(0xFF2563EB),
            borderColor = Color(0xFF94A3B8)
        )
    }
}

private fun String.formatGameTime(): String {
    val time = runCatching {
        OffsetDateTime.parse(this).toLocalTime()
    }.getOrNull()

    return time?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "--:--"
}

@Composable
private fun MatchRegistrationSection(
    uiState: OrganizadorEventsUiState,
    onRetry: () -> Unit,
    onRegisterEvent: (Long, String, String, Int) -> Unit
) {
    val liveGames = remember(uiState.selectedTournamentGames) {
        uiState.selectedTournamentGames.filter { game ->
            game.status.equals("EM_DECORRER", ignoreCase = true)
        }
    }
    var selectedGameId by remember { mutableStateOf<Long?>(null) }
    var selectedEventType by remember { mutableStateOf("GOLO") }
    var selectedPlayerId by remember { mutableStateOf<String?>(null) }
    var minuteText by remember { mutableStateOf("") }

    LaunchedEffect(liveGames.map { it.id }) {
        val liveGameIds = liveGames.map { it.id }
        if (selectedGameId !in liveGameIds) {
            selectedGameId = liveGames.firstOrNull()?.id
        }
    }

    val selectedGame = liveGames.firstOrNull { game -> game.id == selectedGameId }
    val playersForSelectedGame = remember(selectedGame, uiState.selectedTournamentTeams) {
        if (selectedGame == null) {
            emptyList()
        } else {
            val gameTeamIds = setOfNotNull(selectedGame.homeTeamId, selectedGame.awayTeamId)
            uiState.selectedTournamentTeams
                .filter { team -> team.id in gameTeamIds }
                .flatMap { team ->
                    team.players.map { player ->
                        MatchPlayerChoice(
                            playerId = player.playerId,
                            name = player.name,
                            teamName = team.name,
                            shirtNumber = player.shirtNumber
                        )
                    }
                }
                .sortedWith(compareBy<MatchPlayerChoice> { it.teamName.lowercase() }.thenBy { it.name.lowercase() })
        }
    }

    LaunchedEffect(selectedGame?.id, playersForSelectedGame.map { it.playerId }) {
        val playerIds = playersForSelectedGame.map { it.playerId }
        if (selectedPlayerId !in playerIds) {
            selectedPlayerId = playersForSelectedGame.firstOrNull()?.playerId
        }
    }

    val minute = minuteText.trim().toIntOrNull()
    val isMinuteInvalid = minuteText.isNotBlank() && minute == null
    val selectedGameEvents = uiState.selectedTournamentGameEvents
        .filter { event -> event.gameId == selectedGameId }
        .sortedByDescending { event -> event.minute }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
        border = BorderStroke(0.5.dp, Color(0xFFDCEBFF))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Registo de Partida",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportFlowDarkBlue
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Regista eventos reais dos jogos em curso.",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }

                IconButton(onClick = onRetry) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Atualizar eventos",
                        tint = SportFlowGreen
                    )
                }
            }

            when {
                uiState.gameEventsErrorMessage != null -> {
                    Text(
                        text = uiState.gameEventsErrorMessage,
                        fontSize = 12.sp,
                        color = Color(0xFFDC2626),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFFFF1F2))
                            .padding(12.dp)
                    )
                }

                uiState.isLoadingGameEvents -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = SportFlowGreen)
                    }
                }

                liveGames.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White)
                            .border(0.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = null,
                            tint = SportFlowTextGray,
                            modifier = Modifier.size(42.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Nenhum jogo em curso.",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SportFlowDarkBlue
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Inicia um jogo na Gestão de Calendário para registar eventos.",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B),
                            lineHeight = 16.sp
                        )
                    }
                }

                else -> {
                    if (liveGames.size > 1) {
                        SelectorBlock(
                            title = "Jogo em curso",
                            options = liveGames.map { game ->
                                SelectorOption(
                                    id = game.id.toString(),
                                    label = "${game.homeTeamName ?: "Equipa casa"} vs ${game.awayTeamName ?: "Equipa fora"}",
                                    subtitle = game.dateTime.formatGameTime()
                                )
                            },
                            selectedId = selectedGameId?.toString(),
                            enabled = !uiState.isRegisteringGameEvent,
                            onSelect = { selectedGameId = it.toLongOrNull() }
                        )
                    } else if (selectedGame != null) {
                        Text(
                            text = "${selectedGame.homeTeamName ?: "Equipa casa"} vs ${selectedGame.awayTeamName ?: "Equipa fora"}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = SportFlowDarkBlue,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .padding(12.dp)
                        )
                    }

                    SelectorBlock(
                        title = "Tipo de evento",
                        options = EVENT_TYPE_OPTIONS.map { eventType ->
                            SelectorOption(
                                id = eventType,
                                label = eventType.toGameEventLabel(),
                                subtitle = null
                            )
                        },
                        selectedId = selectedEventType,
                        enabled = !uiState.isRegisteringGameEvent,
                        onSelect = { selectedEventType = it }
                    )

                    if (playersForSelectedGame.isEmpty()) {
                        Text(
                            text = "Este jogo ainda não tem jogadores associados às equipas.",
                            fontSize = 12.sp,
                            color = Color(0xFFDC2626),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFFFF1F2))
                                .padding(12.dp)
                        )
                    } else {
                        SelectorBlock(
                            title = "Jogador",
                            options = playersForSelectedGame.map { player ->
                                SelectorOption(
                                    id = player.playerId,
                                    label = player.name,
                                    subtitle = buildString {
                                        append(player.teamName)
                                        player.shirtNumber?.let { append(" • #$it") }
                                    }
                                )
                            },
                            selectedId = selectedPlayerId,
                            enabled = !uiState.isRegisteringGameEvent,
                            onSelect = { selectedPlayerId = it }
                        )
                    }

                    OutlinedTextField(
                        value = minuteText,
                        onValueChange = { minuteText = it.filter { char -> char.isDigit() }.take(3) },
                        label = { Text("Minuto") },
                        singleLine = true,
                        enabled = !uiState.isRegisteringGameEvent,
                        isError = isMinuteInvalid,
                        supportingText = if (isMinuteInvalid) {
                            { Text("Insere um minuto válido.") }
                        } else null,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            val gameId = selectedGameId ?: return@Button
                            val playerId = selectedPlayerId ?: return@Button
                            val parsedMinute = minute ?: return@Button
                            onRegisterEvent(gameId, playerId, selectedEventType, parsedMinute)
                            minuteText = ""
                        },
                        enabled = selectedGameId != null && selectedPlayerId != null && minute != null && !uiState.isRegisteringGameEvent,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SportFlowDarkBlue),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        if (uiState.isRegisteringGameEvent) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Text(
                                text = "REGISTAR EVENTO",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }

                    MatchEventsFeed(events = selectedGameEvents)
                    TopPerformanceCard(topPerformers = uiState.topPerformers)
                }
            }
        }
    }
}

@Composable
private fun MatchEventsFeed(events: List<GameEvent>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "EVENTOS REGISTADOS",
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF94A3B8),
            letterSpacing = 0.5.sp
        )

        if (events.isEmpty()) {
            Text(
                text = "Ainda não existem eventos neste jogo.",
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )
        } else {
            events.forEach { event ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${event.minute}'",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = SportFlowGreen,
                        modifier = Modifier.width(42.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = event.eventType.toGameEventLabel(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = SportFlowDarkBlue
                        )
                        Text(
                            text = buildString {
                                append(event.playerName ?: "Jogador sem nome")
                                event.teamName?.let { append(" • $it") }
                            },
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TopPerformanceCard(topPerformers: List<TopPerformer>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TOP PERFORMANCE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF64748B),
                letterSpacing = 0.5.sp
            )
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = SportFlowGreen,
                modifier = Modifier.size(16.dp)
            )
        }

        if (topPerformers.isEmpty()) {
            Text(
                text = "Sem golos registados ainda.",
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )
        } else {
            val maxGoals = topPerformers.maxOf { it.goals }.coerceAtLeast(1)
            topPerformers.take(3).forEach { performer ->
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = performer.playerName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SportFlowDarkBlue
                        )
                        Text(
                            text = "${performer.goals} Golos",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF047857)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE2E8F0))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(performer.goals.toFloat() / maxGoals.toFloat())
                                .height(4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SportFlowGreen)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectorBlock(
    title: String,
    options: List<SelectorOption>,
    selectedId: String?,
    enabled: Boolean,
    onSelect: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF64748B)
        )
        Column(
            modifier = Modifier.heightIn(max = 210.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            options.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(enabled = enabled) { onSelect(option.id) }
                        .background(if (selectedId == option.id) Color.White else Color(0xFFF8FAFC))
                        .border(
                            width = 0.5.dp,
                            color = if (selectedId == option.id) SportFlowGreen else Color(0xFFE2E8F0),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedId == option.id,
                        onClick = { onSelect(option.id) },
                        enabled = enabled
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = option.label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SportFlowDarkBlue
                        )
                        option.subtitle?.takeIf { it.isNotBlank() }?.let { subtitle ->
                            Text(
                                text = subtitle,
                                fontSize = 10.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class MatchPlayerChoice(
    val playerId: String,
    val name: String,
    val teamName: String,
    val shirtNumber: Int?
)

private data class SelectorOption(
    val id: String,
    val label: String,
    val subtitle: String?
)

private val EVENT_TYPE_OPTIONS = listOf(
    "GOLO",
    "FALTA",
    "CARTAO_AMARELO",
    "CARTAO_VERMELHO"
)

private fun String.toGameEventLabel(): String {
    return when (uppercase(Locale.ROOT)) {
        "GOLO" -> "Golo"
        "FALTA" -> "Falta"
        "CARTAO_AMARELO" -> "Cartão amarelo"
        "CARTAO_VERMELHO" -> "Cartão vermelho"
        else -> this
    }
}


@Composable
private fun EnrollmentsSection(
    uiState: OrganizadorEventsUiState,
    onRetry: () -> Unit,
    onApproveEnrollment: (Long) -> Unit,
    onRejectEnrollment: (Long) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(0.5.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "INSCRIÇÕES",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Pedidos reais do Supabase",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = SportFlowDarkBlue
                    )
                }

                IconButton(onClick = onRetry) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Atualizar inscrições",
                        tint = SportFlowGreen
                    )
                }
            }

            if (uiState.actionMessage != null) {
                Text(
                    text = uiState.actionMessage,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF047857),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFD1FAE5))
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                )
            }

            when {
                uiState.isLoadingEnrollments -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = SportFlowGreen)
                    }
                }

                uiState.enrollmentsErrorMessage != null -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = uiState.enrollmentsErrorMessage,
                            fontSize = 12.sp,
                            color = Color(0xFFDC2626),
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(onClick = onRetry) {
                            Text("Tentar novamente")
                        }
                    }
                }

                uiState.selectedTournamentEnrollments.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.HowToReg,
                            contentDescription = null,
                            tint = SportFlowTextGray,
                            modifier = Modifier.size(42.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Ainda não existem inscrições neste torneio.",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SportFlowDarkBlue
                        )
                    }
                }

                else -> {
                    uiState.selectedTournamentEnrollments.forEach { enrollment ->
                        EnrollmentManagementCard(
                            enrollment = enrollment,
                            isUpdating = uiState.updatingEnrollmentId == enrollment.id,
                            onApprove = { onApproveEnrollment(enrollment.id) },
                            onReject = { onRejectEnrollment(enrollment.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EnrollmentManagementCard(
    enrollment: Enrollment,
    isUpdating: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF8FAFC))
            .border(0.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = enrollment.userName ?: "Atleta sem nome",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = SportFlowDarkBlue
                )
                Text(
                    text = enrollment.userEmail ?: enrollment.userId,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 15.sp
                )
            }

            EnrollmentStatusBadge(status = enrollment.status)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DetailChip(
                icon = Icons.Default.Payments,
                label = enrollment.paymentStatus
            )
            DetailChip(
                icon = Icons.Default.Schedule,
                label = enrollment.registeredAt?.formatShortDate() ?: "Data indisponível"
            )
        }

        if (enrollment.status.equals("PENDENTE", ignoreCase = true)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onReject,
                    enabled = !isUpdating,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFFCA5A5))
                ) {
                    Text(
                        text = "Rejeitar",
                        color = Color(0xFFB91C1C),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = onApprove,
                    enabled = !isUpdating,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SportFlowGreen)
                ) {
                    if (isUpdating) {
                        CircularProgressIndicator(
                            color = SportFlowDarkBlue,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Text(
                            text = "Aprovar",
                            color = SportFlowDarkBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorCard(
    message: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(0.5.dp, Color(0xFFFCA5A5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                fontSize = 12.sp,
                color = Color(0xFFDC2626)
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(onClick = onRetry) {
                Text("Tentar novamente")
            }
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = Color(0xFFDC2626),
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Erro ao carregar eventos",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = SportFlowDarkBlue
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            fontSize = 13.sp,
            color = Color(0xFF64748B)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = SportFlowGreen)
        ) {
            Text("Tentar novamente")
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Assignment,
            contentDescription = null,
            tint = SportFlowTextGray,
            modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Nenhum evento criado",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = SportFlowDarkBlue
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Cria um evento para ele aparecer aqui.",
            fontSize = 13.sp,
            color = Color(0xFF64748B)
        )
    }
}

@Composable
private fun EnrollmentStatusBadge(status: String) {
    val (label, bg, tc) = when (status.uppercase(Locale.ROOT)) {
        "APROVADA" -> Triple("APROVADA", Color(0xFFD1FAE5), Color(0xFF047857))
        "REJEITADA" -> Triple("REJEITADA", Color(0xFFFEE2E2), Color(0xFFB91C1C))
        else -> Triple("PENDENTE", Color(0xFFDBEAFE), Color(0xFF1D4ED8))
    }

    Text(
        text = label,
        fontSize = 9.sp,
        fontWeight = FontWeight.Black,
        color = tc,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    )
}

@Composable
private fun DetailChip(
    icon: ImageVector,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .border(0.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 7.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF64748B),
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF475569)
        )
    }
}

@Composable
fun EventCard(
    imageRes: Int,
    statusText: String,
    statusColor: Color,
    statusBg: Color,
    title: String,
    date: String,
    location: String,
    buttonText: String,
    buttonBg: Color,
    buttonContentColor: Color,
    borderStroke: BorderStroke? = null,
    hasDot: Boolean = false,
    onButtonClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(0.5.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(statusBg)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    if (hasDot) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(statusColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = statusText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SportFlowDarkBlue
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Data",
                        tint = Color(0xFF047857),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = date,
                        fontSize = 12.sp,
                        color = Color(0xFF475569)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "Localização",
                        tint = Color(0xFF047857),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = location,
                        fontSize = 12.sp,
                        color = Color(0xFF475569)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                if (borderStroke != null) {
                    OutlinedButton(
                        onClick = onButtonClick,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = buttonBg,
                            contentColor = buttonContentColor
                        ),
                        border = borderStroke,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Text(
                            text = buttonText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Button(
                        onClick = onButtonClick,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = buttonBg,
                            contentColor = buttonContentColor
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Text(
                            text = buttonText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

private data class StatusStyle(
    val textColor: Color,
    val backgroundColor: Color
)

private fun String.toStatusStyle(): StatusStyle {
    return when (uppercase(Locale.ROOT)) {
        "ABERTO", "ATIVO" -> StatusStyle(
            textColor = Color(0xFF047857),
            backgroundColor = Color(0xFFD1FAE5)
        )
        "PLANEAMENTO", "EM_PLANEAMENTO", "RASCUNHO" -> StatusStyle(
            textColor = Color(0xFF1E40AF),
            backgroundColor = Color(0xFFDBEAFE)
        )
        "FECHADO", "TERMINADO", "CONCLUIDO", "CONCLUÍDO" -> StatusStyle(
            textColor = Color.White,
            backgroundColor = Color(0xFF334155)
        )
        "CANCELADO" -> StatusStyle(
            textColor = Color(0xFF991B1B),
            backgroundColor = Color(0xFFFEE2E2)
        )
        else -> StatusStyle(
            textColor = Color(0xFF475569),
            backgroundColor = Color(0xFFE2E8F0)
        )
    }
}

private fun String?.toImageRes(): Int {
    return when (this?.uppercase(Locale.ROOT)) {
        "SOCCER" -> R.drawable.futsal_stadium
        "BASKETBALL" -> R.drawable.silver_trophy
        "TENNIS" -> R.drawable.silver_trophy
        "PADEL" -> R.drawable.silver_trophy
        else -> R.drawable.silver_trophy
    }
}

private fun String.formatTournamentDate(): String {
    val date = runCatching {
        OffsetDateTime.parse(this).toLocalDate()
    }.getOrElse {
        runCatching { LocalDate.parse(this) }.getOrNull()
    }

    return date?.format(
        DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.forLanguageTag("pt-PT"))
    )?.uppercase(Locale.forLanguageTag("pt-PT")) ?: this
}

private fun String.formatShortDate(): String {
    val date = runCatching {
        OffsetDateTime.parse(this).toLocalDate()
    }.getOrElse {
        runCatching { LocalDate.parse(this) }.getOrNull()
    }

    return date?.format(
        DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.forLanguageTag("pt-PT"))
    ) ?: this
}

private fun String.initials(): String {
    val words = trim().split(Regex("\\s+")).filter { it.isNotBlank() }
    return words
        .take(2)
        .joinToString(separator = "") { it.first().uppercaseChar().toString() }
        .ifBlank { "EQ" }
}

@Preview(showBackground = true)
@Composable
fun OrganizadorEventsScreenPreview() {
    OrganizadorEventsScreen()
}
