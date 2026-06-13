package com.sportflow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import com.sportflow.app.ui.localization.Text
import androidx.compose.runtime.Composable
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
import com.sportflow.app.ui.screens.user.TournamentEvent
import com.sportflow.app.ui.theme.SportFlowDarkBlue
import com.sportflow.app.ui.theme.SportFlowGreen
import java.time.LocalDate
import java.util.Locale


@Composable
fun TournamentEnrollDialog(
    tournament: TournamentEvent,
    onEnroll: () -> Unit,
    onDismiss: () -> Unit
) {
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
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFFEFF6FF), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = Color(0xFF3B82F6),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Inscrever no Torneio",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = SportFlowDarkBlue
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Revê os detalhes antes de garantires a vaga da tua equipa. As vagas são limitadas!",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Detail Box
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF8FAFC))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    EnrollDetailRow(icon = tournament.icon, label = "Torneio", value = tournament.title)
                    Spacer(modifier = Modifier.height(12.dp))
                    EnrollDetailRow(icon = Icons.Default.Category, label = "Categoria", value = tournament.category)
                    Spacer(modifier = Modifier.height(12.dp))
                    EnrollDetailRow(icon = Icons.Default.Event, label = "Data", value = tournament.date)
                    Spacer(modifier = Modifier.height(12.dp))
                    EnrollDetailRow(icon = Icons.Default.Place, label = "Local", value = tournament.location)
                    Spacer(modifier = Modifier.height(12.dp))

                    EnrollDetailRow(
                        icon = Icons.Default.Payments,
                        label = "Valor da Inscrição",
                        value = formatEnrollmentPrice(tournament.price)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFEF3C7))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = tournament.vacanciesLeft?.let {
                            "Restam apenas $it vagas! Inscreve-te rápido."
                        } ?: "A disponibilidade será confirmada no momento da inscrição.",
                        fontSize = 11.sp,
                        color = Color(0xFFB45309),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Actions
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onEnroll,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SportFlowGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "AVANÇAR PARA PAGAMENTO",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "CANCELAR",
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

private fun formatEnrollmentPrice(price: Double?): String {
    return when {
        price == null -> "Valor a definir"
        price <= 0.0 -> "Gratuito"
        else -> "%.2f€ / equipa".format(Locale.forLanguageTag("pt-PT"), price)
    }
}

@Composable
private fun EnrollDetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF64748B),
                modifier = Modifier.size(14.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8)
            )
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = SportFlowDarkBlue
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TournamentEnrollDialogPreview() {
    TournamentEnrollDialog(
        tournament = TournamentEvent(
            id = 1,
            category = "BASQUETEBOL • LIGA PRO",
            title = "Master Cup Lisboa 2024",
            date = "15 MAIO, 2024",
            location = "Arena 2, Lisboa",
            vacanciesLeft = 12,
            isSoldOut = false,
            sportType = "BASKETBALL",
            icon = Icons.Default.SportsBasketball,
            localDate = LocalDate.of(2024, 5, 15),
            price = null
        ),
        onEnroll = {},
        onDismiss = {}
    )
}
