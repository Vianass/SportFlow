package com.sportflow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sportflow.app.ui.screens.user.SubscriptionStatus
import com.sportflow.app.ui.screens.user.UserSubscription
import com.sportflow.app.ui.theme.SportFlowDarkBlue
import com.sportflow.app.ui.theme.SportFlowGreen

@Composable
fun SubscriptionDetailsDialog(
    subscription: UserSubscription,
    isProcessingPayment: Boolean = false,
    onMarkAsPaid: () -> Unit = {},
    onDismiss: () -> Unit
) {
    val header = subscription.toDialogHeader()

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
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(header.iconBackground, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = header.icon,
                        contentDescription = null,
                        tint = header.iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = header.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = SportFlowDarkBlue,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = header.description,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF8FAFC))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    DetailRow(icon = Icons.Default.EmojiEvents, label = "Torneio", value = subscription.title)
                    Spacer(modifier = Modifier.height(12.dp))
                    DetailRow(icon = Icons.Default.Category, label = "Categoria", value = subscription.category)
                    Spacer(modifier = Modifier.height(12.dp))
                    DetailRow(icon = Icons.Default.Event, label = "Data", value = subscription.date)
                    Spacer(modifier = Modifier.height(12.dp))
                    DetailRow(icon = Icons.Default.Place, label = "Local", value = subscription.location)
                    Spacer(modifier = Modifier.height(12.dp))
                    DetailRow(
                        icon = Icons.Default.Payment,
                        label = "Pagamento",
                        value = "${subscription.priceLabel} • ${subscription.paymentStatus}"
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (subscription.canPay) {
                        Button(
                            onClick = onMarkAsPaid,
                            enabled = !isProcessingPayment,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SportFlowGreen),
                            shape = RoundedCornerShape(12.dp)
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
                                    color = SportFlowDarkBlue,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SportFlowDarkBlue),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "FECHAR",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    if (subscription.paymentStatus == "PAGO") {
                        TextButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "DESCARREGAR FATURA",
                                color = Color(0xFF3B82F6),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF94A3B8),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
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

private data class SubscriptionDialogHeader(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val iconBackground: Color,
    val iconTint: Color
)

private fun UserSubscription.toDialogHeader(): SubscriptionDialogHeader {
    return when (status) {
        SubscriptionStatus.CONFIRMED -> {
            if (paymentStatus == "PAGO") {
                SubscriptionDialogHeader(
                    title = "Inscrição e pagamento confirmados",
                    description = "O teu lugar está garantido neste torneio. Prepara-te para a competição!",
                    icon = Icons.Default.CheckCircle,
                    iconBackground = Color(0xFFDCFCE7),
                    iconTint = Color(0xFF16A34A)
                )
            } else {
                SubscriptionDialogHeader(
                    title = "Inscrição aprovada",
                    description = "A tua inscrição foi aprovada. Finaliza o pagamento para concluir a participação.",
                    icon = Icons.Default.CheckCircle,
                    iconBackground = Color(0xFFDCFCE7),
                    iconTint = Color(0xFF16A34A)
                )
            }
        }

        SubscriptionStatus.PENDING -> SubscriptionDialogHeader(
            title = "Inscrição pendente",
            description = "A inscrição está a aguardar aprovação do organizador. O pagamento só deve ser feito depois da aprovação.",
            icon = Icons.Default.HourglassTop,
            iconBackground = Color(0xFFEFF6FF),
            iconTint = Color(0xFF2563EB)
        )

        SubscriptionStatus.REJECTED -> SubscriptionDialogHeader(
            title = "Inscrição rejeitada",
            description = "O organizador rejeitou esta inscrição. Consulta outros torneios disponíveis.",
            icon = Icons.Default.Cancel,
            iconBackground = Color(0xFFFEE2E2),
            iconTint = Color(0xFFDC2626)
        )
    }
}
