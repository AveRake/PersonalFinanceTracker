package com.example.budgetapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AuthViewModel(private val apiService: ApiService, private val authManager: AuthManager) : ViewModel() {
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var email by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var isLoggedIn by mutableStateOf(false)
    var registrationSuccess by mutableStateOf(false)

    init {
        viewModelScope.launch {
            val token = authManager.getToken()
            isLoggedIn = token != null
        }
    }

    fun register() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = apiService.register(RegisterRequest(username, password, email))
                registrationSuccess = true
            } catch (e: Exception) {
                errorMessage = e.message ?: "Ошибка регистрации"
            } finally {
                isLoading = false
            }
        }
    }

    fun login() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = apiService.login(LoginRequest(username, password))
                authManager.saveAuthData("Bearer ${response.token}", response.userId)
                isLoggedIn = true
            } catch (e: Exception) {
                errorMessage = e.message ?: "Ошибка входа"
            } finally {
                isLoading = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authManager.clearAuthData()
            isLoggedIn = false
            username = ""
            password = ""
        }
    }
}
