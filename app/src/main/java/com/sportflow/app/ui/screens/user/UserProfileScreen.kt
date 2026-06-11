package com.sportflow.app.ui.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.sportflow.app.model.AppLanguage
import com.sportflow.app.model.LocalLanguageViewModel
import com.sportflow.app.ui.components.ChangePasswordDialog
import com.sportflow.app.ui.components.LanguagePickerDialog
import com.sportflow.app.ui.components.NotificationsDialog
import com.sportflow.app.ui.components.PaymentDialog
import com.sportflow.app.ui.components.UserPrivacyDialog
import com.sportflow.app.ui.theme.SportFlowDarkBlue
import com.sportflow.app.ui.theme.SportFlowGreen
import com.sportflow.app.ui.viewmodel.ProfileState
import com.sportflow.app.ui.viewmodel.ProfileViewModel

@Composable
fun UserProfileScreen(
    onLogout: () -> Unit = {},
    viewModel: ProfileViewModel = viewModel()
) {
    val langViewModel = LocalLanguageViewModel.current
    val currentLanguage by langViewModel.language.collectAsState()
    val notificationsEnabled by langViewModel.notificationsEnabled.collectAsState()
    val profilePublic by langViewModel.profilePublic.collectAsState()
    val showInRankings by langViewModel.showInRankings.collectAsState()
    val locationEnabled by langViewModel.locationEnabled.collectAsState()
    val context = LocalContext.current
    
    val profileState by viewModel.profileState.collectAsState()

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
                        // Avatar Card Box
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(24.dp))
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color(0xFF3B82F6),
                                            SportFlowDarkBlue
                                        )
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

                    // User Profile Badges & Name
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

                // 2. DADOS DA CONTA (Account Data) Section
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

                // 3. CONFIGURAÇÕES (Settings) Section
                item {
                    SectionTitle(title = "CONFIGURAÇÕES")

                    SettingsItem(
                        icon = Icons.Default.Notifications,
                        title = if (currentLanguage == AppLanguage.PT) "Notificações" else "Notifications",
                        subtitle = if (notificationsEnabled)
                            (if (currentLanguage == AppLanguage.PT) "Ativadas" else "Enabled")
                        else
                            (if (currentLanguage == AppLanguage.PT) "Desativadas" else "Disabled"),
                        onClick = { showNotificationsDialog = true }
                    )
                    SettingsItem(
                        icon = Icons.Default.Lock,
                        title = if (currentLanguage == AppLanguage.PT) "Privacidade" else "Privacy",
                        subtitle = if (currentLanguage == AppLanguage.PT) "Controlo de visibilidade" else "Visibility control",
                        onClick = { showPrivacyDialog = true }
                    )
                    SettingsItem(
                        icon = Icons.Default.Key,
                        title = if (currentLanguage == AppLanguage.PT) "Alterar Palavra-passe" else "Change Password",
                        subtitle = if (currentLanguage == AppLanguage.PT) "Atualizar credenciais" else "Update credentials",
                        onClick = { showPasswordDialog = true }
                    )
                    SettingsItem(
                        icon = Icons.Default.Language,
                        title = if (currentLanguage == AppLanguage.PT) "Selecione o seu Idioma" else "Select your Language",
                        subtitle = if (currentLanguage == AppLanguage.PT) "Português (PT)" else "English (EN)",
                        onClick = { showLanguagePicker = true }
                    )

                    Spacer(modifier = Modifier.height(28.dp))
                }

                // 4. Terminar Sessão (Sign Out) Button
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Sair",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Terminar Sessão",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
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
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF94A3B8),
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun AccountDataItem(
    icon: ImageVector,
    title: String,
    value: String
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
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFEFF6FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF2563EB),
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = SportFlowDarkBlue
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = value,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 5.dp)
            .clickable { onClick() },
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
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFEFF6FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF2563EB),
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
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

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Abrir",
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserProfileScreenPreview() {
    UserProfileScreen()
}
