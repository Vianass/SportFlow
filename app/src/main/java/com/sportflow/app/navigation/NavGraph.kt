package com.sportflow.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sportflow.app.ui.screens.*
import com.sportflow.app.ui.screens.user.UserDashboardScreen
import com.sportflow.app.ui.screens.admin.AdminDashboardScreen
import com.sportflow.app.ui.screens.organizador.OrganizadorDashboardScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.INTRO
    ) {
        composable(NavRoutes.INTRO) {
            IntroScreen(onNavigate = { route ->
                navController.navigate(route) {
                    popUpTo(NavRoutes.INTRO) { inclusive = true }
                }
            })
        }
        composable(NavRoutes.LANDING) {
            LandingScreen(onNavigateToLogin = {
                navController.navigate(NavRoutes.LOGIN)
            })
        }
        composable(NavRoutes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.INTRO) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(NavRoutes.REGISTER)
                }
            )
        }
        composable(NavRoutes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.INTRO) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(NavRoutes.LOGIN)
                }
            )
        }
        composable(NavRoutes.HOME) {
            val context = LocalContext.current
            val sharedPrefs = remember { context.getSharedPreferences("sportflow_prefs", Context.MODE_PRIVATE) }
            val userType = remember { sharedPrefs.getString("user_type", "ATLETA") ?: "ATLETA" }

            val onLogout = {
                sharedPrefs.edit()
                    .putBoolean("is_logged_in", false)
                    .apply()
                navController.navigate(NavRoutes.LANDING) {
                    popUpTo(NavRoutes.HOME) { inclusive = true }
                }
            }

            when (userType) {
                "ADMIN" -> AdminDashboardScreen(onLogout = onLogout)
                "ORGANIZADOR" -> OrganizadorDashboardScreen(onLogout = onLogout)
                else -> UserDashboardScreen(onLogout = onLogout)
            }
        }
    }
}