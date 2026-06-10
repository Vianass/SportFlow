package com.sportflow.app.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sportflow.app.model.AppLanguage
import com.sportflow.app.R
import com.sportflow.app.ui.theme.SportFlowDarkBlue
import com.sportflow.app.ui.theme.SportFlowGreen
import kotlinx.coroutines.delay

enum class PaymentStep {
    METHOD_SELECTION,
    DETAILS_INPUT,
    PROCESSING,
    SUCCESS
}

enum class PaymentMethod {
    MBWAY,
    CREDIT_CARD,
    APPLE_PAY
}

@Composable
fun PaymentDialog(
    currentLanguage: AppLanguage,
    isCheckout: Boolean = false,
    onPaymentSuccess: () -> Unit = {},
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        PaymentDialogContent(currentLanguage, isCheckout, onPaymentSuccess, onDismiss)
    }
}

@Composable
fun PaymentDialogContent(
    currentLanguage: AppLanguage,
    isCheckout: Boolean = false,
    onPaymentSuccess: () -> Unit = {},
    onDismiss: () -> Unit
) {
    var currentStep by remember { mutableStateOf(PaymentStep.METHOD_SELECTION) }
    var selectedMethod by remember { mutableStateOf<PaymentMethod?>(null) }
    val isPT = currentLanguage == AppLanguage.PT

    // Details state
    var phoneNumber by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }

    // Coroutine for processing simulation
    LaunchedEffect(currentStep) {
        if (currentStep == PaymentStep.PROCESSING) {
            delay(1500) // Simulate network request
            currentStep = PaymentStep.SUCCESS
        }
    }

    Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
                .clickable(onClick = onDismiss)
                .imePadding(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Color(0xFFF8FAFC))
                    .clickable(enabled = false) {}
                    .padding(bottom = 36.dp)
                    .animateContentSize()
            ) {
                // Drag handle
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 12.dp)
                        .size(width = 40.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFFCBD5E1))
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        val title = when (currentStep) {
                            PaymentStep.METHOD_SELECTION -> if (isPT) "Método de Pagamento" else "Payment Method"
                            PaymentStep.DETAILS_INPUT -> when (selectedMethod) {
                                PaymentMethod.MBWAY -> "MB Way"
                                PaymentMethod.APPLE_PAY -> "Apple Pay"
                                else -> if (isPT) "Cartão de Crédito" else "Credit Card"
                            }
                            PaymentStep.PROCESSING -> if (isPT) "A Processar..." else "Processing..."
                            PaymentStep.SUCCESS -> if (isPT) "Sucesso" else "Success"
                        }

                        val subtitle = when (currentStep) {
                            PaymentStep.METHOD_SELECTION -> if (isPT) "Escolhe como queres pagar os torneios" else "Choose how you want to pay for tournaments"
                            PaymentStep.DETAILS_INPUT -> if (isPT) "Insere os teus detalhes" else "Enter your details"
                            PaymentStep.PROCESSING -> if (isPT) "A validar o teu método" else "Validating your method"
                            PaymentStep.SUCCESS -> if (isPT) "O método foi guardado!" else "Method was saved!"
                        }

                        Text(
                            text = title,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = SportFlowDarkBlue
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = subtitle,
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEFF6FF))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = if (isPT) "Fechar" else "Close",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Content based on step
                when (currentStep) {
                    PaymentStep.METHOD_SELECTION -> {
                        PaymentMethodOption(
                            iconRes = R.drawable.mbway_logo,
                            title = "MB Way",
                            subtitle = if (isPT) "Rápido e seguro pelo telemóvel" else "Fast and secure via mobile",
                            iconBg = Color(0xFFFEF2F2),
                            iconTint = Color(0xFFEF4444),
                            isSelected = selectedMethod == PaymentMethod.MBWAY,
                            onClick = { selectedMethod = PaymentMethod.MBWAY }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        PaymentMethodOption(
                            icon = Icons.Default.CreditCard,
                            title = if (isPT) "Cartão de Crédito" else "Credit Card",
                            subtitle = "Visa, Mastercard, AMEX",
                            iconBg = Color(0xFFEFF6FF),
                            iconTint = Color(0xFF2563EB),
                            isSelected = selectedMethod == PaymentMethod.CREDIT_CARD,
                            onClick = { selectedMethod = PaymentMethod.CREDIT_CARD }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        PaymentMethodOption(
                            iconRes = R.drawable.apple_pay_logo,
                            title = "Apple Pay",
                            subtitle = if (isPT) "Paga com um toque" else "Pay with one touch",
                            iconBg = Color(0xFFF1F5F9), // Light gray/black theme for Apple
                            iconTint = Color.Unspecified,
                            isSelected = selectedMethod == PaymentMethod.APPLE_PAY,
                            onClick = { selectedMethod = PaymentMethod.APPLE_PAY }
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = { currentStep = PaymentStep.DETAILS_INPUT },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SportFlowDarkBlue),
                            shape = RoundedCornerShape(12.dp),
                            enabled = selectedMethod != null
                        ) {
                            Text(
                                text = if (isPT) "CONTINUAR" else "CONTINUE",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    PaymentStep.DETAILS_INPUT -> {
                        if (selectedMethod == PaymentMethod.APPLE_PAY) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF0F172A)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.apple_pay_logo),
                                        contentDescription = "Apple Pay",
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = if (isPT) "Confirma com Face ID ou Touch ID" else "Confirm with Face ID or Touch ID",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SportFlowDarkBlue,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        } else if (selectedMethod == PaymentMethod.MBWAY) {
                            OutlinedTextField(
                                value = phoneNumber,
                                onValueChange = { phoneNumber = it },
                                label = { Text(if (isPT) "Número de Telemóvel" else "Phone Number", color = Color(0xFF64748B)) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SportFlowDarkBlue,
                                    unfocusedBorderColor = Color(0xFFE2E8F0)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        } else {
                            OutlinedTextField(
                                value = cardNumber,
                                onValueChange = { cardNumber = it },
                                label = { Text(if (isPT) "Número do Cartão" else "Card Number", color = Color(0xFF64748B)) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SportFlowDarkBlue,
                                    unfocusedBorderColor = Color(0xFFE2E8F0)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = expiryDate,
                                    onValueChange = { expiryDate = it },
                                    label = { Text("MM/AA", color = Color(0xFF64748B)) },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = SportFlowDarkBlue,
                                        unfocusedBorderColor = Color(0xFFE2E8F0)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                OutlinedTextField(
                                    value = cvv,
                                    onValueChange = { cvv = it },
                                    label = { Text("CVV", color = Color(0xFF64748B)) },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = SportFlowDarkBlue,
                                        unfocusedBorderColor = Color(0xFFE2E8F0)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        val isValid = when (selectedMethod) {
                            PaymentMethod.MBWAY -> phoneNumber.length >= 9
                            PaymentMethod.APPLE_PAY -> true
                            else -> cardNumber.isNotEmpty() && expiryDate.isNotEmpty() && cvv.isNotEmpty()
                        }

                        Button(
                            onClick = { currentStep = PaymentStep.PROCESSING },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SportFlowGreen),
                            shape = RoundedCornerShape(12.dp),
                            enabled = isValid
                        ) {
                            val btnText = if (selectedMethod == PaymentMethod.APPLE_PAY) {
                                if (isPT) "CLIQUE DUPLO PARA PAGAR" else "DOUBLE CLICK TO PAY"
                            } else {
                                if (isPT) "CONFIRMAR" else "CONFIRM"
                            }
                            Text(
                                text = btnText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    PaymentStep.PROCESSING -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = SportFlowGreen)
                        }
                    }

                    PaymentStep.SUCCESS -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFECFDF5)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Success",
                                    tint = SportFlowGreen,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (isPT) "Tudo pronto!" else "All set!",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = SportFlowDarkBlue
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isPT) {
                                    if (isCheckout) "Pagamento concluído com sucesso!" else "Este método será usado na tua próxima inscrição."
                                } else {
                                    if (isCheckout) "Payment completed successfully!" else "This method will be used on your next enrollment."
                                },
                                fontSize = 13.sp,
                                color = Color(0xFF64748B),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )
                            Spacer(modifier = Modifier.height(32.dp))
                            Button(
                                onClick = {
                                    onPaymentSuccess()
                                    onDismiss()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp)
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SportFlowDarkBlue),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = if (isPT) "FECHAR" else "CLOSE",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }

@Composable
private fun PaymentMethodOption(
    icon: ImageVector? = null,
    iconRes: Int? = null,
    title: String,
    subtitle: String,
    iconBg: Color,
    iconTint: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) SportFlowDarkBlue else Color(0xFFE2E8F0)
    val bgColor = if (isSelected) Color(0xFFF8FAFC) else Color.White

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon box
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            if (iconRes != null) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(26.dp)
                )
            } else if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = SportFlowDarkBlue
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Check circle
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(SportFlowDarkBlue),
                contentAlignment = Alignment.Center
            ) {
                // Inner white circle
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.White))
            }
        } else {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, Color(0xFFE2E8F0), CircleShape)
            )
        }
    }
}

@Preview
@Composable
private fun PaymentDialogPreview() {
    PaymentDialogContent(
        currentLanguage = AppLanguage.PT,
        onDismiss = {}
    )
}
