package com.sportflow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sportflow.app.ui.theme.SportFlowDarkBlue
import com.sportflow.app.ui.theme.SportFlowGreen

@Composable
fun EventsFilterDialog(
    initialSportFilter: String,
    initialAvailabilityFilter: String,
    onApplyFilters: (sport: String, availability: String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedSport by remember { mutableStateOf(initialSportFilter) }
    var selectedAvailability by remember { mutableStateOf(initialAvailabilityFilter) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filtros",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = SportFlowDarkBlue
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Modalidade Section
                Text(
                    text = "Modalidade",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                val sports = listOf("Todas", "Basquetebol", "Padel", "Futebol", "Ténis")
                FilterChipGroup(
                    items = sports,
                    selectedItem = selectedSport,
                    onItemSelected = { selectedSport = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Vagas Section
                Text(
                    text = "Disponibilidade",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                val availabilities = listOf("Todas", "Apenas com vagas")
                FilterChipGroup(
                    items = availabilities,
                    selectedItem = selectedAvailability,
                    onItemSelected = { selectedAvailability = it }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            selectedSport = "Todas"
                            selectedAvailability = "Todas"
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "LIMPAR",
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                    Button(
                        onClick = { onApplyFilters(selectedSport, selectedAvailability) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SportFlowDarkBlue),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "APLICAR",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterChipGroup(
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { item ->
            val isSelected = item == selectedItem
            val bgColor = if (isSelected) SportFlowDarkBlue else Color.White
            val textColor = if (isSelected) Color.White else Color(0xFF64748B)
            val borderColor = if (isSelected) SportFlowDarkBlue else Color(0xFFE2E8F0)

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(bgColor)
                    .border(1.dp, borderColor, RoundedCornerShape(20.dp))
                    .clickable { onItemSelected(item) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = item,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = textColor
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EventsFilterDialogPreview() {
    EventsFilterDialog(
        initialSportFilter = "Todas",
        initialAvailabilityFilter = "Apenas com vagas",
        onApplyFilters = { _, _ -> },
        onDismiss = {}
    )
}
