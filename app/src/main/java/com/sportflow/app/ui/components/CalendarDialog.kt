package com.sportflow.app.ui.components

import com.sportflow.app.ui.localization.localizedText

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import com.sportflow.app.ui.localization.Text
import com.sportflow.app.ui.localization.appLocale
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
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarDialog(
    initialSelectedDate: LocalDate?,
    onDateSelected: (LocalDate?) -> Unit,
    onDismiss: () -> Unit
) {
    val locale = appLocale()
    // Determine the month to start showing. If there is a selected date, show that month.
    // Otherwise, default to Sept 2024 which has most of our mock events.
    var currentMonth by remember { 
        mutableStateOf(
            if (initialSelectedDate != null) YearMonth.from(initialSelectedDate)
            else YearMonth.of(2024, 9)
        )
    }
    var selectedDate by remember { mutableStateOf(initialSelectedDate) }

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
                        text = "Calendário",
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
                            contentDescription = localizedText("Fechar"),
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Month Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = localizedText("Mês Anterior"), tint = SportFlowDarkBlue)
                    }
                    Text(
                        text = "${currentMonth.month.getDisplayName(TextStyle.FULL, locale).replaceFirstChar { it.uppercase(locale) }} ${currentMonth.year}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportFlowDarkBlue
                    )
                    IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                        Icon(Icons.Default.ChevronRight, contentDescription = localizedText("Próximo Mês"), tint = SportFlowDarkBlue)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Days of week header
                val daysOfWeek = listOf("D", "S", "T", "Q", "Q", "S", "S")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    daysOfWeek.forEach { day ->
                        Text(
                            text = day,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8),
                            modifier = Modifier.width(36.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Calendar Grid
                val firstDayOfWeek = currentMonth.atDay(1).dayOfWeek.value % 7 // 0 for Sunday
                val daysInMonth = currentMonth.lengthOfMonth()
                
                // Let's identify which dates have events to show green dots under them
                val eventDates = listOf(
                    // Home Screen events
                    LocalDate.of(2024, 9, 24),
                    LocalDate.of(2024, 10, 2),
                    LocalDate.of(2024, 10, 15),
                    // Events Screen events
                    LocalDate.of(2024, 5, 15),
                    LocalDate.of(2024, 5, 22),
                    LocalDate.of(2024, 6, 5),
                    LocalDate.of(2024, 6, 12)
                )

                var dayCounter = 1
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (row in 0..5) {
                        if (dayCounter > daysInMonth) break
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            for (col in 0..6) {
                                if ((row == 0 && col < firstDayOfWeek) || dayCounter > daysInMonth) {
                                    Box(modifier = Modifier.size(36.dp))
                                } else {
                                    val currentDay = dayCounter
                                    val date = currentMonth.atDay(currentDay)
                                    val isSelected = selectedDate == date
                                    val hasEvent = eventDates.contains(date)

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) SportFlowDarkBlue else Color.Transparent)
                                            .clickable { selectedDate = date }
                                    ) {
                                        Text(
                                            text = currentDay.toString(),
                                            fontSize = 13.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else SportFlowDarkBlue
                                        )
                                        if (hasEvent) {
                                            Box(
                                                modifier = Modifier
                                                    .size(4.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isSelected) Color.White else SportFlowGreen)
                                            )
                                        } else {
                                            Spacer(modifier = Modifier.height(4.dp))
                                        }
                                    }
                                    dayCounter++
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            selectedDate = null
                            onDateSelected(null)
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
                        onClick = { onDateSelected(selectedDate) },
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

@Preview(showBackground = true)
@Composable
fun CalendarDialogPreview() {
    CalendarDialog(
        initialSelectedDate = LocalDate.of(2024, 9, 24),
        onDateSelected = {},
        onDismiss = {}
    )
}
