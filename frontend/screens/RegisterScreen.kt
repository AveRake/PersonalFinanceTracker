package com.example.homebudget.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.homebudget.UserSession
import com.example.homebudget.api.ApiClient
import com.example.homebudget.models.RegisterRequest
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Регистрация",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Логин") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        if (successMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = successMessage!!,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (username.isBlank() || email.isBlank() || password.isBlank()) {
                    errorMessage = "Заполните все поля"
                    return@Button
                }

                isLoading = true
                errorMessage = null
                successMessage = null

                scope.launch {
                    try {
                        val response = ApiClient.apiService.register(
                            RegisterRequest(
                                username = username,
                                email = email,
                                password = password
                            )
                        )

                        if (response.isSuccessful) {
                            response.body()?.let { authResponse ->
                                // Сохраняем данные сессии
                                UserSession.apply {
                                    token = authResponse.token
                                    userId = authResponse.user_id
                                    expiresAt = authResponse.expires_at
                                }
                                successMessage = "Регистрация успешна!"
                                onRegisterSuccess()
                            } ?: run {
                                errorMessage = "Ошибка: неверный формат ответа сервера"
                            }
                        } else {
                            errorMessage = when (response.code()) {
                                409 -> "Пользователь уже существует"
                                400 -> "Некорректные данные"
                                500 -> "Ошибка сервера"
                                else -> "Ошибка: ${response.code()}"
                            }
                            Log.w("RegisterError", "Ошибка регистрации: ${response.errorBody()?.string()}")
                        }
                    } catch (e: Exception) {
                        errorMessage = when {
                            e.message?.contains("timeout") == true -> "Таймаут соединения"
                            e.message?.contains("Unable to resolve host") == true -> "Проблемы с интернетом"
                            else -> "Ошибка соединения"
                        }
                        Log.e("RegisterError", "Ошибка регистрации", e)
                        showToast(context, "Подробности в логах")
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Text("Зарегистрироваться")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onNavigateToLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Уже есть аккаунт? Войти")
        }
    }
}

private fun showToast(context: Context, message: String) {
    ContextCompat.getMainExecutor(context).execute {
        android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_LONG).show()
    }
}