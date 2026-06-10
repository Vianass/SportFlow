package com.sportflow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sportflow.app.model.AppLanguage
import com.sportflow.app.ui.theme.SportFlowDarkBlue
import com.sportflow.app.ui.theme.SportFlowGreen

@Composable
fun ChangePasswordDialog(
    currentLanguage: AppLanguage,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val isPT = currentLanguage == AppLanguage.PT

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

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
                            text = if (isPT) "Alterar Palavra-passe" else "Change Password",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = SportFlowDarkBlue
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isPT) "Atualiza as tuas credenciais de acesso"
                                   else "Update your access credentials",
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

                // Current Password
                PasswordTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = if (isPT) "Palavra-passe atual" else "Current password",
                    isVisible = currentPasswordVisible,
                    onVisibilityChange = { currentPasswordVisible = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // New Password
                PasswordTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = if (isPT) "Nova palavra-passe" else "New password",
                    isVisible = newPasswordVisible,
                    onVisibilityChange = { newPasswordVisible = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Confirm Password
                PasswordTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = if (isPT) "Confirmar nova palavra-passe" else "Confirm new password",
                    isVisible = confirmPasswordVisible,
                    onVisibilityChange = { confirmPasswordVisible = it }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Save Button
                Button(
                    onClick = {
                        onSave()
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SportFlowGreen),
                    shape = RoundedCornerShape(12.dp),
                    enabled = currentPassword.isNotEmpty() && newPassword.isNotEmpty() && newPassword == confirmPassword
                ) {
                    Text(
                        text = if (isPT) "GUARDAR ALTERAÇÕES" else "SAVE CHANGES",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isVisible: Boolean,
    onVisibilityChange: (Boolean) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color(0xFF64748B)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = SportFlowGreen,
            unfocusedBorderColor = Color(0xFFE2E8F0),
            focusedLabelColor = SportFlowGreen,
            cursorColor = SportFlowGreen
        ),
        shape = RoundedCornerShape(12.dp),
        trailingIcon = {
            val image = if (isVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            val description = if (isVisible) "Hide password" else "Show password"

            IconButton(onClick = { onVisibilityChange(!isVisible) }) {
                Icon(imageVector = image, contentDescription = description, tint = Color(0xFF94A3B8))
            }
        }
    )
}

@Preview
@Composable
private fun ChangePasswordDialogPreview() {
    ChangePasswordDialog(
        currentLanguage = AppLanguage.PT,
        onSave = {},
        onDismiss = {}
    )
}
