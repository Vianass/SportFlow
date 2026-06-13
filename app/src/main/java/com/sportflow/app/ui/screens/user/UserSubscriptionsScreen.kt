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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sportflow.app.model.Enrollment
import com.sportflow.app.ui.theme.SportFlowDarkBlue
import com.sportflow.app.ui.theme.SportFlowGreen
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

// Enum for Subscription Status
enum class SubscriptionStatus {
    CONFIRMED,
    PENDING,
    REJECTED
}

// Data model for User Subscriptions
data class UserSubscription(
    val enrollmentId: Long,
    val title: String,
    val subtitle: String,
    val date: String,
    val location: String,
    val status: SubscriptionStatus,
    val paymentStatus: String,
    val category: String,
    val priceLabel: String,
    val canPay: Boolean,
    val infoText: String? = null,
    val resultText: String? = null
)

@Composable
fun UserSubscriptionsScreen(
    viewModel: UserSubscriptionsViewModel = viewModel()
) {
    var selectedFilter by remember { mutableStateOf(0) }
    var selectedSubscription by remember { mutableStateOf<UserSubscription?>(null) }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadEnrollments()
    }

    val subscriptions = remember(uiState.enrollments) {
        uiState.enrollments.map { it.toUserSubscription() }
    }

    LaunchedEffect(subscriptions, selectedSubscription?.enrollmentId) {
        val currentId = selectedSubscription?.enrollmentId ?: return@LaunchedEffect
        selectedSubscription = subscriptions.firstOrNull { it.enrollmentId == currentId }
    }

    if (selectedSubscription != null) {
        com.sportflow.app.ui.components.SubscriptionDetailsDialog(
            subscription = selectedSubscription!!,
            isProcessingPayment = uiState.updatingPaymentEnrollmentId == selectedSubscription!!.enrollmentId,
            onMarkAsPaid = { viewModel.markEnrollmentAsPaid(selectedSubscription!!.enrollmentId) },
            onDismiss = { selectedSubscription = null }
        )
    }

    val filteredSubscriptions = remember(selectedFilter, subscriptions) {
        when (selectedFilter) {
            1 -> subscriptions.filter { it.status == SubscriptionStatus.CONFIRMED || it.status == SubscriptionStatus.PENDING }
            2 -> subscriptions.filter { it.status == SubscriptionStatus.REJECTED }
            else -> subscriptions
        }
    }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = SportFlowGreen)
        }
        return
    }

    if (uiState.errorMessage != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = Color(0xFFDC2626),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Erro ao carregar inscrições",
                fontWeight = FontWeight.Bold,
                color = SportFlowDarkBlue
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = uiState.errorMessage ?: "Erro desconhecido.",
                color = Color(0xFF64748B),
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = viewModel::loadEnrollments,
                colors = ButtonDefaults.buttonColors(containerColor = SportFlowGreen)
            ) {
                Text("Tentar novamente")
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Minhas Inscrições",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = SportFlowDarkBlue,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = viewModel::loadEnrollments) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Atualizar inscrições",
                            tint = SportFlowDarkBlue
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Gere as tuas participações nos próximos torneios de elite.",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 18.sp
                )
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val filters = listOf("TODAS", "ATIVAS", "REJEITADAS")
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

        if (filteredSubscriptions.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp, start = 24.dp, end = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Nenhuma inscrição encontrada",
                        fontWeight = FontWeight.Bold,
                        color = SportFlowDarkBlue
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Quando te inscreveres num torneio, a inscrição aparece aqui.",
                        color = Color(0xFF64748B),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(filteredSubscriptions) { subscription ->
                SubscriptionCard(
                    subscription = subscription,
                    isProcessingPayment = uiState.updatingPaymentEnrollmentId == subscription.enrollmentId,
                    onViewDetails = { selectedSubscription = subscription },
                    onMarkAsPaid = { viewModel.markEnrollmentAsPaid(subscription.enrollmentId) }
                )
            }
        }
    }
}

