package com.sportflow.app.ui.screens.organizador

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import com.sportflow.app.ui.components.SportFlowLoadingOverlay
import com.sportflow.app.ui.theme.SportFlowDarkBlue
import com.sportflow.app.ui.theme.SportFlowGreen

private data class SportOption(
    val label: String,
    val value: String
)

private val sportOptions = listOf(
    SportOption(label = "Futebol", value = "SOCCER"),
    SportOption(label = "Padel", value = "PADEL"),
    SportOption(label = "Basquetebol", value = "BASKETBALL"),
    SportOption(label = "Ténis", value = "TENNIS")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    onEventCreated: () -> Unit = {},
    viewModel: CreateEventViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var eventName by remember { mutableStateOf("") }
    var regulation by remember { mutableStateOf("") }
    var selectedSport by remember { mutableStateOf(sportOptions.first()) }
    var selectedNivel by remember { mutableStateOf("Amador") }
    var selectedFormato by remember { mutableStateOf("Eliminatórias") }
    var eventDate by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    var modalidadeExpanded by remember { mutableStateOf(false) }
    var nivelExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.successMessage) {
        if (uiState.successMessage != null) {
            eventName = ""
            regulation = ""
            selectedSport = sportOptions.first()
            selectedNivel = "Amador"
            selectedFormato = "Eliminatórias"
            eventDate = ""
            location = ""
            capacity = ""
            price = ""

            delay(1200)
            viewModel.clearMessages()
            onEventCreated()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 20.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
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
                    Box(
                        modifier = Modifier
                            .width(76.dp)
                            .height(4.dp)
                            .background(Color(0xFF16A34A))
                    )
                }
            }

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

                        LabeledTextField(
                            label = "NOME DO EVENTO",
                            value = eventName,
                            onValueChange = {
                                eventName = it
                                viewModel.clearMessages()
                            },
                            placeholder = "Ex: Torneio ESTG 2026"
                        )

                        Column(modifier = Modifier.fillMaxWidth()) {
                            FieldLabel("MODALIDADE")
                            Spacer(modifier = Modifier.height(6.dp))

                            ExposedDropdownMenuBox(
                                expanded = modalidadeExpanded,
                                onExpandedChange = { modalidadeExpanded = !modalidadeExpanded }
                            ) {
                                OutlinedTextField(
                                    value = selectedSport.label,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = modalidadeExpanded) },
                                    colors = sportFlowTextFieldColors(),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor()
                                )

                                ExposedDropdownMenu(
                                    expanded = modalidadeExpanded,
                                    onDismissRequest = { modalidadeExpanded = false }
                                ) {
                                    sportOptions.forEach { item ->
                                        DropdownMenuItem(
                                            text = { Text(item.label) },
                                            onClick = {
                                                selectedSport = item
                                                modalidadeExpanded = false
                                                viewModel.clearMessages()
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Column(modifier = Modifier.fillMaxWidth()) {
                            FieldLabel("NÍVEL / CATEGORIA")
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
                                    colors = sportFlowTextFieldColors(),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor()
                                )

                                ExposedDropdownMenu(
                                    expanded = nivelExpanded,
                                    onDismissRequest = { nivelExpanded = false }
                                ) {
                                    listOf("Amador", "Semi-Profissional", "Profissional").forEach { item ->
                                        DropdownMenuItem(
                                            text = { Text(item) },
                                            onClick = {
                                                selectedNivel = item
                                                nivelExpanded = false
                                                viewModel.clearMessages()
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        LabeledTextField(
                            label = "LOCALIZAÇÃO",
                            value = location,
                            onValueChange = {
                                location = it
                                viewModel.clearMessages()
                            },
                            placeholder = "Ex: Pavilhão Desportivo da ESTG"
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            LabeledTextField(
                                label = "CAPACIDADE",
                                value = capacity,
                                onValueChange = {
                                    capacity = it.filter { char -> char.isDigit() }
                                    viewModel.clearMessages()
                                },
                                placeholder = "32",
                                keyboardType = KeyboardType.Number,
                                modifier = Modifier.weight(1f)
                            )

                            LabeledTextField(
                                label = "PREÇO",
                                value = price,
                                onValueChange = {
                                    price = it
                                    viewModel.clearMessages()
                                },
                                placeholder = "15.00",
                                keyboardType = KeyboardType.Decimal,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        LabeledTextField(
                            label = "DATA DO EVENTO",
                            value = eventDate,
                            onValueChange = {
                                eventDate = it
                                viewModel.clearMessages()
                            },
                            placeholder = "AAAA-MM-DD",
                            keyboardType = KeyboardType.Number
                        )
                    }
                }
            }

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

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0F5A36).copy(alpha = 0.3f))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "O formato ainda não é gravado no Supabase porque a tabela torneios não tem coluna formato.",
                                fontSize = 11.sp,
                                color = SportFlowGreen,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

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
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Gestão de Calendário",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = SportFlowDarkBlue
                        )
                        Text(
                            text = "Para esta fase vamos gravar apenas a data principal em torneios.data_inicio. Jornadas/fases ficam para quando ligarmos jogos.",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B),
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    uiState.errorMessage?.let { message ->
                        MessageBox(
                            message = message,
                            backgroundColor = Color(0xFFFEE2E2),
                            textColor = Color(0xFFB91C1C),
                            icon = Icons.Default.ErrorOutline
                        )
                    }

                    uiState.successMessage?.let { message ->
                        MessageBox(
                            message = message,
                            backgroundColor = Color(0xFFDCFCE7),
                            textColor = Color(0xFF166534),
                            icon = Icons.Default.CheckCircle
                        )
                    }

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
                            text = "Ao publicar, o evento fica visível para inscrições.",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            lineHeight = 16.sp
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.createTournament(
                                name = eventName,
                                sport = selectedSport.value,
                                category = selectedNivel,
                                date = eventDate,
                                location = location,
                                capacity = capacity,
                                price = price
                            )
                        },
                        enabled = !uiState.isSubmitting,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (uiState.isSubmitting) "A PUBLICAR..." else "PUBLICAR EVENTO",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }
        }

        if (uiState.isSubmitting) {
            SportFlowLoadingOverlay()
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF64748B),
        letterSpacing = 0.5.sp
    )
}

@Composable
private fun LabeledTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    minLines: Int = 1
) {
    Column(modifier = modifier.fillMaxWidth()) {
        FieldLabel(label)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color(0xFF94A3B8)) },
            singleLine = minLines == 1,
            minLines = minLines,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = sportFlowTextFieldColors(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun sportFlowTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = SportFlowDarkBlue,
    unfocusedTextColor = SportFlowDarkBlue,
    focusedBorderColor = Color(0xFF16A34A),
    unfocusedBorderColor = Color(0xFFE2E8F0),
    focusedContainerColor = Color(0xFFEFF6FF).copy(alpha = 0.4f),
    unfocusedContainerColor = Color(0xFFEFF6FF).copy(alpha = 0.4f)
)

@Composable
private fun MessageBox(
    message: String,
    backgroundColor: Color,
    textColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = message,
            fontSize = 12.sp,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
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

@Preview(showBackground = true)
@Composable
fun CreateEventScreenPreview() {
    CreateEventScreen()
}
