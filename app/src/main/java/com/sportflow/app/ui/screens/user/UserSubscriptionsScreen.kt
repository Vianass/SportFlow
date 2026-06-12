package com.sportflow.app.ui.screens.user

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import com.sportflow.app.ui.localization.LocalAppLanguage
import com.sportflow.app.ui.localization.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sportflow.app.ui.theme.SportFlowDarkBlue
import com.sportflow.app.ui.theme.SportFlowGreen
import com.sportflow.app.ui.theme.SportFlowTextGray

// Enum for Subscription Status
enum class SubscriptionStatus {
    CONFIRMED,
    PENDING,
    FINISHED
}

// Data model for User Subscriptions
data class UserSubscription(
    val title: String,
    val subtitle: String,
    val date: String,
    val location: String,
    val status: SubscriptionStatus,
    val infoText: String? = null,
    val resultText: String? = null
)

@Composable
fun UserSubscriptionsScreen() {
    val currentLanguage = LocalAppLanguage.current
    // Interactive filter state: 0 = TODAS, 1 = ATIVAS, 2 = CONCLUÍDAS
    var selectedFilter by remember { mutableStateOf(0) }
    var selectedSubscription by remember { mutableStateOf<UserSubscription?>(null) }
    var showPaymentDialogFor by remember { mutableStateOf<UserSubscription?>(null) }

    if (selectedSubscription != null) {
        com.sportflow.app.ui.components.SubscriptionDetailsDialog(
            subscription = selectedSubscription!!,
            onDismiss = { selectedSubscription = null }
        )
    }

    // Mock data matching the mockup exactly
    val subscriptions = remember {
        androidx.compose.runtime.mutableStateListOf(
            UserSubscription(
                title = "Padel Master Series",
                subtitle = "Torneio Open de Lisboa",
                date = "15 Out 2024",
                location = "Lisbon Racket Centre",
                status = SubscriptionStatus.CONFIRMED
            ),
            UserSubscription(
                title = "CrossFit Invitational",
                subtitle = "Elite Arena Games 2024",
                date = "02 Nov 2024",
                location = "Arena Norte, Porto",
                status = SubscriptionStatus.PENDING,
                infoText = "A aguardar confirmação de pagamento via MBWay."
            ),
            UserSubscription(
                title = "Ténis Amador",
                subtitle = "Open de Verão Quinta do Lago",
                date = "20 Ago 2024",
                location = "Vilamoura Academy", // Representative coordinates
                status = SubscriptionStatus.FINISHED,
                resultText = "4º Classificado"
            )
        )
    }

    if (showPaymentDialogFor != null) {
        com.sportflow.app.ui.components.PaymentDialog(
            currentLanguage = currentLanguage,
            isCheckout = true,
            onPaymentSuccess = {
                val index = subscriptions.indexOf(showPaymentDialogFor)
                if (index != -1) {
                    subscriptions[index] = subscriptions[index].copy(
                        status = SubscriptionStatus.CONFIRMED,
                        infoText = null
                    )
                }
            },
            onDismiss = { showPaymentDialogFor = null }
        )
    }

    // Dynamic filtering logic
    val filteredSubscriptions = remember(selectedFilter, subscriptions.toList()) {
        when (selectedFilter) {
            1 -> subscriptions.filter { it.status == SubscriptionStatus.CONFIRMED || it.status == SubscriptionStatus.PENDING }
            2 -> subscriptions.filter { it.status == SubscriptionStatus.FINISHED }
            else -> subscriptions
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Hero Header Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Text(
                    text = "Minhas Inscrições",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = SportFlowDarkBlue
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Gere as tuas participações nos próximos torneios de elite.",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 18.sp
                )
            }
        }

        // Segmented Filter Pills
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val filters = listOf("TODAS", "ATIVAS", "CONCLUÍDAS")
                filters.forEachIndexed { index, label ->
                    val isSelected = selectedFilter == index
                    val bg = if (isSelected) SportFlowDarkBlue else Color(0xFFEFF6FF)
                    val tc = if (isSelected) Color.White else Color(0xFF475569)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(19.dp))
                            .background(bg)
                            .clickable { selectedFilter = index },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = tc,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // List of Subscription Cards
        items(filteredSubscriptions) { subscription ->
            SubscriptionCard(
                subscription = subscription,
                onViewDetails = { selectedSubscription = subscription },
                onCheckout = { showPaymentDialogFor = subscription }
            )
        }
    }
}

@Composable
fun SubscriptionCard(
    subscription: UserSubscription,
    onViewDetails: () -> Unit = {},
    onCheckout: () -> Unit = {}
) {
    // Dynamic styles based on status
    val cardBorderColor = if (subscription.status == SubscriptionStatus.PENDING) {
        BorderStroke(1.dp, Color(0xFF86EFAC)) // Green outline for pending attention
    } else {
        BorderStroke(0.5.dp, Color(0xFFE2E8F0))
    }

    val cardBg = if (subscription.status == SubscriptionStatus.FINISHED) {
        Color(0xFFEFF6FF).copy(alpha = 0.4f) // Shaded/dimmed background for historical events
    } else {
        Color.White
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 10.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = cardBorderColor,
        elevation = CardDefaults.cardElevation(defaultElevation = if (subscription.status == SubscriptionStatus.FINISHED) 0.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Top Row: Status badge and Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status Badge
                StatusBadge(status = subscription.status)

                // Date
                Text(
                    text = subscription.date,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = SportFlowDarkBlue
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Title and Subtitle
            Text(
                text = subscription.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SportFlowDarkBlue
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subscription.subtitle,
                fontSize = 13.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Location details
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = subscription.location,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Info Box / Warning Box for PENDING status
            if (subscription.status == SubscriptionStatus.PENDING && subscription.infoText != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFEFF6FF))
                        .border(
                            BorderStroke(0.5.dp, Color(0xFFDBEAFE)),
                            RoundedCornerShape(8.dp)
                        )
                ) {
                    // Left vertical green highlight bar
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(54.dp)
                            .background(Color(0xFF22C55E))
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = subscription.infoText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1E293B),
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // Trophy/Medal Result Badge for FINISHED status
            if (subscription.status == SubscriptionStatus.FINISHED && subscription.resultText != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFEFF6FF))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = Color(0xFF22C55E),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = subscription.resultText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SportFlowDarkBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            when (subscription.status) {
                SubscriptionStatus.CONFIRMED -> {
                    Button(
                        onClick = onViewDetails,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Text(
                            text = "VER DETALHES",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SportFlowDarkBlue
                        )
                    }
                }
                SubscriptionStatus.PENDING -> {
                    Button(
                        onClick = onCheckout,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SportFlowGreen)
                    ) {
                        Text(
                            text = "FINALIZAR PAGAMENTO",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = SportFlowDarkBlue
                        )
                    }
                }
                SubscriptionStatus.FINISHED -> {
                    // Historical finished cards do not show primary actions directly
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: SubscriptionStatus) {
    val (bg, tc, label) = when (status) {
        SubscriptionStatus.CONFIRMED -> Triple(
            Color(0xFFDCFCE7),
            Color(0xFF16A34A),
            "CONFIRMADA"
        )
        SubscriptionStatus.PENDING -> Triple(
            Color(0xFFEFF6FF),
            Color(0xFF2563EB),
            "PENDENTE"
        )
        SubscriptionStatus.FINISHED -> Triple(
            Color(0xFFF1F5F9),
            Color(0xFF64748B),
            "FINALIZADA"
        )
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            color = tc,
            letterSpacing = 0.5.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserSubscriptionsScreenPreview() {
    UserSubscriptionsScreen()
}
