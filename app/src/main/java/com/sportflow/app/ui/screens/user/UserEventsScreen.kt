package com.sportflow.app.ui.screens.user

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.ChevronRight
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
import com.sportflow.app.ui.theme.SportFlowDarkBlue
import com.sportflow.app.ui.theme.SportFlowGreen
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border

// Data model for Tournament Events
data class TournamentEvent(
    val category: String,
    val title: String,
    val date: String,
    val location: String,
    val vacanciesLeft: Int,
    val isSoldOut: Boolean,
    val sportType: String, // BASKETBALL, PADEL, SOCCER, TENNIS
    val icon: ImageVector,
    val localDate: LocalDate
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserEventsScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedSportFilter by remember { mutableStateOf("Todas") }
    var selectedAvailabilityFilter by remember { mutableStateOf("Todas") }
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedDateFilter by remember { mutableStateOf<LocalDate?>(null) }
    var showCalendarDialog by remember { mutableStateOf(false) }

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
    var selectedTournament by remember { mutableStateOf<TournamentEvent?>(null) }
    var showPaymentDialogFor by remember { mutableStateOf<TournamentEvent?>(null) }

    if (selectedTournament != null) {
        com.sportflow.app.ui.components.TournamentEnrollDialog(
            tournament = selectedTournament!!,
            onEnroll = { 
                showPaymentDialogFor = selectedTournament
                selectedTournament = null
            },
            onDismiss = { selectedTournament = null }
        )
    }

    if (showPaymentDialogFor != null) {
        com.sportflow.app.ui.components.PaymentDialog(
            currentLanguage = com.sportflow.app.model.AppLanguage.PT,
            isCheckout = true,
            onPaymentSuccess = {
                // Here we would typically add it to the user's subscriptions
            },
            onDismiss = { showPaymentDialogFor = null }
        )
    }

    // Mock data for Tournament Events (matching the mockup exactly)
    val tournaments = remember {
        listOf(
            TournamentEvent(
                category = "BASQUETEBOL • LIGA PRO",
                title = "Master Cup Lisboa 2024",
                date = "15 MAIO, 2024",
                location = "Arena 2, Lisboa",
                vacanciesLeft = 12,
                isSoldOut = false,
                sportType = "BASKETBALL",
                icon = Icons.Default.SportsBasketball,
                localDate = LocalDate.of(2024, 5, 15)
            ),
            TournamentEvent(
                category = "PADEL • OPEN MISTO",
                title = "Porto Padel Challenge",
                date = "22 MAIO, 2024",
                location = "Clube Padel Norte",
                vacanciesLeft = 4,
                isSoldOut = false,
                sportType = "PADEL",
                icon = Icons.Default.SportsTennis,
                localDate = LocalDate.of(2024, 5, 22)
            ),
            TournamentEvent(
                category = "FUTEBOL 7 • SÉRIE B",
                title = "Taça dos Campeões",
                date = "05 JUNHO, 2024",
                location = "Estádio Universitário",
                vacanciesLeft = 0,
                isSoldOut = true,
                sportType = "SOCCER",
                icon = Icons.Default.SportsFootball,
                localDate = LocalDate.of(2024, 6, 5)
            ),
            TournamentEvent(
                category = "TÉNIS • SINGULARES",
                title = "Algarve Tennis Open",
                date = "12 JUNHO, 2024",
                location = "Vilamoura Academy",
                vacanciesLeft = 8,
                isSoldOut = false,
                sportType = "TENNIS",
                icon = Icons.Default.SportsTennis,
                localDate = LocalDate.of(2024, 6, 12)
            )
        )
    }

    val filteredTournaments = remember(searchQuery, selectedSportFilter, selectedAvailabilityFilter, selectedDateFilter, tournaments) {
        tournaments.filter { tournament ->
            val matchesSearch = if (searchQuery.isBlank()) true else {
                tournament.title.contains(searchQuery, ignoreCase = true) ||
                tournament.category.contains(searchQuery, ignoreCase = true) ||
                tournament.location.contains(searchQuery, ignoreCase = true)
            }
            val matchesSport = if (selectedSportFilter == "Todas") true else {
                when (selectedSportFilter) {
                    "Basquetebol" -> tournament.sportType == "BASKETBALL"
                    "Padel" -> tournament.sportType == "PADEL"
                    "Futebol" -> tournament.sportType == "SOCCER"
                    "Ténis" -> tournament.sportType == "TENNIS"
                    else -> true
                }
            }
            val matchesAvailability = if (selectedAvailabilityFilter == "Todas") true else {
                if (selectedAvailabilityFilter == "Apenas com vagas") !tournament.isSoldOut else true
            }
            val matchesDate = if (selectedDateFilter == null) true else {
                tournament.localDate == selectedDateFilter
            }
            matchesSearch && matchesSport && matchesAvailability && matchesDate
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Hero Header Title Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Text(
                    text = "CALENDÁRIO NACIONAL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF16A34A),
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Próximos ",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = SportFlowDarkBlue
                    )
                    Text(
                        text = "Eventos",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = SportFlowGreen
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Explora os torneios e competições de elite. Garante o teu lugar nas maiores arenas desportivas do país.",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 18.sp
                )
            }
        }

        // Search and Filter Bar
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { 
                        Text(
                            "Procurar torneios ou modalidades...", 
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

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showFilterDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEFF6FF)),
                        border = BorderStroke(0.5.dp, Color(0xFFDBEAFE))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = null,
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "FILTROS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF2563EB),
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Button(
                        onClick = { showCalendarDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEFF6FF)),
                        border = BorderStroke(0.5.dp, Color(0xFFDBEAFE))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "CALENDÁRIO",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF2563EB),
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                if (selectedSportFilter != "Todas" || selectedAvailabilityFilter != "Todas" || selectedDateFilter != null || searchQuery.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Filtros ativos:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B)
                        )
                        
                        if (searchQuery.isNotBlank()) {
                            FilterBadge(
                                text = "\"$searchQuery\"",
                                onClear = { searchQuery = "" }
                            )
                        }
                        
                        if (selectedSportFilter != "Todas") {
                            FilterBadge(
                                text = selectedSportFilter,
                                onClear = { selectedSportFilter = "Todas" }
                            )
                        }

                        if (selectedAvailabilityFilter != "Todas") {
                            FilterBadge(
                                text = "Com vagas",
                                onClear = { selectedAvailabilityFilter = "Todas" }
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
        }

        if (filteredTournaments.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp, bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Nenhum torneio encontrado",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportFlowDarkBlue
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tente alterar os termos da pesquisa ou limpe os filtros ativos.",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            searchQuery = ""
                            selectedSportFilter = "Todas"
                            selectedAvailabilityFilter = "Todas"
                            selectedDateFilter = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEFF6FF)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Text(
                            text = "Limpar Todos os Filtros",
                            color = Color(0xFF2563EB),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            // Tournament Cards list
            items(filteredTournaments) { tournament ->
                TournamentEventCard(
                    tournament = tournament,
                    onEnrollClick = { selectedTournament = tournament }
                )
            }
        }
    }
}

