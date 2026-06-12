package com.sportflow.app.ui.screens.admin

import com.sportflow.app.ui.localization.localizedText

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
import com.sportflow.app.ui.localization.Text
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
import com.sportflow.app.ui.theme.SportFlowTextGray

// Data model for Admin Tournaments
data class AdminTournament(
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
fun AdminEventsScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(0) } // 0 = TODOS, 1 = ATIVOS, 2 = CONCLUÍDOS

    val tournaments = remember {
        listOf(
            AdminTournament(
                title = "COPA ELITE CHAMPIONS 2024",
                subtitle = "A maior competição regional de futebol society, reunindo as 16 melhores equipas...",
                category = "FUTEBOL",
                statusLabel = "",
                statusColor = Color.Transparent,
                sportType = "FUTEBOL",
                icon = Icons.Default.SportsFootball,
                isCoverEnabled = true,
                details = listOf("ELIMINATÓRIAS", "16 EQUIPAS", "FINAL: 20 OUT")
            ),
            AdminTournament(
                title = "LIGA STREET BASKET",
                subtitle = "Torneio 3x3 modalidade livre para atletas federados.",
                category = "BASQUETEBOL",
                statusLabel = "INSCRIÇÕES ABERTAS",
                statusColor = Color(0xFF64748B),
                sportType = "BASKETBALL",
                icon = Icons.Default.SportsBasketball,
                isCoverEnabled = false,
                hasAvatars = true
            ),
            AdminTournament(
                title = "OPEN TÉNIS PRAIA",
                subtitle = "Circuito de verão com premiação recorde em 2024.",
                category = "TÉNIS",
                statusLabel = "FINALIZADO",
                statusColor = Color(0xFFEF4444),
                sportType = "TENNIS",
                icon = Icons.Default.SportsTennis,
                isCoverEnabled = false,
                winnerText = "VENCEDOR: G. SANTOS"
            )
        )
    }

    val calendarMatches = remember {
        listOf(
            AdminCalendarMatch(dateLabel = "HOJE", timeLabel = "19:30", category = "COPA ELITE", details = "Fénix FC vs Dragões"),
            AdminCalendarMatch(dateLabel = "AMANHÃ", timeLabel = "10:00", category = "LIGA STREET", details = "Titãs 3x3 vs Mavericks"),
            AdminCalendarMatch(dateLabel = "22 OUT", timeLabel = "14:00", category = "COPA ELITE", details = "Quartos-de-Final #3")
        )
    }

    // Dynamic filtering
    val filteredTournaments = remember(selectedFilter, tournaments) {
        when (selectedFilter) {
            1 -> tournaments.filter { it.statusLabel != "FINALIZADO" }
            2 -> tournaments.filter { it.statusLabel == "FINALIZADO" }
            else -> tournaments
        }
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
                onClick = { /* Create a tournament */ },
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
                            contentDescription = localizedText("Pesquisar"),
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
            AdminTournamentCard(tournament = tournament)
        }

        // 5. Match Calendar Card ("CALENDÁRIO DE JOGOS")
        item {
            Spacer(modifier = Modifier.height(16.dp))
            MatchCalendarCard(matches = calendarMatches)
        }

        // 6. New Competition Form Card ("NOVA COMPETIÇÃO")
        item {
            Spacer(modifier = Modifier.height(20.dp))
            NewCompetitionFormCard()
        }
    }
}

@Composable
fun AdminTournamentCard(tournament: AdminTournament) {
    Card(
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
                            contentDescription = localizedText("Mais opções"),
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
                        // Stack of Avatars mock
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
fun MatchCalendarCard(matches: List<AdminCalendarMatch>) {
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
                onClick = { /* Full calendar */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.06f)),
                border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.15f))
            ) {
                Text(
                    text = "VER CALENDÁRIO COMPLETO",
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
fun NewCompetitionFormCard() {
    var eventName by remember { mutableStateOf("") }
    var selectedSport by remember { mutableStateOf("Futebol") }
    var selectedType by remember { mutableStateOf("Eliminatórias") }

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
            Text(
                text = "NOVA COMPETIÇÃO",
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
                onValueChange = { eventName = it },
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .border(0.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                            .clickable { /* Dropdown modalidade */ }
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = selectedSport, fontSize = 12.sp, color = SportFlowDarkBlue, fontWeight = FontWeight.Medium)
                            Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .border(0.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                            .clickable { /* Dropdown tipo */ }
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = selectedType, fontSize = 12.sp, color = SportFlowDarkBlue, fontWeight = FontWeight.Medium)
                            Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action button: Continuar Configuração
            Button(
                onClick = { /* Continue config */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SportFlowDarkBlue)
            ) {
                Text(
                    text = "CONTINUAR CONFIGURAÇÃO",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminEventsScreenPreview() {
    AdminEventsScreen()
}
