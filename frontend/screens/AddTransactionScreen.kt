package com.example.homebudget.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.homebudget.components.DatePickerDialog
import com.example.homebudget.models.Transaction
import com.example.homebudget.api.ApiClient
import com.example.homebudget.models.BudgetRequest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    token: String,
    onBack: () -> Unit
) {
    var date by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("expense") }
    var showDatePicker by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    if (showDatePicker) {
        DatePickerDialog(
            onDateSelected = { selectedDate ->
                date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Добавить транзакцию") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (date.isNotEmpty()) date else "Выберите дату")
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Наименование") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FilterChip(
                    selected = type == "income",
                    onClick = { type = "income" },
                    label = { Text("Доход") }
                )
                FilterChip(
                    selected = type == "expense",
                    onClick = { type = "expense" },
                    label = { Text("Расход") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Сумма") },
                modifier = Modifier.fillMaxWidth()
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (date.isEmpty() || name.isEmpty() || amount.isEmpty()) {
                        errorMessage = "Заполните все поля"
                        return@Button
                    }

                    val amountValue = try {
                        amount.toDouble()
                    } catch (e: NumberFormatException) {
                        errorMessage = "Введите корректную сумму"
                        return@Button
                    }

                    isLoading = true
                    scope.launch {
                        try {
                            val transaction = BudgetRequest(
                                date = date,
                                name = name,
                                category = if (type == "income") "income" else "expense",
                                amount = amountValue
                            )
                            val response = ApiClient.apiService.addTransaction(
                                "Bearer $token",
                                transaction
                            )
                            if (response.isSuccessful) {
                                onBack()
                            } else {
                                errorMessage = "Ошибка при добавлении: ${response.code()}"
                            }
                        } catch (e: Exception) {
                            errorMessage = "Ошибка сети: ${e.message}"
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
                    Text("Добавить")
                }
            }
        }
    }
}
