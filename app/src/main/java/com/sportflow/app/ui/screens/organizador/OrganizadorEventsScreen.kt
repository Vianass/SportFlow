package com.sportflow.app.ui.screens.organizador

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sportflow.app.R
import com.sportflow.app.ui.theme.SportFlowDarkBlue
import com.sportflow.app.ui.theme.SportFlowGreen
import com.sportflow.app.ui.theme.SportFlowTextGray

@Composable
fun OrganizadorEventsScreen() {
    var currentScreen by remember { mutableStateOf("list") } // "list", "detail"

    if (currentScreen == "list") {
        EventsList(onManageEventClick = { currentScreen = "detail" })
    } else {
        OrganizadorEventDetailScreen(onBack = { currentScreen = "list" })
    }
}

@Composable
fun EventsList(onManageEventClick: () -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(top = 24.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Title Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "Gestão de\nInscrições",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 38.sp,
                    color = SportFlowDarkBlue
                )
                Spacer(modifier = Modifier.height(6.dp))
                // Green subline under "Gestão de"
                Box(
                    modifier = Modifier
                        .width(135.dp)
                        .height(4.dp)
                        .background(Color(0xFF16A34A))
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Seleciona um evento para gerir equipas e inscrições.",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 20.sp
                )
            }
        }

        // Event Card 1: Torneio: ESTG Futsal 2026 (Triggers Detail Screen)
        item {
            EventCard(
                imageRes = R.drawable.futsal_stadium,
                statusText = "ATIVO",
                statusColor = Color(0xFF047857),
                statusBg = Color(0xFFD1FAE5),
                title = "Torneio: ESTG Futsal 2026",
                date = "15 Out - 22 Out",
                location = "Viana do Castelo",
                buttonText = "Gerir Evento  →",
                buttonBg = Color(0xFF67FF9A),
                buttonContentColor = SportFlowDarkBlue,
                hasDot = true,
                onButtonClick = onManageEventClick
            )
        }

        // Event Card 2: Open Run Póvoa
        item {
            EventCard(
                imageRes = R.drawable.running_track,
                statusText = "EM PLANEAMENTO",
                statusColor = Color(0xFF1E40AF),
                statusBg = Color(0xFFDBEAFE),
                title = "Open Run Póvoa",
                date = "05 Mai 2026",
                location = "Lisboa",
                buttonText = "Gerir Planeamento  ⚙",
                buttonBg = Color(0xFFEFF6FF),
                buttonContentColor = SportFlowDarkBlue,
                hasDot = false,
                onButtonClick = {}
            )
        }

        // Event Card 3: Torneio de Verão
        item {
            EventCard(
                imageRes = R.drawable.silver_trophy,
                statusText = "CONCLUÍDO",
                statusColor = Color.White,
                statusBg = Color(0xFF334155),
                title = "Torneio de Verão",
                date = "Agosto 2025",
                location = "Porto",
                buttonText = "Ver Relatório  📄",
                buttonBg = Color.White,
                buttonContentColor = SportFlowDarkBlue,
                borderStroke = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                hasDot = false,
                onButtonClick = {}
            )
        }
    }
}

