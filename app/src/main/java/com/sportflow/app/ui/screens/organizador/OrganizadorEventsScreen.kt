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
import com.sportflow.app.model.Enrollment
import com.sportflow.app.model.Team
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
            onCreateTeam = { teamName -> viewModel.createTeam(tournament.id, teamName) },
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
    onCreateTeam: (String) -> Unit,
    onApproveEnrollment: (Long) -> Unit,
    onRejectEnrollment: (Long) -> Unit
) {
    var showCreateTeamDialog by remember { mutableStateOf(false) }
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
                onCreateTeamClick = { showCreateTeamDialog = true }
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
    onCreateTeamClick: () -> Unit
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
            TeamRosterCard(team = team)
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
private fun TeamRosterCard(team: Team) {
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
                        text = "Jogadores ainda não associados",
                        fontSize = 10.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFFFF7ED))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "INCOMPLETO",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEA580C)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .border(
                        border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                        shape = RoundedCornerShape(8.dp)
                    ),
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
