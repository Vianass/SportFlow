package com.sportflow.app.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.sportflow.app.model.Enrollment
import com.sportflow.app.model.Tournament
import com.sportflow.app.ui.components.EnrollmentManagementSection
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Data model for Admin Tournaments
data class AdminTournament(
    val source: Tournament,
    val title: String,
    val subtitle: String,
    val category: String,
    val statusLabel: String, // INSCRIÇÕES ABERTAS, FINALIZADO, etc.
    val statusColor: Color,
    val sportType: String, // FUTEBOL, BASKETBALL, TENNIS
    val icon: ImageVector,
    val isCoverEnabled: Boolean = false,
    val details: List<String> = emptyList(), // e.g. ["ELIMINATÓRIAS", "16 EQUIPAS", "FINAL: 20 OUT"]
    val winnerText: String? = null,
    val hasAvatars: Boolean = false
)

// Data model for Calendar Matches in Admin
data class AdminCalendarMatch(
    val dateLabel: String,
    val timeLabel: String,
    val category: String,
    val details: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEventsScreen(
    viewModel: AdminViewModel = viewModel(),
    createFormRequest: Int = 0
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(0) } // 0 = TODOS, 1 = ATIVOS, 2 = CONCLUÍDOS
    var showFullCalendar by remember { mutableStateOf(false) }
    var selectedTournament by remember { mutableStateOf<Tournament?>(null) }
    var isEditingTournament by remember { mutableStateOf(false) }
    val state by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val tournaments = state.tournaments.map { tournament ->
        val finished = isFinishedStatus(tournament.status)
        AdminTournament(
            source = tournament,
            title = tournament.name,
            subtitle = listOfNotNull(tournament.location, tournament.startDate)
                .joinToString(" • "),
            category = tournament.category ?: tournament.sport ?: "SEM CATEGORIA",
            statusLabel = tournament.status,
            statusColor = if (finished) Color(0xFFEF4444) else Color(0xFF16A34A),
            sportType = tournament.sport.orEmpty(),
            icon = adminEventSportIcon(tournament.sport),
            details = listOfNotNull(
                tournament.category,
                tournament.maxCapacity?.let { "$it PARTICIPANTES" },
                tournament.startDate
            )
        )
    }

    val tournamentsById = state.tournaments.associateBy { it.id }
    val sortedGames = state.games.sortedBy { it.dateTime }
    val calendarMatches = (if (showFullCalendar) sortedGames else sortedGames.take(10)).map { game ->
        val homeTeam = game.homeTeamId?.let { state.teamNames[it] ?: "Equipa #$it" }
            ?: "Equipa por definir"
        val awayTeam = game.awayTeamId?.let { state.teamNames[it] ?: "Equipa #$it" }
            ?: "Equipa por definir"
        AdminCalendarMatch(
            dateLabel = game.dateTime.substringBefore("T").takeLast(5),
            timeLabel = game.dateTime.substringAfter("T", game.dateTime).take(5),
            category = game.tournamentId?.let { tournamentsById[it]?.name }.orEmpty(),
            details = "$homeTeam vs $awayTeam"
        )
    }

    // Dynamic filtering
    val filteredTournaments = remember(selectedFilter, tournaments, searchQuery) {
        when (selectedFilter) {
            1 -> tournaments.filterNot {
                isFinishedStatus(it.statusLabel)
            }
            2 -> tournaments.filter {
                isFinishedStatus(it.statusLabel)
            }
            else -> tournaments
        }.filter {
            searchQuery.isBlank() ||
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true) ||
                it.sportType.contains(searchQuery, ignoreCase = true)
        }
    }

    val formItemIndex = 4 + filteredTournaments.size

    LaunchedEffect(createFormRequest) {
        if (createFormRequest > 0) {
            withFrameNanos { }
            listState.animateScrollToItem(formItemIndex)
        }
    }

    LaunchedEffect(state.tournaments, selectedTournament?.id) {
        val selectedId = selectedTournament?.id ?: return@LaunchedEffect
        state.tournaments.firstOrNull { it.id == selectedId }?.let {
            selectedTournament = it
        }
    }

    LaunchedEffect(state.successMessage) {
        if (state.successMessage == "Torneio atualizado com sucesso.") {
            isEditingTournament = false
            delay(3000)
            viewModel.clearSuccess()
        }
    }

    LaunchedEffect(selectedTournament?.id, state.currentAdminId) {
        val tournament = selectedTournament ?: return@LaunchedEffect
        if (tournament.organizerId == state.currentAdminId) {
            viewModel.loadEnrollmentsForTournament(tournament.id)
        } else {
            viewModel.clearEnrollmentSelection()
        }
    }

    selectedTournament?.let { tournament ->
        AdminTournamentDetailScreen(
            tournament = tournament,
            isEditing = isEditingTournament,
            isSubmitting = state.operationInProgress == "updateTournament:${tournament.id}",
            errorMessage = state.errorMessage,
            successMessage = state.successMessage,
            canManageEnrollments = tournament.organizerId == state.currentAdminId,
            enrollments = state.selectedTournamentEnrollments,
            isLoadingEnrollments = state.isLoadingEnrollments,
            enrollmentsErrorMessage = state.enrollmentsErrorMessage,
            enrollmentSuccessMessage = state.enrollmentSuccessMessage,
            updatingEnrollmentId = state.updatingEnrollmentId,
            onBack = {
                isEditingTournament = false
                selectedTournament = null
                viewModel.clearEnrollmentSelection()
                viewModel.clearError()
                viewModel.clearSuccess()
            },
            onEdit = { isEditingTournament = true },
            onCancelEdit = {
                isEditingTournament = false
                viewModel.clearError()
                viewModel.clearSuccess()
            },
            onClearMessages = {
                viewModel.clearError()
                viewModel.clearSuccess()
            },
            onRetryEnrollments = { viewModel.loadEnrollmentsForTournament(tournament.id) },
            onApproveEnrollment = viewModel::approveEnrollment,
            onRejectEnrollment = viewModel::rejectEnrollment,
            onSubmit = { name, sport, category, date, location, capacity, price, status ->
                viewModel.updateTournament(
                    tournamentId = tournament.id,
                    name = name,
                    sport = sport,
                    category = category,
                    date = date,
                    location = location,
                    capacity = capacity,
                    price = price,
                    status = status
                )
            }
        )
        return
    }

    LazyColumn(
        state = listState,
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
                    text = "GESTÃO DE COMPETIÇÕES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF16A34A),
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "MEUS TORNEIOS",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = SportFlowDarkBlue
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Gira os seus campeonatos, acompanhe resultados e organize chaves de forma profissional.",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 18.sp
                )
            }
        }

        // 2. Primary "+ CRIAR TORNEIO" Action Button
        item {
            Button(
                onClick = {
                    scope.launch { listState.animateScrollToItem(formItemIndex) }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 4.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SportFlowGreen)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircleOutline,
                        contentDescription = null,
                        tint = SportFlowDarkBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CRIAR TORNEIO",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = SportFlowDarkBlue,
                        letterSpacing = 0.5.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. Search and Filters
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                // Search field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { 
                        Text(
                            "Pesquisar por nome, modalidade ou tipo...", 
                            fontSize = 13.sp, 
                            color = Color(0xFF94A3B8)
                        ) 
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Pesquisar",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color(0xFFEFF6FF),
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Segmented pills
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val filters = listOf("TODOS", "ATIVOS", "CONCLUÍDOS")
                    filters.forEachIndexed { index, label ->
                        val isSelected = selectedFilter == index
                        val bg = if (isSelected) SportFlowDarkBlue else Color(0xFFEFF6FF)
                        val tc = if (isSelected) Color.White else Color(0xFF475569)

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(bg)
                                .clickable { selectedFilter = index },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = tc,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 4. Tournaments list
        items(filteredTournaments) { tournament ->
            AdminTournamentCard(
                tournament = tournament,
                onClick = { selectedTournament = tournament.source }
            )
        }

        // 5. Match Calendar Card ("CALENDÁRIO DE JOGOS")
        item {
            Spacer(modifier = Modifier.height(16.dp))
            MatchCalendarCard(
                matches = calendarMatches,
                showFullCalendar = showFullCalendar,
                onToggleFullCalendar = { showFullCalendar = !showFullCalendar }
            )
        }

        // 6. New Competition Form Card ("NOVA COMPETIÇÃO")
        item {
            Spacer(modifier = Modifier.height(20.dp))
            NewCompetitionFormCard(
                initialTournament = null,
                isSubmitting = state.operationInProgress == "createTournament",
                errorMessage = state.errorMessage,
                successMessage = state.successMessage,
                onClearMessages = {
                    viewModel.clearError()
                    viewModel.clearSuccess()
                },
                onSubmit = { name, sport, category, date, location, capacity, price, _ ->
                    viewModel.createTournament(name, sport, category, date, location, capacity, price)
                }
            )
        }
    }
}

@Composable
fun AdminTournamentCard(
    tournament: AdminTournament,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 10.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Optional Cover Header Area (For Copa Elite Champions)
            if (tournament.isCoverEnabled) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF0F172A),
                                    Color(0xFF1E293B)
                                )
                            )
                        )
                ) {
                    // Stadium pattern indicator in the cover
                    Icon(
                        imageVector = tournament.icon,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.08f),
                        modifier = Modifier
                            .size(110.dp)
                            .align(Alignment.Center)
                    )
                }
            }

            // Tournament Information Content Block
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header tags row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFEFF6FF))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = tournament.category,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E40AF)
                        )
                    }

                    // Status label or Options three dots
                    if (tournament.statusLabel.isNotEmpty()) {
                        Text(
                            text = tournament.statusLabel,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = tournament.statusColor,
                            letterSpacing = 0.5.sp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.MoreHoriz,
                            contentDescription = "Mais opções",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Title
                Text(
                    text = tournament.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SportFlowDarkBlue
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Subtitle/Description
                Text(
                    text = tournament.subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Conditionally render bottom rows based on type
                if (tournament.isCoverEnabled && tournament.details.isNotEmpty()) {
                    // Card 1 Details Row (Soccer Copa Elite)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TournamentDetailItem(icon = Icons.Default.EmojiEvents, label = tournament.details[0])
                        TournamentDetailItem(icon = Icons.Default.People, label = tournament.details[1])
                        TournamentDetailItem(icon = Icons.Default.DateRange, label = tournament.details[2])
                    }
                } else if (tournament.hasAvatars) {
                    // Card 2 Details Row (Basketball Liga Street)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Stack visual de participantes.
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy((-8).dp)
                            ) {
                                repeat(3) { i ->
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.radialGradient(
                                                    colors = listOf(
                                                        Color(0xFF93C5FD),
                                                        Color(0xFF2563EB)
                                                    )
                                                )
                                            )
                                            .border(1.dp, Color.White, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = Color.White.copy(alpha = 0.8f),
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "+9",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B)
                            )
                        }

                        // Ver chaves button
                        Text(
                            text = "VER CHAVES →",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF16A34A),
                            modifier = Modifier.clickable { /* View keys */ }
                        )
                    }
                } else if (tournament.winnerText != null) {
                    // Card 3 Details Row (Tennis Open)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = Color(0xFF16A34A),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = tournament.winnerText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF16A34A)
                            )
                        }

                        // Relatórios link
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { /* View reports */ }
                        ) {
                            Text(
                                text = "RELATÓRIOS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF16A34A)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = Color(0xFF16A34A),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminTournamentDetailScreen(
    tournament: Tournament,
    isEditing: Boolean,
    isSubmitting: Boolean,
    errorMessage: String?,
    successMessage: String?,
    canManageEnrollments: Boolean,
    enrollments: List<Enrollment>,
    isLoadingEnrollments: Boolean,
    enrollmentsErrorMessage: String?,
    enrollmentSuccessMessage: String?,
    updatingEnrollmentId: Long?,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onCancelEdit: () -> Unit,
    onClearMessages: () -> Unit,
    onRetryEnrollments: () -> Unit,
    onApproveEnrollment: (Long) -> Unit,
    onRejectEnrollment: (Long) -> Unit,
    onSubmit: (String, String, String, String, String, String, String, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "DETALHE DO TORNEIO",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportFlowGreen
                    )
                    Text(
                        text = tournament.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = SportFlowDarkBlue
                    )
                }
            }
        }

        if (isEditing) {
            item {
                NewCompetitionFormCard(
                    initialTournament = tournament,
                    isSubmitting = isSubmitting,
                    errorMessage = errorMessage,
                    successMessage = successMessage,
                    onClearMessages = onClearMessages,
                    onSubmit = onSubmit
                )
            }
            item {
                OutlinedButton(
                    onClick = onCancelEdit,
                    enabled = !isSubmitting,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("CANCELAR EDIÇÃO")
                }
            }
        } else item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    AdminTournamentDetailRow("Estado", tournament.status)
                    AdminTournamentDetailRow("Modalidade", tournament.sport ?: "Não definida")
                    AdminTournamentDetailRow("Categoria", tournament.category ?: "Não definida")
                    AdminTournamentDetailRow("Data de início", tournament.startDate)
                    AdminTournamentDetailRow("Localização", tournament.location ?: "Não definida")
                    AdminTournamentDetailRow(
                        "Capacidade máxima",
                        tournament.maxCapacity?.toString() ?: "Não definida"
                    )
                    AdminTournamentDetailRow(
                        "Preço",
                        tournament.price?.let { "%.2f €".format(it) } ?: "Não definido"
                    )
                    AdminTournamentDetailRow(
                        "Organizador",
                        tournament.organizerId ?: "Não atribuído"
                    )
                }
            }
        }

        if (!isEditing) {
            item {
                Button(
                    onClick = onEdit,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SportFlowGreen)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = SportFlowDarkBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("EDITAR TORNEIO", color = SportFlowDarkBlue, fontWeight = FontWeight.Black)
                }
            }

            if (canManageEnrollments) {
                item {
                    EnrollmentManagementSection(
                        enrollments = enrollments,
                        isLoading = isLoadingEnrollments,
                        errorMessage = enrollmentsErrorMessage,
                        successMessage = enrollmentSuccessMessage,
                        updatingEnrollmentId = updatingEnrollmentId,
                        onRetry = onRetryEnrollments,
                        onApproveEnrollment = onApproveEnrollment,
                        onRejectEnrollment = onRejectEnrollment
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminTournamentDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(0.42f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF64748B)
        )
        Text(
            text = value,
            modifier = Modifier.weight(0.58f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = SportFlowDarkBlue
        )
    }
}

@Composable
fun TournamentDetailItem(icon: ImageVector, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF16A34A),
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF15803D)
        )
    }
}

