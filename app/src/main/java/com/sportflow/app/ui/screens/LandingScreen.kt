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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sportflow.app.R
import com.sportflow.app.ui.theme.SportFlowDarkBlue
import com.sportflow.app.ui.theme.SportFlowGreen

@Composable
fun LandingScreen(onNavigateToLogin: () -> Unit) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // 1. Fixed Header
        LandingHeader(onNavigateToLogin = onNavigateToLogin)

        // 2. Scrollable Body containing Hero and Footer Sections
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // A. Dark Hero Section
            HeroSection(onNavigateToLogin = onNavigateToLogin)

            // B. White Footer Section
            FooterSection()
        }
    }
}

@Composable
fun LandingHeader(onNavigateToLogin: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SportFlowDarkBlue,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.sportflowlogo),
                contentDescription = "SportFlow Logo",
                modifier = Modifier
                    .size(28.dp)
                    .background(Color.White.copy(alpha = 0.05f), CircleShape)
                    .padding(3.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "SPORTFLOW",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            // Header "ENTRAR" button
            Button(
                onClick = onNavigateToLogin,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SportFlowGreen),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    text = "ENTRAR",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = SportFlowDarkBlue,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
fun HeroSection(onNavigateToLogin: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A), // Slate 900
                        Color(0xFF020617)  // Slate 955
                    )
                )
            )
            .padding(horizontal = 24.dp, vertical = 40.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            // "ESTÁ NA HORA DE JOGAR" Green Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(SportFlowGreen.copy(alpha = 0.15f))
                    .border(1.dp, SportFlowGreen.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "ESTÁ NA HORA DE JOGAR",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = SportFlowGreen,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Main Bold Heading "CRIA E GERE O TEU TORNEIO"
            Text(
                text = "CRIA E GERE\nO TEU",
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                lineHeight = 38.sp
            )
            Text(
                text = "TORNEIO",
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                color = SportFlowGreen,
                lineHeight = 38.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Description Body
            Text(
                text = "A plataforma definitiva para gerir, organizar e acompanhar os teus torneios desportivos em tempo real.",
                fontSize = 14.sp,
                color = Color(0xFF94A3B8), // slate-400
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Sports Pitch Graphic representation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.03f))
                    .border(0.5.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Stadium Pitch Vector outline styling
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.SportsSoccer,
                        contentDescription = null,
                        tint = SportFlowGreen.copy(alpha = 0.15f),
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "ESTÁDIO SPORTFLOW",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.2f),
                        letterSpacing = 1.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // "COMEÇAR AGORA" Green button
            Button(
                onClick = onNavigateToLogin,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SportFlowGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "COMEÇAR AGORA",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = SportFlowDarkBlue,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // "VER DOCUMENTAÇÃO" Outline button
            OutlinedButton(
                onClick = { /* Open docs */ },
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "VER DOCUMENTAÇÃO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
fun FooterSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 40.dp)
    ) {
        // Logo + Text Row
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.sportflowlogo),
                contentDescription = "SportFlow Logo",
                modifier = Modifier
                    .size(24.dp)
                    .background(Color.Black.copy(alpha = 0.05f), CircleShape)
                    .padding(3.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "SPORTFLOW",
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SportFlowDarkBlue,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // About paragraph
        Text(
            text = "A plataforma de gestão desportiva definitiva para clubes, organizadores de torneios e atletas de alto rendimento.",
            fontSize = 11.sp,
            color = Color(0xFF64748B),
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Social Icons Row
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SocialShareIcon(icon = Icons.Default.Public)
            SocialShareIcon(icon = Icons.Default.Share)
            SocialShareIcon(icon = Icons.Default.Language)
        }

        Spacer(modifier = Modifier.height(32.dp))
        Divider(color = Color(0xFFE2E8F0), thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(28.dp))

        // Product Link columns (PLATAFORMA and SUPORTE)
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "PLATAFORMA",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = SportFlowDarkBlue,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                FooterLink(label = "Torneios")
                FooterLink(label = "Equipas")
                FooterLink(label = "Atletas")
                FooterLink(label = "Planos")
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "SUPORTE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = SportFlowDarkBlue,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                FooterLink(label = "Central de Ajuda")
                FooterLink(label = "Termos de Uso")
                FooterLink(label = "Política de Priv.")
                FooterLink(label = "Contacto")
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
        Divider(color = Color(0xFFE2E8F0), thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(20.dp))

        // Bottom Copyright Text
        Text(
            text = "© 2024 SportFlow. Todos os direitos reservados.",
            fontSize = 9.sp,
            color = Color(0xFF94A3B8),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun SocialShareIcon(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(Color(0xFFF1F5F9))
            .clickable { /* Social link */ },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF475569),
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun FooterLink(label: String) {
    Text(
        text = label,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF64748B),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Link click */ }
            .padding(vertical = 5.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun LandingScreenPreview() {
    LandingScreen(onNavigateToLogin = {})
}
