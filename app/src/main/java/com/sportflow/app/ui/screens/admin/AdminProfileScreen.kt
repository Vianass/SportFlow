package com.sportflow.app.ui.screens.admin

import androidx.compose.foundation.Image
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sportflow.app.R
import com.sportflow.app.data.remote.dto.ProfileDto
import com.sportflow.app.model.AppLanguage
import com.sportflow.app.model.LocalLanguageViewModel
import com.sportflow.app.model.ProfileStatus
import com.sportflow.app.ui.components.ChangePasswordDialog
import com.sportflow.app.ui.components.LanguagePickerDialog
import com.sportflow.app.ui.components.NotificationsDialog
import com.sportflow.app.ui.components.PaymentDialog
import com.sportflow.app.ui.screens.user.AccountDataItem
import com.sportflow.app.ui.screens.user.SectionTitle
import com.sportflow.app.ui.screens.user.SettingsItem
import com.sportflow.app.ui.theme.SportFlowDarkBlue
import com.sportflow.app.ui.theme.SportFlowGreen
import com.sportflow.app.ui.viewmodel.AdminViewModel
import com.sportflow.app.ui.viewmodel.ProfileState
import com.sportflow.app.ui.viewmodel.ProfileViewModel

@Composable
fun AdminProfileScreen(
    onLogout: () -> Unit = {},
    viewModel: ProfileViewModel = viewModel(),
    adminViewModel: AdminViewModel = viewModel()
) {
    val langViewModel = LocalLanguageViewModel.current
    val currentLanguage by langViewModel.language.collectAsState()
    val notificationsEnabled by langViewModel.notificationsEnabled.collectAsState()
    val context = LocalContext.current
    val profileState by viewModel.profileState.collectAsState()
    val adminState by adminViewModel.uiState.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }

    var showLanguagePicker by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showPaymentDialog by remember { mutableStateOf(false) }

    LaunchedEffect(adminState.successMessage) {
        adminState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            adminViewModel.clearSuccess()
        }
    }

    LaunchedEffect(adminState.errorMessage) {
        adminState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            adminViewModel.clearError()
        }
    }

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

    if (showPasswordDialog) {
        ChangePasswordDialog(
            currentLanguage = currentLanguage,
            onSave = { /* Handle save password */ },
            onDismiss = { showPasswordDialog = false }
        )
    }

    if (showPaymentDialog) {
        PaymentDialog(
            currentLanguage = currentLanguage,
            onDismiss = { showPaymentDialog = false }
        )
    }

    Scaffold(
        snackbarHost = { 
            SnackbarHost(snackbarHostState) { data ->
                Surface(
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = SportFlowDarkBlue,
                    tonalElevation = 6.dp,
                    shadowElevation = 8.dp,
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.White.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.sportflow_logo),
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color.White.copy(alpha = 0.1f), CircleShape)
                                .padding(4.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = data.visuals.message,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (profileState) {
                is ProfileState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = SportFlowGreen)
                    }
                }
                is ProfileState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(imageVector = Icons.Default.ErrorOutline, contentDescription = null, tint = Color.Red, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = (profileState as ProfileState.Error).message, color = Color.Red, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = onLogout,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2), contentColor = Color(0xFF991B1B)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Voltar ao Login")
                        }
                    }
                }
                is ProfileState.Success -> {
                    val profile = (profileState as ProfileState.Success).profile
                    
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 24.dp, bottom = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 1. Profile Avatar & Basic Info Card
                        item {
                            Box(modifier = Modifier.size(130.dp).padding(4.dp)) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(Brush.verticalGradient(listOf(Color(0xFF475569), SportFlowDarkBlue)))
                                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(24.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color.White.copy(alpha = 0.9f), modifier = Modifier.size(75.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = "ADMIN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A), letterSpacing = 0.5.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = profile.nome, fontSize = 28.sp, fontWeight = FontWeight.Black, color = SportFlowDarkBlue)
                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        // 2. Pendidos de Aprovação (ADMIN EXCLUSIVE)
                        if (adminState.pendingOrganizers.isNotEmpty()) {
                            item { SectionTitle(title = "APROVAÇÕES PENDENTES") }
                            items(adminState.pendingOrganizers, key = { it.id }) { pendingUser ->
                                AdminPendingOrganizerItem(
                                    user = pendingUser,
                                    operationInProgress = adminState.operationInProgress,
                                    onApprove = { adminViewModel.approveOrganizer(pendingUser.id) },
                                    onReject = { adminViewModel.rejectOrganizer(pendingUser.id) }
                                )
                            }
                            item { Spacer(modifier = Modifier.height(16.dp)) }
                        }

                        if (adminState.users.isNotEmpty()) {
                            item { SectionTitle(title = "UTILIZADORES") }
                            items(adminState.users, key = { it.id }) { user ->
                                AdminUserItem(
                                    user = user,
                                    isCurrentAdmin = user.id == profile.id,
                                    operationInProgress = adminState.operationInProgress,
                                    onBlock = { adminViewModel.blockUser(user.id) },
                                    onUnblock = { adminViewModel.unblockUser(user.id) }
                                )
                            }
                            item { Spacer(modifier = Modifier.height(16.dp)) }
                        }

                        // 3. DADOS DA CONTA
                        item {
                            SectionTitle(title = "DADOS DA CONTA")
                            AccountDataItem(icon = Icons.Default.Email, title = "Email", value = profile.email)
                            profile.metodoPagamento?.let { AccountDataItem(icon = Icons.Default.CreditCard, title = "Método de Pagamento", value = it) }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // 4. CONFIGURAÇÕES
                        item {
                            SectionTitle(title = "CONFIGURAÇÕES")
                            SettingsItem(icon = Icons.Default.Notifications, title = "Notificações", subtitle = if (notificationsEnabled) "Ativadas" else "Desativadas", onClick = { showNotificationsDialog = true })
                            SettingsItem(icon = Icons.Default.Key, title = "Alterar Palavra-passe", subtitle = "Atualizar credenciais", onClick = { showPasswordDialog = true })
                            SettingsItem(icon = Icons.Default.Language, title = "Idioma", subtitle = if (currentLanguage == AppLanguage.PT) "Português (PT)" else "English (EN)", onClick = { showLanguagePicker = true })
                            Spacer(modifier = Modifier.height(28.dp))
                        }

                        // 5. Terminar Sessão
                        item {
                            Button(
                                onClick = onLogout,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2), contentColor = Color(0xFF991B1B))
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
    }
}

@Composable
private fun AdminPendingOrganizerItem(
    user: ProfileDto,
    operationInProgress: String?,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    val isBusy = operationInProgress?.endsWith(":${user.id}") == true
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 5.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(user.nome, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SportFlowDarkBlue)
                Text(user.email, fontSize = 11.sp, color = Color(0xFF64748B))
                Text("PEDIDO: ORGANIZADOR", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color(0xFF2563EB))
            }
            if (isBusy) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            } else {
                IconButton(onClick = onReject, enabled = operationInProgress == null) {
                    Icon(Icons.Default.Close, contentDescription = "Rejeitar", tint = Color(0xFFDC2626))
                }
                IconButton(onClick = onApprove, enabled = operationInProgress == null) {
                    Icon(Icons.Default.Check, contentDescription = "Aprovar", tint = Color(0xFF16A34A))
                }
            }
        }
    }
}

@Composable
private fun AdminUserItem(
    user: ProfileDto,
    isCurrentAdmin: Boolean,
    operationInProgress: String?,
    onBlock: () -> Unit,
    onUnblock: () -> Unit
) {
    val isBlocked = user.status == ProfileStatus.BLOQUEADO
    val canChangeStatus = !isCurrentAdmin && !user.papel.equals("ADMIN", ignoreCase = true)
    val isBusy = operationInProgress?.endsWith(":${user.id}") == true

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 5.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(user.nome, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SportFlowDarkBlue)
                Text(user.email, fontSize = 11.sp, color = Color(0xFF64748B))
                Text(
                    "${user.papel} · ${user.estado}",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isBlocked) Color(0xFFDC2626) else Color(0xFF16A34A)
                )
            }
            when {
                isBusy -> CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                canChangeStatus -> IconButton(
                    onClick = if (isBlocked) onUnblock else onBlock,
                    enabled = operationInProgress == null
                ) {
                    Icon(
                        imageVector = if (isBlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                        contentDescription = if (isBlocked) "Desbloquear" else "Bloquear",
                        tint = if (isBlocked) Color(0xFF16A34A) else Color(0xFFDC2626)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminProfileScreenPreview() {
    AdminProfileScreen()
}
