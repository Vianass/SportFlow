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

// Data model for Tournament Events
data class TournamentEvent(
    val category: String,
    val title: String,
    val date: String,
    val location: String,
    val vacanciesLeft: Int,
    val isSoldOut: Boolean,
    val sportType: String, // BASKETBALL, PADEL, SOCCER, TENNIS
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserEventsScreen() {
    var searchQuery by remember { mutableStateOf("") }

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
                icon = Icons.Default.SportsBasketball
            ),
            TournamentEvent(
                category = "PADEL • OPEN MISTO",
                title = "Porto Padel Challenge",
                date = "22 MAIO, 2024",
                location = "Clube Padel Norte",
                vacanciesLeft = 4,
                isSoldOut = false,
                sportType = "PADEL",
                icon = Icons.Default.SportsTennis
            ),
            TournamentEvent(
                category = "FUTEBOL 7 • SÉRIE B",
                title = "Taça dos Campeões",
                date = "05 JUNHO, 2024",
                location = "Estádio Universitário",
                vacanciesLeft = 0,
                isSoldOut = true,
                sportType = "SOCCER",
                icon = Icons.Default.SportsFootball
            ),
            TournamentEvent(
                category = "TÉNIS • SINGULARES",
                title = "Algarve Tennis Open",
                date = "12 JUNHO, 2024",
                location = "Vilamoura Academy",
                vacanciesLeft = 8,
                isSoldOut = false,
                sportType = "TENNIS",
                icon = Icons.Default.SportsTennis
            )
        )
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

                Button(
                    onClick = { /* Handle Filter trigger */ },
                    modifier = Modifier
                        .fillMaxWidth()
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
            }
        }

        // Tournament Cards list
        items(tournaments) { tournament ->
            TournamentEventCard(tournament = tournament)
        }
    }
}

@Composable
fun TournamentEventCard(tournament: TournamentEvent) {
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
                        onClick = { /* Handle subscription trigger */ },
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