@Composable
fun MatchCalendarCard(
    matches: List<AdminCalendarMatch>,
    showFullCalendar: Boolean,
    onToggleFullCalendar: () -> Unit
) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CALENDÁRIO DE JOGOS",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.15f),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            matches.forEachIndexed { index, match ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Date & Time block
                    Column(modifier = Modifier.width(60.dp)) {
                        Text(
                            text = match.dateLabel,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = SportFlowGreen
                        )
                        Text(
                            text = match.timeLabel,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Match Category & Title
                    Column {
                        Text(
                            text = match.category,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                        Text(
                            text = match.details,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }

                if (index < matches.size - 1) {
                    Divider(color = Color.White.copy(alpha = 0.08f), thickness = 0.5.dp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // VER CALENDÁRIO COMPLETO button
            Button(
                onClick = onToggleFullCalendar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.06f)),
                border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.15f))
            ) {
                Text(
                    text = if (showFullCalendar) "MOSTRAR MENOS" else "VER CALENDÁRIO COMPLETO",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCompetitionFormCard(
    initialTournament: Tournament?,
    isSubmitting: Boolean,
    errorMessage: String?,
    successMessage: String?,
    onClearMessages: () -> Unit,
    onSubmit: (String, String, String, String, String, String, String, String) -> Unit
) {
    val isEditing = initialTournament != null
    var eventName by remember(initialTournament?.id) { mutableStateOf(initialTournament?.name.orEmpty()) }
    var selectedSport by remember(initialTournament?.id) { mutableStateOf(initialTournament?.sport ?: "Futebol") }
    var selectedType by remember(initialTournament?.id) { mutableStateOf(initialTournament?.category ?: "Eliminatórias") }
    var eventDate by remember(initialTournament?.id) {
        mutableStateOf(initialTournament?.startDate?.substringBefore("T").orEmpty())
    }
    var location by remember(initialTournament?.id) { mutableStateOf(initialTournament?.location.orEmpty()) }
    var capacity by remember(initialTournament?.id) { mutableStateOf(initialTournament?.maxCapacity?.toString().orEmpty()) }
    var price by remember(initialTournament?.id) { mutableStateOf(initialTournament?.price?.toString().orEmpty()) }
    var selectedStatus by remember(initialTournament?.id) { mutableStateOf(initialTournament?.status ?: "ABERTO") }
    var sportExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(successMessage) {
        if (successMessage != null && !isEditing) {
            eventName = ""
            eventDate = ""
            location = ""
            capacity = ""
            price = ""
            delay(3000)
            onClearMessages()
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = if (isEditing) 0.dp else 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF).copy(alpha = 0.7f)),
        border = BorderStroke(0.5.dp, Color(0xFFDBEAFE))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Text(
                text = if (isEditing) "EDITAR TORNEIO" else "NOVA COMPETIÇÃO",
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = SportFlowDarkBlue,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Field 1: Nome do Evento
            Text(
                text = "NOME DO EVENTO",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = eventName,
                onValueChange = {
                    eventName = it
                    onClearMessages()
                },
                placeholder = { 
                    Text(
                        "Ex: Torneio de Primavera", 
                        fontSize = 13.sp, 
                        color = Color(0xFF94A3B8)
                    ) 
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF3B82F6),
                    unfocusedBorderColor = Color(0xFFE2E8F0)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Field 2: Dropdowns Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Dropdown 1: Modalidade
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "MODALIDADE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    ExposedDropdownMenuBox(
                        expanded = sportExpanded,
                        onExpandedChange = { sportExpanded = !sportExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedSport,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(sportExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(10.dp),
                            colors = adminFormFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = sportExpanded,
                            onDismissRequest = { sportExpanded = false }
                        ) {
                            listOf("Futebol", "Basquetebol", "Ténis").forEach { sport ->
                                DropdownMenuItem(
                                    text = { Text(sport) },
                                    onClick = {
                                        selectedSport = sport
                                        sportExpanded = false
                                        onClearMessages()
                                    }
                                )
                            }
                        }
                    }
                }

                // Dropdown 2: Tipo
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "TIPO",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    ExposedDropdownMenuBox(
                        expanded = typeExpanded,
                        onExpandedChange = { typeExpanded = !typeExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedType,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(typeExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(10.dp),
                            colors = adminFormFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = typeExpanded,
                            onDismissRequest = { typeExpanded = false }
                        ) {
                            listOf("Eliminatórias", "Liga", "Grupos").forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type) },
                                    onClick = {
                                        selectedType = type
                                        typeExpanded = false
                                        onClearMessages()
                                    }
                                )
                            }
                        }
                    }
                }
            }

            if (isEditing) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "ESTADO",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.height(4.dp))
                ExposedDropdownMenuBox(
                    expanded = statusExpanded,
                    onExpandedChange = { statusExpanded = !statusExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedStatus,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(statusExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(10.dp),
                        colors = adminFormFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false }
                    ) {
                        listOf("ABERTO", "ATIVO", "CONCLUIDO", "FINALIZADO").forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status) },
                                onClick = {
                                    selectedStatus = status
                                    statusExpanded = false
                                    onClearMessages()
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            AdminCompetitionTextField("DATA DO EVENTO", eventDate, "AAAA-MM-DD") {
                eventDate = it
                onClearMessages()
            }
            Spacer(modifier = Modifier.height(12.dp))
            AdminCompetitionTextField("LOCALIZAÇÃO", location, "Ex: Pavilhão Municipal") {
                location = it
                onClearMessages()
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AdminCompetitionTextField(
                    label = "CAPACIDADE",
                    value = capacity,
                    placeholder = "16",
                    modifier = Modifier.weight(1f)
                ) {
                    capacity = it
                    onClearMessages()
                }
                AdminCompetitionTextField(
                    label = "PREÇO",
                    value = price,
                    placeholder = "0.00",
                    modifier = Modifier.weight(1f)
                ) {
                    price = it
                    onClearMessages()
                }
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(10.dp))
                Text(it, color = Color(0xFFDC2626), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            successMessage?.let {
                Spacer(modifier = Modifier.height(10.dp))
                Text(it, color = Color(0xFF15803D), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action button: Continuar Configuração
            Button(
                onClick = {
                    onSubmit(
                        eventName,
                        selectedSport,
                        selectedType,
                        eventDate,
                        location,
                        capacity,
                        price,
                        selectedStatus
                    )
                },
                enabled = !isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SportFlowDarkBlue)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
                } else {
                    Text(
                        text = if (isEditing) "GUARDAR ALTERAÇÕES" else "CRIAR TORNEIO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminCompetitionTextField(
    label: String,
    value: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    Column(modifier = modifier) {
        Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, fontSize = 12.sp, color = Color(0xFF94A3B8)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = adminFormFieldColors()
        )
    }
}

@Composable
private fun adminFormFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    focusedBorderColor = Color(0xFF3B82F6),
    unfocusedBorderColor = Color(0xFFE2E8F0)
)

private fun isFinishedStatus(status: String): Boolean {
    val normalized = status.uppercase()
        .replace('Í', 'I')
        .replace('Á', 'A')
    return normalized == "FINALIZADO" || normalized == "CONCLUIDO" || normalized == "TERMINADO"
}

private fun adminEventSportIcon(sport: String?): ImageVector = when (sport?.uppercase()) {
    "BASQUETEBOL", "BASKETBALL" -> Icons.Default.SportsBasketball
    "TÉNIS", "TENIS", "TENNIS" -> Icons.Default.SportsTennis
    else -> Icons.Default.SportsFootball
}

@Preview(showBackground = true)
@Composable
fun AdminEventsScreenPreview() {
    AdminEventsScreen()
}
