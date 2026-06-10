package com.sportflow.app.ui.screens.organizador

import com.sportflow.app.ui.components.SportFlowLoadingOverlay
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sportflow.app.ui.theme.SportFlowDarkBlue
import com.sportflow.app.ui.theme.SportFlowGreen
import com.sportflow.app.ui.theme.SportFlowTextGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen() {
    var eventName by remember { mutableStateOf("") }
    var regulation by remember { mutableStateOf("") }
    var selectedModalidade by remember { mutableStateOf("Padel") }
    var selectedNivel by remember { mutableStateOf("Amador") }
    var selectedFormato by remember { mutableStateOf("Eliminatórias") } // "Eliminatórias", "Grupos", "Liga"
    
    var modalidadeExpanded by remember { mutableStateOf(false) }
    var nivelExpanded by remember { mutableStateOf(false) }
    
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 20.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
        // 1. Screen Title
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "Criar Novo Evento",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = SportFlowDarkBlue
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Clean green subline matching figma
                Box(
                    modifier = Modifier
                        .width(76.dp)
                        .height(4.dp)
                        .background(Color(0xFF16A34A)) // Green underline
                )
            }
        }

        // 2. Detalhes do Torneio Card
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
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Detalhes do Torneio",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportFlowDarkBlue
                    )

                    // Event Name Field
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "NOME DO EVENTO",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B),
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = eventName,
                            onValueChange = { eventName = it },
                            placeholder = { Text("Ex: Torneio ESTG 2026", color = Color(0xFF94A3B8)) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = SportFlowDarkBlue,
                                unfocusedTextColor = SportFlowDarkBlue,
                                focusedBorderColor = Color(0xFF16A34A),
                                unfocusedBorderColor = Color(0xFFE2E8F0),
                                focusedContainerColor = Color(0xFFEFF6FF).copy(alpha = 0.4f),
                                unfocusedContainerColor = Color(0xFFEFF6FF).copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Modalidade Dropdown Field
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "MODALIDADE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B),
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        ExposedDropdownMenuBox(
                            expanded = modalidadeExpanded,
                            onExpandedChange = { modalidadeExpanded = !modalidadeExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedModalidade,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = modalidadeExpanded) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = SportFlowDarkBlue,
                                    unfocusedTextColor = SportFlowDarkBlue,
                                    focusedBorderColor = Color(0xFF16A34A),
                                    unfocusedBorderColor = Color(0xFFE2E8F0),
                                    focusedContainerColor = Color(0xFFEFF6FF).copy(alpha = 0.4f),
                                    unfocusedContainerColor = Color(0xFFEFF6FF).copy(alpha = 0.4f)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            
                            ExposedDropdownMenu(
                                expanded = modalidadeExpanded,
                                onDismissRequest = { modalidadeExpanded = false }
                            ) {
                                listOf("Padel", "Futebol", "Basquetebol", "Ténis").forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(item) },
                                        onClick = {
                                            selectedModalidade = item
                                            modalidadeExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Nivel Dropdown Field
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "NÍVEL DE COMPETIÇÃO",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B),
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        ExposedDropdownMenuBox(
                            expanded = nivelExpanded,
                            onExpandedChange = { nivelExpanded = !nivelExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedNivel,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = nivelExpanded) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = SportFlowDarkBlue,
                                    unfocusedTextColor = SportFlowDarkBlue,
                                    focusedBorderColor = Color(0xFF16A34A),
                                    unfocusedBorderColor = Color(0xFFE2E8F0),
                                    focusedContainerColor = Color(0xFFEFF6FF).copy(alpha = 0.4f),
                                    unfocusedContainerColor = Color(0xFFEFF6FF).copy(alpha = 0.4f)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )

                            ExposedDropdownMenu(
                                expanded = nivelExpanded,
                                onDismissRequest = { nivelExpanded = false }
                            ) {
                                listOf("Amador", "Profissional", "Semi-Profissional").forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(item) },
                                        onClick = {
                                            selectedNivel = item
                                            nivelExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Regulation Text Area Field
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "REGULAMENTO E REGRAS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B),
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = regulation,
                            onValueChange = { regulation = it },
                            placeholder = { Text("Descreva as regras específicas, formato de pontuação e conduta...", color = Color(0xFF94A3B8)) },
                            minLines = 4,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = SportFlowDarkBlue,
                                unfocusedTextColor = SportFlowDarkBlue,
                                focusedBorderColor = Color(0xFF16A34A),
                                unfocusedBorderColor = Color(0xFFE2E8F0),
                                focusedContainerColor = Color(0xFFEFF6FF).copy(alpha = 0.4f),
                                unfocusedContainerColor = Color(0xFFEFF6FF).copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // 3. Formato Card (Modo Escuro)
        item {
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
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Formato",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FormatOptionItem(
                            title = "Eliminatórias",
                            description = "K.O. direto até à final",
                            isSelected = selectedFormato == "Eliminatórias",
                            onClick = { selectedFormato = "Eliminatórias" }
                        )

                        FormatOptionItem(
                            title = "Grupos",
                            description = "Fase de grupos + eliminatórias",
                            isSelected = selectedFormato == "Grupos",
                            onClick = { selectedFormato = "Grupos" }
                        )

                        FormatOptionItem(
                            title = "Liga",
                            description = "Todos contra todos / Pontos",
                            isSelected = selectedFormato == "Liga",
                            onClick = { selectedFormato = "Liga" }
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Green Tip box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F5A36).copy(alpha = 0.3f))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Dica: O formato de eliminatórias é ideal para torneios rápidos de um fim de semana.",
                            fontSize = 11.sp,
                            color = SportFlowGreen,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // 4. Gestão de Calendário Card
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
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Gestão de Calendário",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportFlowDarkBlue
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Defina as datas das jornadas e fases finais.",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Adicionar Data button (Pill shaped)
                    Button(
                        onClick = { /* Add Date */ },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEFF6FF),
                            contentColor = Color(0xFF2563EB)
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
                                text = "Adicionar Data",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // List of Date/Jornadas Cards
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CalendarDateItem(
                            jornada = "JORNADA 1",
                            date = "12 Mai, 2026",
                            time = "09:00 - 18:00",
                            badgeText = "FECHADO",
                            badgeColor = Color(0xFFEF4444), // red badge
                            borderColor = Color(0xFFEF4444)
                        )

                        CalendarDateItem(
                            jornada = "JORNADA 2",
                            date = "19 Mai, 2026",
                            time = "09:00 - 18:00",
                            badgeText = "FECHADO",
                            badgeColor = Color(0xFFEF4444), // red badge
                            borderColor = Color(0xFFEF4444)
                        )

                        CalendarDateItem(
                            jornada = "JORNADA 3",
                            date = "Definir Data",
                            time = "--:--",
                            badgeText = "DISPONÍVEL",
                            badgeColor = Color(0xFF16A34A), // green badge
                            borderColor = Color(0xFF94A3B8)
                        )

                        // Dashed visual Card box at the bottom
                        DashedAddBox()
                    }
                }
            }
        }

        // 5. Footer shield alert & Actions
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Info text with green shield icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = Color(0xFF16A34A),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ao publicar, o evento fica visível para inscrições no ecossistema Elite Arena.",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        lineHeight = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Actions buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Guardar Rascunho button
                    Text(
                        text = "Guardar Rascunho",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportFlowDarkBlue,
                        modifier = Modifier
                            .clickable { /* Save draft */ }
                            .padding(vertical = 8.dp)
                    )

                    // Publicar Evento button
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isLoading = true
                                delay(1500)
                                isLoading = false
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)), // Green publish button
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "Publicar Evento",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }
        }
    } // end LazyColumn
    if (isLoading) {
        SportFlowLoadingOverlay()
    }
    } // end Box
}

@Composable
fun FormatOptionItem(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (isSelected) Color(0xFF1E293B) else Color(0xFF1E293B).copy(alpha = 0.5f)
    val borderStroke = if (isSelected) BorderStroke(1.dp, SportFlowGreen) else BorderStroke(0.5.dp, Color.White.copy(alpha = 0.1f))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = borderStroke
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            // Green check icon on active
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF16A34A)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarDateItem(
    jornada: String,
    date: String,
    time: String,
    badgeText: String,
    badgeColor: Color,
    borderColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF).copy(alpha = 0.4f)),
        border = BorderStroke(0.5.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            // Left color stripe edge
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(borderColor)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = jornada,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (jornada.contains("3")) Color(0xFF94A3B8) else Color(0xFF16A34A),
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = date,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = SportFlowDarkBlue
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = time,
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }

                // Status badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
fun DashedAddBox() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .border(
                border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                shape = RoundedCornerShape(12.dp)
            ), // In production we can use a custom dashed draw modifier, this represents a clean rounded outline placeholder
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = Color(0xFF94A3B8),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CreateEventScreenPreview() {
    CreateEventScreen()
}