@Composable
fun OrganizadorEventDetailScreen(onBack: () -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(top = 24.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header with Back Button and Multi-line Title
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
                    text = "Torneio: ESTG\nFutsal 2026",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 34.sp,
                    color = SportFlowDarkBlue
                )
            }
        }

        // Card 1: Status Geral
        item {
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
                            text = "12/16",
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF67FF9A)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "EQUIPAS CONFIRMADAS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    // Green dot at top right
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF67FF9A))
                            .align(Alignment.TopEnd)
                    )

                    // Small bar chart at bottom right
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

        // Card 2: Jogadores Inscritos
        item {
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
                        text = "JOGADORES INSCRITOS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "120",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Black,
                        color = SportFlowDarkBlue
                    )
                }
            }
        }

        // Card 3: Localização & Nova Equipa
        item {
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
                    // Location info
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Campo ESTG",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = SportFlowDarkBlue
                        )
                        Text(
                            text = "VIANA DO CASTELO, PORTUGAL",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B)
                        )
                    }

                    // Nova Equipa neon green button
                    Button(
                        onClick = {},
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

        // Section Title: EQUIPAS E ELENCOS
        item {
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
        }

        // Card 4: Equipa Fiéis de Viana (Expanded state)
        item {
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
                    // Header row
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
                                text = "FV",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF047857)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Fiéis de Viana",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = SportFlowDarkBlue
                            )
                            Text(
                                text = "Capitão: Bernardo Fernandes",
                                fontSize = 10.sp,
                                color = Color(0xFF64748B)
                            )
                        }

                        // VALIDADO badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFD1FAE5))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "VALIDADO",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF065F46)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Player List
                    val players = listOf(
                        Pair("Pedro Carvalho", "Ponta"),
                        Pair("Lucas Maciel", "Defesa"),
                        Pair("Tiago Melo", "Guarda-Redes")
                    )

                    players.forEach { (name, pos) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SportFlowDarkBlue
                                )
                                Text(
                                    text = pos,
                                    fontSize = 10.sp,
                                    color = Color(0xFF64748B)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "ELITE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF047857)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remover",
                                    tint = Color(0xFFCBD5E1),
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clickable {}
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Dashed visual Card box "+ ASSOCIAR JOGADOR"
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .border(
                                border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {},
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+ ASSOCIAR JOGADOR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        }

        // Card 5: Equipa Black & White (Collapsed state)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
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
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFEFF6FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "BW",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF64748B)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Black & White",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = SportFlowDarkBlue
                        )
                        Text(
                            text = "A aguardar confirmação (4/7)",
                            fontSize = 10.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    // INCOMPLETO badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFEE2E2))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "INCOMPLETO",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF991B1B)
                        )
                    }
                }
            }
        }

        // Card 6: Registo de Partida
        item {
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
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Assignment,
                            contentDescription = null,
                            tint = Color(0xFF047857),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Registo de Partida",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SportFlowDarkBlue
                        )
                    }

                    // Dropdown inputs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Dropdown 1
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color.White, RoundedCornerShape(10.dp))
                                .border(0.5.dp, Color(0xFFCBD5E1), RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "100 pés",
                                    fontSize = 12.sp,
                                    color = SportFlowDarkBlue,
                                    fontWeight = FontWeight.Medium
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = Color(0xFF64748B)
                                )
                            }
                        }

                        // Dropdown 2
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color.White, RoundedCornerShape(10.dp))
                                .border(0.5.dp, Color(0xFFCBD5E1), RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Team Porto",
                                    fontSize = 12.sp,
                                    color = SportFlowDarkBlue,
                                    fontWeight = FontWeight.Medium
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = Color(0xFF64748B)
                                )
                            }
                        }
                    }

                    // Confirm button
                    Button(
                        onClick = {},
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SportFlowDarkBlue),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Text(
                            text = "CONFIRMAR AGENDAMENTO",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF67FF9A)
                        )
                    }
                }
            }
        }

        // Card 7: Top Performance
        item {
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
                        Text(
                            text = "TOP PERFORMANCE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8),
                            letterSpacing = 0.5.sp
                        )
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = Color(0xFF16A34A),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Player 1
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "André Brito",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SportFlowDarkBlue
                            )
                            Text(
                                text = "14 Golos",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF047857)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        // Progress bar simulating ~85%
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .background(Color(0xFFF1F5F9), RoundedCornerShape(2.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .fillMaxHeight()
                                    .background(Color(0xFF047857), RoundedCornerShape(2.dp))
                            )
                        }
                    }

                    // Player 2
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Mogo Viana",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SportFlowDarkBlue
                            )
                            Text(
                                text = "9 Golos",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF047857)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        // Progress bar simulating ~55%
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .background(Color(0xFFF1F5F9), RoundedCornerShape(2.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.55f)
                                    .fillMaxHeight()
                                    .background(Color(0xFF047857), RoundedCornerShape(2.dp))
                            )
                        }
                    }
                }
            }
        }
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
            // Image with Badge
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

                // Status Badge Overlay
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

            // Body
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

                // Date row
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

                // Location row
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

                // Action Button
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

@Preview(showBackground = true)
@Composable
fun OrganizadorEventsScreenPreview() {
    OrganizadorEventsScreen()
}
