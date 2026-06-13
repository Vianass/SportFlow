package com.sportflow.app.ui.screens.admin

import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sportflow.app.R
import com.sportflow.app.ui.theme.SportFlowDarkBlue
import com.sportflow.app.ui.theme.SportFlowGreen
import com.sportflow.app.ui.theme.SportFlowTextGray
import com.sportflow.app.ui.viewmodel.AdminViewModel

@Composable
fun AdminDashboardScreen(
    onLogout: () -> Unit = {},
    adminViewModel: AdminViewModel = viewModel()
) {
    var selectedTab by remember { mutableStateOf(0) }
    var createFormRequest by remember { mutableIntStateOf(0) }
    val adminState by adminViewModel.uiState.collectAsState()

    if (!adminState.accessChecked || adminState.isLoading && !adminState.isAuthorized) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = SportFlowGreen)
        }
        return
    }

    if (!adminState.isAuthorized) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color.Red, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = adminState.errorMessage ?: "Acesso reservado a administradores ativos.",
                color = Color.Red
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = onLogout) { Text("Voltar ao Login") }
        }
        return
    }

    Scaffold(
        topBar = { SportFlowAdminHeader() },
        bottomBar = {
            AdminBottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> AdminHomeScreen(
                    viewModel = adminViewModel,
                    onOpenEvents = { selectedTab = 1 },
                    onCreateEvent = {
                        createFormRequest++
                        selectedTab = 1
                    }
                )
                1 -> AdminEventsScreen(
                    viewModel = adminViewModel,
                    createFormRequest = createFormRequest
                )
                2 -> AdminStatsScreen(adminViewModel)
                3 -> AdminProfileScreen(onLogout = onLogout, adminViewModel = adminViewModel)
            }
        }
    }
}

@Composable
fun SportFlowAdminHeader() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SportFlowDarkBlue,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.sportflow_logo),
                contentDescription = "SportFlow Logo",
                modifier = Modifier
                    .size(32.dp)
                    .background(Color.White.copy(alpha = 0.05f), CircleShape)
                    .padding(4.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "SPORTFLOW",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            // TODO: ligar pesquisa global quando houver uma pesquisa transversal definida.
            IconButton(
                onClick = {},
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Pesquisar",
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, SportFlowGreen, CircleShape)
                    .background(Color.White.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Perfil",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun AdminBottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
        color = SportFlowDarkBlue
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val tabs = listOf(
                Triple("INÍCIO", Icons.Default.Home, 0),
                Triple("EVENTOS", Icons.Default.DateRange, 1),
                Triple("ESTATÍSTICAS", Icons.Default.BarChart, 2), // Customized for Admin
                Triple("PERFIL", Icons.Default.Person, 3)
            )

            tabs.forEach { (label, icon, index) ->
                val isSelected = selectedTab == index
                val itemColor = if (isSelected) SportFlowGreen else Color(0xFF94A3B8)

                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onTabSelected(index) }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = itemColor,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = label,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = itemColor,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AdminEventsPlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = Icons.Default.DateRange, contentDescription = null, tint = SportFlowTextGray, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Eventos Criados", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SportFlowDarkBlue)
            Text(text = "Crie, edite e organize competições.", fontSize = 13.sp, color = SportFlowTextGray)
        }
    }
}

@Composable
fun AdminStatsPlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = Icons.Default.BarChart, contentDescription = null, tint = SportFlowTextGray, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Estatísticas Gerais", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SportFlowDarkBlue)
            Text(text = "Faturação, número de atletas e relatórios avançados.", fontSize = 13.sp, color = SportFlowTextGray)
        }
    }
}

@Composable
fun AdminProfilePlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = SportFlowTextGray, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Perfil do Diretor", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SportFlowDarkBlue)
            Text(text = "Configurações da conta administrativa.", fontSize = 13.sp, color = SportFlowTextGray)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminDashboardScreenPreview() {
    AdminDashboardScreen()
}
