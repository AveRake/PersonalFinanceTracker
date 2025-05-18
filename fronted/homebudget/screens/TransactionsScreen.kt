package com.example.homebudget.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.homebudget.components.DatePickerDialog
import com.example.homebudget.models.Transaction
import com.example.homebudget.api.ApiClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    token: String,
    onAddTransaction: () -> Unit
) {
    var transactions by remember { mutableStateOf<List<Transaction>>(emptyList()) }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Выносим функцию за пределы LaunchedEffect
    fun loadTransactions(token: String, startDate: String, endDate: String) {
        scope.launch {
            isLoading = true
            try {
                val response = ApiClient.apiService.getTransactions(
                    "Bearer $token",
                    if (startDate.isNotEmpty()) startDate else null,
                    if (endDate.isNotEmpty()) endDate else null
                )
                if (response.isSuccessful) {
                    transactions = response.body() ?: emptyList()
                    errorMessage = null
                } else {
                    errorMessage = "Ошибка загрузки данных"
                }
            } catch (e: Exception) {
                errorMessage = "Ошибка сети: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    if (showStartDatePicker) {
        DatePickerDialog(
            onDateSelected = { selectedDate ->
                startDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate)
                showStartDatePicker = false
                loadTransactions(token, startDate, endDate)
            },
            onDismiss = { showStartDatePicker = false }
        )
    }

    if (showEndDatePicker) {
        DatePickerDialog(
            onDateSelected = { selectedDate ->
                endDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate)
                showEndDatePicker = false
                loadTransactions(token, startDate, endDate)
            },
            onDismiss = { showEndDatePicker = false }
        )
    }

    // Используем LaunchedEffect для первоначальной загрузки
    LaunchedEffect(Unit) {
        loadTransactions(token, startDate, endDate)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Домашний бюджет") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTransaction
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = { showStartDatePicker = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (startDate.isNotEmpty()) startDate else "Начальная дата")
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedButton(
                    onClick = { showEndDatePicker = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (endDate.isNotEmpty()) endDate else "Конечная дата")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(transactions) { transaction ->
                        TransactionItem(transaction = transaction)
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = transaction.name,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "%.2f руб".format(transaction.amount),
                style = MaterialTheme.typography.bodyLarge,
                color = if (transaction.type == "income") MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = transaction.date,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = if (transaction.type == "income") "Доход" else "Расход",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}