package com.sportflow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sportflow.app.ui.theme.SportFlowDarkBlue
import com.sportflow.app.ui.theme.SportFlowGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditOrgDataDialog(
    initialOrgName: String,
    initialNif: String,
    initialAddress: String,
    initialContact: String,
    onSave: (orgName: String, nif: String, address: String, contact: String) -> Unit,
    onDismiss: () -> Unit
) {
    var orgName by remember { mutableStateOf(initialOrgName) }
    var nif by remember { mutableStateOf(initialNif) }
    var address by remember { mutableStateOf(initialAddress) }
    var contact by remember { mutableStateOf(initialContact) }

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
                // Header
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFFEFF6FF), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Business,
                        contentDescription = null,
                        tint = Color(0xFF3B82F6),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Dados da Organização",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = SportFlowDarkBlue
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Atualize as informações legais e de contacto da sua organização.",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Input Fields
                OutlinedTextField(
                    value = orgName,
                    onValueChange = { orgName = it },
                    label = { Text("Nome da Organização", fontSize = 12.sp) },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, tint = Color(0xFF94A3B8)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SportFlowDarkBlue,
                        unfocusedTextColor = SportFlowDarkBlue,
                        focusedBorderColor = SportFlowGreen,
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedLabelColor = SportFlowGreen
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = nif,
                    onValueChange = { nif = it },
                    label = { Text("NIF", fontSize = 12.sp) },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null, tint = Color(0xFF94A3B8)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SportFlowDarkBlue,
                        unfocusedTextColor = SportFlowDarkBlue,
                        focusedBorderColor = SportFlowGreen,
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedLabelColor = SportFlowGreen
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Morada (Sede)", fontSize = 12.sp) },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null, tint = Color(0xFF94A3B8)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SportFlowDarkBlue,
                        unfocusedTextColor = SportFlowDarkBlue,
                        focusedBorderColor = SportFlowGreen,
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedLabelColor = SportFlowGreen
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = contact,
                    onValueChange = { contact = it },
                    label = { Text("Contacto Principal", fontSize = 12.sp) },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF94A3B8)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SportFlowDarkBlue,
                        unfocusedTextColor = SportFlowDarkBlue,
                        focusedBorderColor = SportFlowGreen,
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedLabelColor = SportFlowGreen
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "CANCELAR",
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                    Button(
                        onClick = { onSave(orgName, nif, address, contact) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SportFlowGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "GUARDAR",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditOrgDataDialogPreview() {
    EditOrgDataDialog(
        initialOrgName = "AE - IPCV-ESTG",
        initialNif = "500 123 456",
        initialAddress = "Avenida do Atlântico, Viana do Castelo",
        initialContact = "258 111 222",
        onSave = { _, _, _, _ -> },
        onDismiss = {}
    )
}
