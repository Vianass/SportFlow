package com.sportflow.app.ui.components

import com.sportflow.app.ui.localization.localizedText

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import com.sportflow.app.ui.localization.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
fun LanguagePickerDialog(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    onDismiss: () -> Unit
) {
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
            // Light bottom sheet panel
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

                // Header row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = if (currentLanguage == AppLanguage.PT) "Idioma da App" else "App Language",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = SportFlowDarkBlue
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (currentLanguage == AppLanguage.PT)
                                "Escolhe o idioma para toda a aplicação"
                            else
                                "Choose the language for the entire app",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    // Close button
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
                            contentDescription = localizedText("Fechar"),
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // PT Option
                LanguageOption(
                    flag = "🇵🇹",
                    name = "Português",
                    tag = "PT",
                    isSelected = currentLanguage == AppLanguage.PT,
                    onClick = {
                        onLanguageSelected(AppLanguage.PT)
                        onDismiss()
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // EN Option
                LanguageOption(
                    flag = "🇬🇧",
                    name = "English",
                    tag = "EN",
                    isSelected = currentLanguage == AppLanguage.EN,
                    onClick = {
                        onLanguageSelected(AppLanguage.EN)
                        onDismiss()
                    }
                )
            }
        }
    }
}

@Composable
private fun LanguageOption(
    flag: String,
    name: String,
    tag: String,
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
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Flag emoji
        Text(
            text = flag,
            fontSize = 28.sp
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = SportFlowDarkBlue
            )
            Text(
                text = tag,
                fontSize = 11.sp,
                color = Color(0xFF94A3B8),
                letterSpacing = 1.sp
            )
        }

        // Check circle
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(SportFlowGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, Color(0xFFE2E8F0), CircleShape)
                    .background(Color(0xFFF8FAFC))
            )
        }
    }
}

@Preview
@Composable
private fun LanguagePickerDialogPreview() {
    LanguagePickerDialog(
        currentLanguage = AppLanguage.PT,
        onLanguageSelected = {},
        onDismiss = {}
    )
}
