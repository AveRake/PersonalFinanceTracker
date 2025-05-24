package com.example.budgetapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.budgetapp.ui.theme.BudgetappTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BudgetappTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BudgetApp()
                }
            }
        }
    }
}

@Composable
fun BudgetApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val authManager = remember { AuthManager(context) }
    val apiService = remember { ApiService.create() }

    val authViewModel: AuthViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(apiService, authManager) as T
            }
        }
    )

    val budgetViewModel: BudgetViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return BudgetViewModel(apiService, authManager) as T
            }
        }
    )

    NavHost(navController = navController, startDestination = if (authViewModel.isLoggedIn) "budget" else "login") {
        composable("login") {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = { navController.navigate("budget") },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }
        composable("register") {
            RegisterScreen(
                authViewModel = authViewModel,
                onRegisterSuccess = { navController.navigate("login") },
                onNavigateToLogin = { navController.navigate("login") }
            )
        }
        composable("budget") {
            BudgetScreen(
                authViewModel = authViewModel,
                budgetViewModel = budgetViewModel,
                onLogout = { navController.navigate("login") }
            )
        }
    }
}
