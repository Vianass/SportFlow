package com.sportflow.app.ui.screens.organizador

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sportflow.app.R
import com.sportflow.app.model.AppLanguage
import com.sportflow.app.model.LocalLanguageViewModel
import com.sportflow.app.ui.components.ChangePasswordDialog
import com.sportflow.app.ui.components.LanguagePickerDialog
import com.sportflow.app.ui.components.NotificationsDialog
import com.sportflow.app.ui.components.OrganizadorPrivacyDialog
import com.sportflow.app.ui.theme.SportFlowDarkBlue
import com.sportflow.app.ui.theme.SportFlowGreen
import com.sportflow.app.ui.viewmodel.ProfileState
import com.sportflow.app.ui.viewmodel.ProfileViewModel

@Composable
fun OrganizadorProfileScreen(
    onLogout: () -> Unit = {},
    viewModel: ProfileViewModel = viewModel()
) {
    val langViewModel = LocalLanguageViewModel.current
    val currentLanguage by langViewModel.language.collectAsState()
    val notificationsEnabled by langViewModel.notificationsEnabled.collectAsState()
    val shareContact by langViewModel.shareContactWithAthletes.collectAsState()
    val context = LocalContext.current
    
    val profileState by viewModel.profileState.collectAsState()

    var showLanguagePicker by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showSupportDialog by remember { mutableStateOf(false) }
    var showOrgProfileDialog by remember { mutableStateOf(false) }
    var showOrgDataDialog by remember { mutableStateOf(false) }

    // Estados locais para os dados (Nome e Email virão do Supabase)
    var orgUserName by remember { mutableStateOf("Hugo Carvalho") }
    var orgUserEmail by remember { mutableStateOf("Hugo Carvalho@ESTG.pt") }
    
    // Outros dados continuam mockados conforme solicitado
    var orgUserBio by remember { mutableStateOf("Organizador de torneios de futsal na ESTG.") }
    var orgName by remember { mutableStateOf("AE - IPCV-ESTG") }
    var orgNif by remember { mutableStateOf("500 123 456") }
    var orgAddress by remember { mutableStateOf("Avenida do Atlântico, Viana do Castelo") }
    var orgContact by remember { mutableStateOf("258 111 222") }

    // Atualiza Nome e Email quando o perfil for carregado do Supabase
    LaunchedEffect(profileState) {
        if (profileState is ProfileState.Success) {
            val profile = (profileState as ProfileState.Success).profile
            orgUserName = profile.nome
            orgUserEmail = profile.email
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

    if (showPrivacyDialog) {
        OrganizadorPrivacyDialog(
            currentLanguage = currentLanguage,
            shareContact = shareContact,
            onShareContactToggle = { langViewModel.setShareContactWithAthletes(it, context) },
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

    if (showSupportDialog) {
        com.sportflow.app.ui.components.SupportDialog(
            onDismiss = { showSupportDialog = false }
        )
    }

    if (showOrgProfileDialog) {
        com.sportflow.app.ui.components.EditOrgProfileDialog(
            initialName = orgUserName,
            initialEmail = orgUserEmail,
            initialBio = orgUserBio,
            onSave = { name, email, bio ->
                orgUserName = name
                orgUserEmail = email
                orgUserBio = bio
                showOrgProfileDialog = false
            },
            onDismiss = { showOrgProfileDialog = false }
        )
    }

    if (showOrgDataDialog) {
        com.sportflow.app.ui.components.EditOrgDataDialog(
            initialOrgName = orgName,
            initialNif = orgNif,
            initialAddress = orgAddress,
            initialContact = orgContact,
            onSave = { name, nif, address, contact ->
                orgName = name
                orgNif = nif
                orgAddress = address
                orgContact = contact
                showOrgDataDialog = false
            },
            onDismiss = { showOrgDataDialog = false }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF8FAFC)),
                    contentPadding = PaddingValues(top = 28.dp, bottom = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Profile Header Section (Avatar, Name, Details)
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Profile picture with green check mark badge
                            Box(
                                modifier = Modifier.size(110.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.organizer_profile_pic),
                                    contentDescription = "Foto de Perfil",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(24.dp))
                                )
                                
                                // Verification green check badge
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF10B981))
                                        .border(2.dp, Color.White, CircleShape)
                                        .align(Alignment.BottomEnd),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "ORGANIZADOR",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF16A34A),
                                letterSpacing = 1.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = orgUserName,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = SportFlowDarkBlue
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = orgUserEmail,
                                fontSize = 13.sp,
                                color = Color(0xFF64748B)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Organization Badge
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFEFF6FF))
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Business,
                                    contentDescription = null,
                                    tint = Color(0xFF2563EB),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = orgName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2563EB)
                                )
                            }
                        }
                    }

                    // Metrics Section
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Metric 1: Torneios Ativos
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = SportFlowDarkBlue)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 18.dp)
                                ) {
                                    Text(
                                        text = "TORNEIOS ATIVOS",
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF94A3B8),
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "08",
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF67FF9A)
                                    )
                                }
                            }

                            // Metric 2: Atletas Inscritos
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 18.dp)
                                ) {
                                    Text(
                                        text = "ATLETAS INSCRITOS",
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF64748B),
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "1,240",
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.Black,
                                        color = SportFlowDarkBlue
                                    )
                                }
                            }
                        }
                    }

                    // Configurations Section 1: CONFIGURAÇÕES DE CONTA
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                        ) {
                            Text(
                                text = "CONFIGURAÇÕES DE CONTA",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF94A3B8),
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                ProfileSettingItem(
                                    icon = Icons.Default.Person,
                                    title = "Editar Perfil",
                                    subtitle = "Alterar nome, foto e bio",
                                    onClick = { showOrgProfileDialog = true }
                                )
                                ProfileSettingItem(
                                    icon = Icons.Default.Business,
                                    title = "Dados da Organização",
                                    subtitle = "NIF, Morada e Contactos",
                                    onClick = { showOrgDataDialog = true }
                                )
                                ProfileSettingItem(
                                    icon = Icons.Default.Notifications,
                                    title = if (currentLanguage == AppLanguage.PT) "Notificações" else "Notifications",
                                    subtitle = if (notificationsEnabled)
                                        (if (currentLanguage == AppLanguage.PT) "Ativadas" else "Enabled")
                                    else
                                        (if (currentLanguage == AppLanguage.PT) "Desativadas" else "Disabled"),
                                    onClick = { showNotificationsDialog = true }
                                )
                            }
                        }
                    }

                    // Configurations Section 2: SEGURANÇA E SUPORTE
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                        ) {
                            Text(
                                text = "SEGURANÇA E SUPORTE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF94A3B8),
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                ProfileSettingItem(
                                    icon = Icons.Default.Lock,
                                    title = if (currentLanguage == AppLanguage.PT) "Privacidade" else "Privacy",
                                    subtitle = if (currentLanguage == AppLanguage.PT) "Visibilidade de dados" else "Data visibility",
                                    onClick = { showPrivacyDialog = true }
                                )
                                ProfileSettingItem(
                                    icon = Icons.Default.Shield,
                                    title = if (currentLanguage == AppLanguage.PT) "Segurança" else "Security",
                                    subtitle = "Password e 2FA",
                                    onClick = { showPasswordDialog = true }
                                )
                                ProfileSettingItem(
                                    icon = Icons.Default.Headset,
                                    title = "Centro de Ajuda",
                                    subtitle = "Falar com o suporte",
                                    onClick = { showSupportDialog = true }
                                )
                                ProfileSettingItem(
                                    icon = Icons.Default.Language,
                                    title = if (currentLanguage == AppLanguage.PT) "Selecione o seu Idioma" else "Select your Language",
                                    subtitle = if (currentLanguage == AppLanguage.PT) "Português (PT)" else "English (EN)",
                                    onClick = { showLanguagePicker = true }
                                )
                            }
                        }
                    }

                    // Logout Button
                    item {
                        Button(
                            onClick = onLogout,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFEE2E2),
                                contentColor = Color(0xFF991B1B)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .height(48.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ExitToApp,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Terminar Sessão",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileSettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(0.5.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon box
            Box(
                modifier = Modifier
                    .size(36.dp)
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

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = SportFlowDarkBlue
                )
                Text(
                    text = subtitle,
                    fontSize = 10.5.sp,
                    color = Color(0xFF64748B)
                )
            }

            // Arrow Chevron
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFFCBD5E1),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrganizadorProfileScreenPreview() {
    OrganizadorProfileScreen()
}
