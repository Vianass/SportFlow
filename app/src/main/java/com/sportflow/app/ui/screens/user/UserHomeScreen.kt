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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.sportflow.app.model.Game
import com.sportflow.app.ui.theme.SportFlowDarkBlue
import com.sportflow.app.ui.theme.SportFlowGreen
import com.sportflow.app.ui.theme.SportFlowTextGray
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

// Data model for Live Events
data class LiveEvent(
    val category: String,
    val title: String,
    val statusLabel: String,
    val statusValue: String,
    val progress: Float? = null,
    val icon: ImageVector,
    val isDarkTheme: Boolean = false,
    val type: String = "MATCH"
)

// Data model for Upcoming Events
data class UpcomingEvent(
    val day: String,
    val month: String,
    val title: String,
    val location: String,
    val time: String,
    val categoryBadge: String,
    val localDate: LocalDate
)

@Composable
fun UserHomeScreen(
    onNavigateToEvents: () -> Unit = {},
    viewModel: UserEventsViewModel = viewModel()
) {
    var selectedUpcomingEvent by remember { mutableStateOf<TournamentEvent?>(null) }

    var selectedSportFilter by remember { mutableStateOf("Todas") }
    var selectedAvailabilityFilter by remember { mutableStateOf("Todas") }
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedDateFilter by remember { mutableStateOf<LocalDate?>(null) }
    var showCalendarDialog by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()

    if (showFilterDialog) {
        com.sportflow.app.ui.components.EventsFilterDialog(
            initialSportFilter = selectedSportFilter,
            initialAvailabilityFilter = selectedAvailabilityFilter,
            onApplyFilters = { sport, availability ->
                selectedSportFilter = sport
                selectedAvailabilityFilter = availability
                showFilterDialog = false
            },
            onDismiss = { showFilterDialog = false }
        )
    }

    if (showCalendarDialog) {
        com.sportflow.app.ui.components.CalendarDialog(
            initialSelectedDate = selectedDateFilter,
            onDateSelected = { date ->
                selectedDateFilter = date
                showCalendarDialog = false
            },
            onDismiss = { showCalendarDialog = false }
        )
    }

    if (selectedUpcomingEvent != null) {
        com.sportflow.app.ui.components.TournamentEnrollDialog(
            tournament = selectedUpcomingEvent!!,
            onEnroll = { selectedUpcomingEvent = null },
            onDismiss = { selectedUpcomingEvent = null }
        )
    }

    val liveEvents = remember(uiState.liveGames) {
        uiState.liveGames.map { game -> game.toLiveEvent() }
    }

    val upcomingEvents = remember(uiState.tournaments) {
        uiState.tournaments
            .map { it.toTournamentEvent() }
            .filter { !it.isSoldOut }
            .sortedBy { it.localDate }
            .take(5)
    }

    val filteredUpcomingEvents = remember(selectedSportFilter, selectedDateFilter, upcomingEvents) {
        upcomingEvents.filter { event ->
            val matchesSport = if (selectedSportFilter == "Todas") true else {
                when (selectedSportFilter) {
                    "Basquetebol" -> event.sportType == "BASKETBALL"
                    "Padel" -> event.sportType == "PADEL"
                    "Futebol" -> event.sportType == "SOCCER"
                    "Ténis" -> event.sportType == "TENNIS"
                    else -> event.sportType.contains(selectedSportFilter, ignoreCase = true)
                }
            }
            val matchesDate = selectedDateFilter == null || event.localDate == selectedDateFilter
            matchesSport && matchesDate
        }
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
                onViewAllClick = onNavigateToEvents
            )
        }

        when {
            uiState.liveGamesErrorMessage != null -> {
                item {
                    LiveEventsErrorState(
                        message = uiState.liveGamesErrorMessage,
                        onRetry = viewModel::loadLiveGames
                    )
                }
            }

            uiState.isLoadingLiveGames -> {
                item { LiveEventsLoadingState() }
            }

            liveEvents.isEmpty() -> {
                item { LiveEventsEmptyState() }
            }

            else -> {
                items(liveEvents) { event ->
                    LiveEventCard(event = event)
                }
            }
        }

        // 3. Próximos Eventos (Upcoming Events) Section
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(
                title = "Próximos Eventos",
                isLive = false,
                onFilterClick = { showFilterDialog = true },
                onCalendarClick = { showCalendarDialog = true }
            )
        }

        if (selectedSportFilter != "Todas" || selectedDateFilter != null) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filtros ativos:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B)
                    )

                    if (selectedSportFilter != "Todas") {
                        FilterBadge(
                            text = selectedSportFilter,
                            onClear = { selectedSportFilter = "Todas" }
                        )
                    }

                    if (selectedDateFilter != null) {
                        val formattedDate = "${selectedDateFilter!!.dayOfMonth} ${selectedDateFilter!!.month.getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("pt")).uppercase()}"
                        FilterBadge(
                            text = formattedDate,
                            onClear = { selectedDateFilter = null }
                        )
                    }
                }
            }
        }

        if (filteredUpcomingEvents.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Nenhum evento encontrado",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportFlowDarkBlue
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tente alterar os filtros selecionados ou limpe os filtros ativos.",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            selectedSportFilter = "Todas"
                            selectedDateFilter = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEFF6FF)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(
                            text = "Limpar Filtros",
                            color = Color(0xFF2563EB),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            items(filteredUpcomingEvents) { event ->
                UpcomingEventCard(event = event, onEnrollClick = { selectedUpcomingEvent = event })
            }
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
    onViewAllClick: () -> Unit = {},
    onFilterClick: () -> Unit = {},
    onCalendarClick: () -> Unit = {}
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
                    onClick = onFilterClick,
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
                    onClick = onCalendarClick,
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
private fun FilterBadge(text: String, onClear: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFEFF6FF))
            .border(0.5.dp, Color(0xFFDBEAFE), RoundedCornerShape(8.dp))
            .clickable { onClear() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2563EB)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Limpar",
            tint = Color(0xFF2563EB),
            modifier = Modifier.size(12.dp)
        )
    }
}