@Composable
fun SubscriptionCard(
    subscription: UserSubscription,
    isProcessingPayment: Boolean = false,
    onViewDetails: () -> Unit = {},
    onMarkAsPaid: () -> Unit = {}
) {
    val cardBorderColor = if (subscription.status == SubscriptionStatus.PENDING) {
        BorderStroke(1.dp, Color(0xFF86EFAC))
    } else {
        BorderStroke(0.5.dp, Color(0xFFE2E8F0))
    }

    val cardBg = if (subscription.status == SubscriptionStatus.REJECTED) {
        Color(0xFFEFF6FF).copy(alpha = 0.4f)
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
        elevation = CardDefaults.cardElevation(defaultElevation = if (subscription.status == SubscriptionStatus.REJECTED) 0.dp else 2.dp)
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
                StatusBadge(status = subscription.status)

                Text(
                    text = subscription.date,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = SportFlowDarkBlue
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = subscription.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SportFlowDarkBlue,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subscription.subtitle,
                fontSize = 13.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(10.dp))

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

            if (subscription.infoText != null) {
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

            Spacer(modifier = Modifier.height(16.dp))

            when (subscription.status) {
                SubscriptionStatus.CONFIRMED -> {
                    if (subscription.canPay) {
                        Button(
                            onClick = onMarkAsPaid,
                            enabled = !isProcessingPayment,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SportFlowGreen)
                        ) {
                            if (isProcessingPayment) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = SportFlowDarkBlue
                                )
                            } else {
                                Text(
                                    text = "FINALIZAR PAGAMENTO",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = SportFlowDarkBlue
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }

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
                        onClick = onViewDetails,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SportFlowGreen)
                    ) {
                        Text(
                            text = "VER INSCRIÇÃO",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = SportFlowDarkBlue
                        )
                    }
                }

                SubscriptionStatus.REJECTED -> {
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
            "APROVADA"
        )

        SubscriptionStatus.PENDING -> Triple(
            Color(0xFFEFF6FF),
            Color(0xFF2563EB),
            "PENDENTE"
        )

        SubscriptionStatus.REJECTED -> Triple(
            Color(0xFFF1F5F9),
            Color(0xFF64748B),
            "REJEITADA"
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

private fun Enrollment.toUserSubscription(): UserSubscription {
    val tournament = tournament
    val status = when (this.status.uppercase(Locale.ROOT)) {
        "APROVADA" -> SubscriptionStatus.CONFIRMED
        "REJEITADA" -> SubscriptionStatus.REJECTED
        else -> SubscriptionStatus.PENDING
    }

    val normalizedPaymentStatus = paymentStatus.uppercase(Locale.ROOT)
    val isPaymentPending = normalizedPaymentStatus == "PENDENTE"
    val canPay = status == SubscriptionStatus.CONFIRMED && isPaymentPending

    val paymentInfo = when {
        normalizedPaymentStatus == "PAGO" -> "Pagamento confirmado."
        status == SubscriptionStatus.PENDING -> "Inscrição pendente. Aguarda aprovação do organizador antes de finalizar o pagamento."
        canPay -> "Inscrição aprovada. Finaliza o pagamento para garantir a tua participação."
        status == SubscriptionStatus.REJECTED -> "Inscrição rejeitada pelo organizador."
        else -> "Pagamento pendente."
    }

    return UserSubscription(
        enrollmentId = id,
        title = tournament?.name ?: "Torneio removido",
        subtitle = tournament?.sport?.toSportLabel() ?: "Torneio",
        date = tournament?.startDate?.formatTournamentDate() ?: registeredAt?.formatTournamentDate() ?: "Data a definir",
        location = tournament?.location ?: "Local a definir",
        status = status,
        paymentStatus = normalizedPaymentStatus,
        category = tournament?.category ?: "Categoria a definir",
        priceLabel = tournament?.price.formatPrice(),
        canPay = canPay,
        infoText = if (normalizedPaymentStatus == "PAGO") null else paymentInfo,
        resultText = null
    )
}

private fun String.formatTournamentDate(): String? {
    val localDate = runCatching {
        OffsetDateTime.parse(this).toLocalDate()
    }.getOrElse {
        runCatching { LocalDate.parse(this) }.getOrNull()
    } ?: return null

    val month = localDate.month.getDisplayName(
        TextStyle.SHORT,
        Locale.forLanguageTag("pt")
    ).replaceFirstChar { it.uppercase(Locale.forLanguageTag("pt")) }

    return "${localDate.dayOfMonth} $month ${localDate.year}"
}

private fun String.toSportLabel(): String {
    return when (uppercase(Locale.ROOT)) {
        "SOCCER" -> "Futebol"
        "BASKETBALL" -> "Basquetebol"
        "TENNIS" -> "Ténis"
        "PADEL" -> "Padel"
        else -> this
    }
}

private fun Double?.formatPrice(): String {
    return when {
        this == null -> "Valor a definir"
        this <= 0.0 -> "Gratuito"
        else -> "%.2f€".format(Locale.forLanguageTag("pt-PT"), this)
    }
}

@Preview(showBackground = true)
@Composable
fun UserSubscriptionsScreenPreview() {
    UserSubscriptionsScreen()
}
