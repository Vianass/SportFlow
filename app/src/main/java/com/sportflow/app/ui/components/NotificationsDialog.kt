package com.sportflow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
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

@Composable
fun NotificationsDialog(
    enabled: Boolean,
    currentLanguage: AppLanguage,
    onToggle: (Boolean) -> Unit,
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = if (isPT) "Notificações" else "Notifications",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = SportFlowDarkBlue
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isPT) "Gere as tuas preferências de alerta"
                                   else "Manage your alert preferences",
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

                // ON Option
                NotificationOption(
                    icon = Icons.Default.Notifications,
                    iconBg = Color(0xFFECFDF5),
                    iconTint = SportFlowGreen,
                    title = if (isPT) "Ativadas" else "Enabled",
                    subtitle = if (isPT) "Recebe alertas de novos eventos e atualizações"
                               else "Receive alerts for new events and updates",
                    isSelected = enabled,
                    onClick = {
                        onToggle(true)
                        onDismiss()
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // OFF Option
                NotificationOption(
                    icon = Icons.Default.NotificationsOff,
                    iconBg = Color(0xFFFEF2F2),
                    iconTint = Color(0xFFEF4444),
                    title = if (isPT) "Desativadas" else "Disabled",
                    subtitle = if (isPT) "Não receberes nenhuma notificação da app"
                               else "You won't receive any app notifications",
                    isSelected = !enabled,
                    onClick = {
                        onToggle(false)
                        onDismiss()
                    }
                )
            }
        }
    }
}

@Composable
private fun NotificationOption(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) SportFlowGreen else Color(0xFFE2E8F0)
    val bgColor = if (isSelected) Color(0xFFECFDF5) else Color.White

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
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
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
                color = Color(0xFF64748B),
                lineHeight = 15.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Check circle
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(SportFlowGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, Color(0xFFE2E8F0), CircleShape)
                    .background(Color(0xFFF8FAFC))
            )
        }
    }
}

@Preview
@Composable
private fun NotificationsDialogPreview() {
    NotificationsDialog(
        enabled = true,
        currentLanguage = AppLanguage.PT,
        onToggle = {},
        onDismiss = {}
    )
}
