package com.sportflow.app.ui.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sportflow.app.data.remote.dto.ProfileDto
import com.sportflow.app.model.AppLanguage
import com.sportflow.app.model.LocalLanguageViewModel
import com.sportflow.app.ui.components.ChangePasswordDialog
import com.sportflow.app.ui.components.LanguagePickerDialog
import com.sportflow.app.ui.components.NotificationsDialog
import com.sportflow.app.ui.components.PaymentDialog
import com.sportflow.app.ui.components.UserPrivacyDialog
import com.sportflow.app.ui.theme.SportFlowDarkBlue
import com.sportflow.app.ui.theme.SportFlowGreen
import com.sportflow.app.ui.viewmodel.AdminViewModel
import com.sportflow.app.ui.viewmodel.ProfileState
import com.sportflow.app.ui.viewmodel.ProfileViewModel

@Composable
fun UserProfileScreen(
    onLogout: () -> Unit = {},
    viewModel: ProfileViewModel = viewModel(),
    adminViewModel: AdminViewModel = viewModel()
) {
    val langViewModel = LocalLanguageViewModel.current
    val currentLanguage by langViewModel.language.collectAsState()
    val notificationsEnabled by langViewModel.notificationsEnabled.collectAsState()
    val profilePublic by langViewModel.profilePublic.collectAsState()
    val showInRankings by langViewModel.showInRankings.collectAsState()
    val locationEnabled by langViewModel.locationEnabled.collectAsState()
    val context = LocalContext.current
    
    val profileState by viewModel.profileState.collectAsState()
    val pendingUsers by adminViewModel.pendingUsers.collectAsState()

    var showLanguagePicker by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showPaymentDialog by remember { mutableStateOf(false) }

    if (showLanguagePicker) {
        LanguagePickerDialog(
            currentLanguage = currentLanguage,
            onLanguageSelected = { lang -> langViewModel.setLanguage(lang, context) },
            onDismiss = { showLanguagePicker = false }
        )
    }
    if (showNotificationsDialog) {
        NotificationsDialog(
            enabled = notificationsEnabled,
            currentLanguage = currentLanguage,
            onToggle = { enabled -> langViewModel.setNotificationsEnabled(enabled, context) },
            onDismiss = { showNotificationsDialog = false }
        )
    }
    if (showPrivacyDialog) {
        UserPrivacyDialog(
            currentLanguage = currentLanguage,
            profilePublic = profilePublic,
            showInRankings = showInRankings,
            locationEnabled = locationEnabled,
            onProfilePublicToggle = { langViewModel.setProfilePublic(it, context) },
            onShowInRankingsToggle = { langViewModel.setShowInRankings(it, context) },
            onLocationToggle = { langViewModel.setLocationEnabled(it, context) },
            onDismiss = { showPrivacyDialog = false }
        )
    }
    if (showPasswordDialog) {
        ChangePasswordDialog(
            currentLanguage = currentLanguage,
            onSave = { /* Save logic here */ },
            onDismiss = { showPasswordDialog = false }
        )
    }

    if (showPaymentDialog) {
        PaymentDialog(
            currentLanguage = currentLanguage,
            onDismiss = { showPaymentDialog = false }
        )
    }

    when (profileState) {
        is ProfileState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = SportFlowGreen)
            }
        }
        is ProfileState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = (profileState as ProfileState.Error).message, color = Color.Red)
            }
        }
        is ProfileState.Success -> {
            val profile = (profileState as ProfileState.Success).profile
            
            // Forçar refresh dos utilizadores pendentes se for ADMIN
            androidx.compose.runtime.LaunchedEffect(profile.papel) {
                if (profile.papel == "ADMIN") {
                    adminViewModel.loadPendingUsers()
                }
            }
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 24.dp, bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Profile Avatar & Basic Info Card
                item {
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(24.dp))
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color(0xFF3B82F6), SportFlowDarkBlue)
                                    )
                                )
                                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(24.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.size(75.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (profile.papel == "JOGADOR") "ATLETA" else profile.papel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF16A34A),
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = profile.nome,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = SportFlowDarkBlue
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // 2. ADMIN ONLY: Pendidos de Aprovação
                if (profile.papel == "ADMIN" && pendingUsers.isNotEmpty()) {
                    item {
                        SectionTitle(title = "APROVAÇÕES PENDENTES")
                    }
                    items(pendingUsers) { pendingUser ->
                        PendingUserItem(
                            user = pendingUser,
                            onApprove = { adminViewModel.approveUser(pendingUser.id) },
                            onReject = { adminViewModel.rejectUser(pendingUser.id) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }

                // 3. DADOS DA CONTA
                item {
                    SectionTitle(title = "DADOS DA CONTA")
                    AccountDataItem(
                        icon = Icons.Default.Email,
                        title = "Email",
                        value = profile.email
                    )
                    profile.metodoPagamento?.let { 
                        AccountDataItem(
                            icon = Icons.Default.CreditCard,
                            title = "Método de Pagamento",
                            value = it
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 4. CONFIGURAÇÕES
                item {
                    SectionTitle(title = "CONFIGURAÇÕES")
                    SettingsItem(
                        icon = Icons.Default.Notifications,
                        title = if (currentLanguage == AppLanguage.PT) "Notificações" else "Notifications",
                        subtitle = if (notificationsEnabled) "Ativadas" else "Desativadas",
                        onClick = { showNotificationsDialog = true }
                    )
                    SettingsItem(
                        icon = Icons.Default.Lock,
                        title = "Privacidade",
                        subtitle = "Controlo de visibilidade",
                        onClick = { showPrivacyDialog = true }
                    )
                    SettingsItem(
                        icon = Icons.Default.Key,
                        title = "Alterar Palavra-passe",
                        subtitle = "Atualizar credenciais",
                        onClick = { showPasswordDialog = true }
                    )
                    SettingsItem(
                        icon = Icons.Default.Language,
                        title = "Idioma",
                        subtitle = if (currentLanguage == AppLanguage.PT) "Português (PT)" else "English (EN)",
                        onClick = { showLanguagePicker = true }
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                }

                // 5. Terminar Sessão
                item {
                    Button(
                        onClick = onLogout,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFEE2E2),
                            contentColor = Color(0xFF991B1B)
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Terminar Sessão", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PendingUserItem(
    user: ProfileDto,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 5.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = user.nome, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SportFlowDarkBlue)
                Text(text = user.email, fontSize = 11.sp, color = Color(0xFF64748B))
                Text(
                    text = "PEDIDO: ORGANIZADOR",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF2563EB),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Reject Button
                IconButton(
                    onClick = onReject,
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFFFEE2E2), CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Rejeitar", tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                }
                
                // Approve Button
                IconButton(
                    onClick = onApprove,
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFFDCFCE7), CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Aprovar", tint = Color(0xFF16A34A), modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8), letterSpacing = 1.sp)
    }
}

@Composable
fun AccountDataItem(icon: ImageVector, title: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 5.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(38.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFEFF6FF)), contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SportFlowDarkBlue)
                Text(text = value, fontSize = 12.sp, color = Color(0xFF64748B))
            }
        }
    }
}

@Composable
fun SettingsItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 5.dp).clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(38.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFEFF6FF)), contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SportFlowDarkBlue)
                Text(text = subtitle, fontSize = 11.sp, color = Color(0xFF64748B))
            }
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(20.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserProfileScreenPreview() {
    UserProfileScreen()
}