@Composable
fun LiveEventCard(event: LiveEvent, onClick: () -> Unit = {}) {
    val cardBg = if (event.isDarkTheme) SportFlowDarkBlue else Color.White
    val textPrimary = if (event.isDarkTheme) Color.White else SportFlowDarkBlue
    val textSecondary = if (event.isDarkTheme) SportFlowGreen else Color(0xFF64748B)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
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

            event.progress?.let { progress ->
                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { progress },
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
}

@Composable
private fun LiveEventsLoadingState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = SportFlowGreen,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
private fun LiveEventsErrorState(
    message: String?,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFF1F2))
            .border(0.5.dp, Color(0xFFFECACA), RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = Color(0xFFDC2626),
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message ?: "Erro ao carregar eventos a decorrer.",
            fontSize = 12.sp,
            color = Color(0xFF991B1B),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextButton(onClick = onRetry) {
            Text(
                text = "Tentar novamente",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFDC2626)
            )
        }
    }
}

@Composable
private fun LiveEventsEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(0.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Event,
            contentDescription = null,
            tint = Color(0xFF94A3B8),
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Sem eventos a decorrer",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = SportFlowDarkBlue
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Quando o organizador iniciar um jogo, ele aparece aqui.",
            fontSize = 12.sp,
            color = Color(0xFF64748B),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

private fun Game.toLiveEvent(): LiveEvent {
    return LiveEvent(
        category = tournamentName?.uppercase(Locale.ROOT) ?: "JOGO EM DECORRER",
        title = "${homeTeamName ?: "Equipa casa"} vs ${awayTeamName ?: "Equipa fora"}",
        statusLabel = tournamentLocation ?: "Local a definir",
        statusValue = "Em curso",
        progress = null,
        icon = sport.toHomeSportIcon(),
        isDarkTheme = false
    )
}

private fun String?.toHomeSportIcon(): ImageVector {
    return when (this?.uppercase(Locale.ROOT)) {
        "SOCCER" -> Icons.Default.SportsSoccer
        "BASKETBALL" -> Icons.Default.SportsBasketball
        "TENNIS" -> Icons.Default.SportsTennis
        "PADEL" -> Icons.Default.SportsTennis
        else -> Icons.Default.EmojiEvents
    }
}

@Composable
fun UpcomingEventCard(event: TournamentEvent, onEnrollClick: () -> Unit = {}) {
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
                    text = event.localDate.month.getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("pt")).uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = event.localDate.dayOfMonth.toString(),
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
                        text = event.date,
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
                            text = event.sportType,
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
                            .clickable { onEnrollClick() },
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
