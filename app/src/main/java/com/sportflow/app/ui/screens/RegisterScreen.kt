package com.sportflow.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sportflow.app.Perfil
import com.sportflow.app.R
import com.sportflow.app.supabase
import com.sportflow.app.ui.theme.SportFlowDarkBlue
import com.sportflow.app.ui.theme.SportFlowGreen
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit, onNavigateToLogin: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var selectedUserType by remember { mutableStateOf("ATLETA") } // "ATLETA", "ORGANIZADOR", "ADMIN"
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFEFF6FF), // Soft light blue-gray
                            Color(0xFFF8FAFC)
                        )
                    )
                )
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // White Card Container representing the UI mockup
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(12.dp, RoundedCornerShape(32.dp)),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(0.5.dp, Color(0xFFE2E8F0))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 1. Logo Header Section
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.sportflowlogo),
                                contentDescription = "SportFlow Logo",
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(Color(0xFFEFF6FF), CircleShape)
                                    .padding(2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SPORTFLOW",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = SportFlowDarkBlue,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // 2. Headings
                        Text(
                            text = "Criar a sua\nconta",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = SportFlowDarkBlue,
                            textAlign = TextAlign.Center,
                            lineHeight = 32.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Registe-se e comece a gerir o seu torneio",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // 3. Segmented Role Selector ("TIPO DE UTILIZADOR")
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "TIPO DE UTILIZADOR",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B),
                                letterSpacing = 0.5.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Tab container
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFEFF6FF))
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                val roles = listOf(
                                    Triple("ATLETA", Icons.Default.Person, "ATLETA"),
                                    Triple("ORGANIZADOR", Icons.Default.Groups, "ORGANIZADOR"),
                                    Triple("ADMIN", Icons.Default.Shield, "ADMIN")
                                )

                                roles.forEach { (label, icon, type) ->
                                    val isSelected = selectedUserType == type
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) Color.White else Color.Transparent)
                                            .clickable { selectedUserType = type }
                                            .padding(vertical = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = null,
                                                tint = if (isSelected) Color(0xFF047857) else Color(0xFF64748B),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = label,
                                                fontSize = 7.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color(0xFF047857) else Color(0xFF64748B)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // 4. Name Input Field
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "NOME COMPLETO",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B),
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it; errorMessage = "" },
                                placeholder = { Text("O seu nome completo", color = Color(0xFF94A3B8)) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = Color(0xFF64748B)
                                    )
                                },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = SportFlowDarkBlue,
                                    unfocusedTextColor = SportFlowDarkBlue,
                                    focusedBorderColor = Color(0xFF047857),
                                    unfocusedBorderColor = Color(0xFFE2E8F0),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // 5. Email Input Field
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "E-MAIL",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B),
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it; errorMessage = "" },
                                placeholder = { Text("nome@exemplo.com", color = Color(0xFF94A3B8)) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = null,
                                        tint = Color(0xFF64748B)
                                    )
                                },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = SportFlowDarkBlue,
                                    unfocusedTextColor = SportFlowDarkBlue,
                                    focusedBorderColor = Color(0xFF047857),
                                    unfocusedBorderColor = Color(0xFFE2E8F0),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // 6. Password Input Field
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "PALAVRA-PASSE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B),
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it; errorMessage = "" },
                                placeholder = { Text("••••••••", color = Color(0xFF94A3B8)) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = Color(0xFF64748B)
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = if (passwordVisible) "Ocultar" else "Mostrar",
                                            tint = Color(0xFF64748B)
                                        )
                                    }
                                },
                                singleLine = true,
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = SportFlowDarkBlue,
                                    unfocusedTextColor = SportFlowDarkBlue,
                                    focusedBorderColor = Color(0xFF047857),
                                    unfocusedBorderColor = Color(0xFFE2E8F0),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // 7. Confirm Password Input Field
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "CONFIRMAR PALAVRA-PASSE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B),
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it; errorMessage = "" },
                                placeholder = { Text("••••••••", color = Color(0xFF94A3B8)) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = Color(0xFF64748B)
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                        Icon(
                                            imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = if (confirmPasswordVisible) "Ocultar" else "Mostrar",
                                            tint = Color(0xFF64748B)
                                        )
                                    }
                                },
                                singleLine = true,
                                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = SportFlowDarkBlue,
                                    unfocusedTextColor = SportFlowDarkBlue,
                                    focusedBorderColor = Color(0xFF047857),
                                    unfocusedBorderColor = Color(0xFFE2E8F0),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (errorMessage.isNotEmpty()) {
                            Text(
                                text = errorMessage,
                                color = Color.Red,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 8. REGISTAR NA ARENA Button
                        Button(
                            onClick = {
                                when {
                                    name.isBlank() || email.isBlank() -> errorMessage = "Por favor, preencha todos os campos"
                                    password != confirmPassword -> errorMessage = "As palavra-passes não coincidem"
                                    password.length < 6 -> errorMessage = "Mínimo 6 caracteres"
                                    else -> {
                                        isLoading = true
                                        scope.launch {
                                            try {
                                                withTimeout(20_000L) {
                                                    supabase.auth.signUpWith(Email) {
                                                        this.email = email
                                                        this.password = password
                                                        this.data = buildJsonObject {
                                                            put("nome", name)
                                                            put("full_name", name)
                                                            put("papel", selectedUserType)
                                                        }
                                                    }
                                                }

                                                snackbarHostState.showSnackbar(
                                                    "Conta criada com sucesso. Se necessario, confirme o e-mail para concluir o acesso."
                                                )
                                                onRegisterSuccess()
                                            } catch (_: TimeoutCancellationException) {
                                                errorMessage = "O servidor demorou demasiado a responder. Tente novamente."
                                                snackbarHostState.showSnackbar(errorMessage)
                                            } catch (e: Exception) {
                                                errorMessage = mapRegisterError(e)
                                                snackbarHostState.showSnackbar(errorMessage)
                                            } finally {
                                                isLoading = false
                                            }
                                        }
                                    }
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857)), // forest green
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "REGISTAR NA ARENA",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // 9. Divider "OU CONTINUE COM"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE2E8F0), thickness = 0.5.dp)
                            Text(
                                text = "OU CONTINUE COM",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF94A3B8),
                                modifier = Modifier.padding(horizontal = 10.dp),
                                letterSpacing = 0.5.sp
                            )
                            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE2E8F0), thickness = 0.5.dp)
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // 10. Google & Facebook buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SocialLoginButton(
                                icon = Icons.Default.Public,
                                label = "Google",
                                modifier = Modifier.weight(1f)
                            )
                            SocialLoginButton(
                                icon = Icons.Default.Share,
                                label = "Facebook",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        // 11. Footer Nav Link
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Já tem uma conta? ",
                                fontSize = 13.sp,
                                color = Color(0xFF64748B)
                            )
                            Text(
                                text = "Iniciar Sessão",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF047857),
                                modifier = Modifier.clickable { onNavigateToLogin() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(onRegisterSuccess = {}, onNavigateToLogin = {})
}

private fun mapRegisterError(exception: Exception): String {
    val rawMessage = exception.localizedMessage.orEmpty()
    val message = rawMessage.lowercase()

    return when {
        message.contains("timed out") || message.contains("timeout") ->
            "Tempo de resposta excedido no Supabase. Tente novamente em alguns segundos."
        message.contains("network") || message.contains("unable to resolve host") || message.contains("failed to connect") ->
            "Sem ligacao ao servidor. Verifique a internet e tente novamente."
        message.contains("user already registered") || message.contains("already exists") ->
            "Este e-mail ja esta registado. Inicie sessao ou recupere a password."
        message.contains("invalid email") ->
            "E-mail invalido. Confirme o formato e tente novamente."
        message.contains("password") && message.contains("weak") ->
            "Password fraca. Use pelo menos 6 caracteres, incluindo letras e numeros."
        message.contains("rate limit") || message.contains("too many requests") ->
            "Muitas tentativas em pouco tempo. Aguarde e tente novamente."
        else ->
            "Nao foi possivel concluir o registo. Tente novamente."
    }
}

