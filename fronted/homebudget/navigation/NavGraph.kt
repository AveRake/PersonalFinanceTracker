package com.example.homebudget.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.homebudget.screens.AddTransactionScreen
import com.example.homebudget.screens.LoginScreen
import com.example.homebudget.screens.RegisterScreen
import com.example.homebudget.screens.TransactionsScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { token ->
                    navController.navigate("transactions/$token") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.popBackStack()
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
        composable("transactions/{token}") { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token") ?: ""
            TransactionsScreen(
                token = token,
                onAddTransaction = {
                    navController.navigate("add/$token")
                }
            )
        }
        composable("add/{token}") { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token") ?: ""
            AddTransactionScreen(
                token = token,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}