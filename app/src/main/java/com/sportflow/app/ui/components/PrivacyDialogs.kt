package com.sportflow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import com.sportflow.app.ui.localization.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sportflow.app.model.AppLanguage
import com.sportflow.app.ui.theme.SportFlowDarkBlue
import com.sportflow.app.ui.theme.SportFlowGreen

// ── Atleta Privacy Dialog (3 opções) ─────────────────────────────────────────

@Composable
fun UserPrivacyDialog(
    currentLanguage: AppLanguage,
    profilePublic: Boolean,
    showInRankings: Boolean,
    locationEnabled: Boolean,
    onProfilePublicToggle: (Boolean) -> Unit,
    onShowInRankingsToggle: (Boolean) -> Unit,
    onLocationToggle: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val isPT = currentLanguage == AppLanguage.PT

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Color(0xFFF8FAFC))
                    .clickable(enabled = false) {}
                    .padding(bottom = 36.dp)
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
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = if (isPT) "Privacidade" else "Privacy",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = SportFlowDarkBlue
                        )
                        Text(
                            text = if (isPT) "Controla a tua visibilidade na app"
                                   else "Control your visibility in the app",
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
                            contentDescription = null,
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Toggle Row: Perfil Público
                PrivacyToggleRow(
                    icon = Icons.Default.Person,
                    iconBg = Color(0xFFEFF6FF),
                    iconTint = Color(0xFF2563EB),
                    title = if (isPT) "Perfil Público" else "Public Profile",
                    subtitle = if (isPT) "Outros atletas podem ver o teu perfil"
                               else "Other athletes can see your profile",
                    checked = profilePublic,
                    onToggle = onProfilePublicToggle
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Toggle Row: Rankings
                PrivacyToggleRow(
                    icon = Icons.Default.EmojiEvents,
                    iconBg = Color(0xFFFFFBEB),
                    iconTint = Color(0xFFF59E0B),
                    title = if (isPT) "Aparecer em Rankings" else "Show in Rankings",
                    subtitle = if (isPT) "O teu nome aparece nas classificações públicas"
                               else "Your name appears in public leaderboards",
                    checked = showInRankings,
                    onToggle = onShowInRankingsToggle
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Toggle Row: Localização
                PrivacyToggleRow(
                    icon = Icons.Default.LocationOn,
                    iconBg = Color(0xFFECFDF5),
                    iconTint = SportFlowGreen,
                    title = if (isPT) "Localização" else "Location",
                    subtitle = if (isPT) "Permitir que a app use a tua localização"
                               else "Allow the app to use your location",
                    checked = locationEnabled,
                    onToggle = onLocationToggle
                )
            }
        }
    }
}

// ── Organizador Privacy Dialog (1 opção) ─────────────────────────────────────

@Composable
fun OrganizadorPrivacyDialog(
    currentLanguage: AppLanguage,
    shareContact: Boolean,
    onShareContactToggle: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val isPT = currentLanguage == AppLanguage.PT

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Color(0xFFF8FAFC))
                    .clickable(enabled = false) {}
                    .padding(bottom = 36.dp)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 12.dp)
                        .size(width = 40.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFFCBD5E1))
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = if (isPT) "Privacidade" else "Privacy",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = SportFlowDarkBlue
                        )
                        Text(
                            text = if (isPT) "Visibilidade dos teus dados de contacto"
                                   else "Visibility of your contact data",
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
                        Icon(Icons.Default.Close, null, tint = Color(0xFF64748B), modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                PrivacyToggleRow(
                    icon = Icons.Default.Person,
                    iconBg = Color(0xFFEFF6FF),
                    iconTint = Color(0xFF2563EB),
                    title = if (isPT) "Partilhar Contacto" else "Share Contact Info",
                    subtitle = if (isPT) "Atletas inscritos nos teus eventos podem ver o teu email e telemóvel"
                               else "Athletes registered in your events can see your email and phone",
                    checked = shareContact,
                    onToggle = onShareContactToggle
                )
            }
        }
    }
}

// ── Shared toggle row component ───────────────────────────────────────────────

@Composable
private fun PrivacyToggleRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
            .clickable { onToggle(!checked) }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SportFlowDarkBlue)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, fontSize = 11.sp, color = Color(0xFF64748B), lineHeight = 15.sp)
        }

        Spacer(modifier = Modifier.width(8.dp))

        Switch(
            checked = checked,
            onCheckedChange = { onToggle(it) },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = SportFlowGreen,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFCBD5E1)
            )
        )
    }
}

@Preview
@Composable
private fun UserPrivacyDialogPreview() {
    UserPrivacyDialog(
        currentLanguage = AppLanguage.PT,
        profilePublic = true,
        showInRankings = true,
        locationEnabled = false,
        onProfilePublicToggle = {},
        onShowInRankingsToggle = {},
        onLocationToggle = {},
        onDismiss = {}
    )
}

@Preview
@Composable
private fun OrganizadorPrivacyDialogPreview() {
    OrganizadorPrivacyDialog(
        currentLanguage = AppLanguage.PT,
        shareContact = true,
        onShareContactToggle = {},
        onDismiss = {}
    )
}