@Composable
fun TournamentEventCard(
    tournament: TournamentEvent,
    onEnrollClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                val brush = remember {
                    when (tournament.sportType) {
                        "BASKETBALL" -> Brush.verticalGradient(listOf(Color(0xFFEA580C), Color(0xFFF97316)))
                        "PADEL" -> Brush.verticalGradient(listOf(Color(0xFF0D9488), Color(0xFF14B8A6)))
                        "SOCCER" -> Brush.verticalGradient(listOf(Color(0xFF334155), Color(0xFF475569)))
                        else -> Brush.verticalGradient(listOf(Color(0xFF0369A1), Color(0xFF0284C7)))
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
                        tint = Color.White.copy(alpha = 0.12f),
                        modifier = Modifier
                            .size(120.dp)
                            .align(Alignment.Center)
                    )
                }

                if (!tournament.isSoldOut) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${tournament.vacanciesLeft} VAGAS RESTANTES",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SportFlowGreen,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                if (tournament.isSoldOut) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFDC2626))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "INSCRIÇÕES ESGOTADAS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = tournament.category,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF16A34A),
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = tournament.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SportFlowDarkBlue
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = tournament.date,
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = tournament.location,
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (tournament.isSoldOut) {
                    Button(
                        onClick = { /* Disabled */ },
                        enabled = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            disabledContainerColor = Color(0xFFEFF6FF),
                            disabledContentColor = Color(0xFF94A3B8)
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "INDISPONÍVEL",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Block,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = onEnrollClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "INSCREVER AGORA",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserEventsScreenPreview() {
    UserEventsScreen()
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
