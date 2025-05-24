package com.example.budgetapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    authViewModel: AuthViewModel,
    budgetViewModel: BudgetViewModel,
    onLogout: () -> Unit
) {
    val isLoading = budgetViewModel.isLoading
    val errorMessage = budgetViewModel.errorMessage
    val budgetItems = budgetViewModel.budgetItems

    LaunchedEffect(Unit) {
        budgetViewModel.loadBudgetData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Мой бюджет", fontSize = 24.sp)
            Button(onClick = {
                authViewModel.logout()
                onLogout()
            }) {
                Text("Выйти")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Форма добавления новой записи
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Добавить запись", fontSize = 18.sp)

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = budgetViewModel.newCategory,
                    onValueChange = { budgetViewModel.newCategory = it },
                    label = { Text("Категория") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = budgetViewModel.newAmount,
                    onValueChange = { budgetViewModel.newAmount = it },
                    label = { Text("Сумма") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = budgetViewModel.newDescription,
                    onValueChange = { budgetViewModel.newDescription = it },
                    label = { Text("Описание") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = budgetViewModel.newDate,
                    onValueChange = { budgetViewModel.newDate = it },
                    label = { Text("Дата (ГГГГ-ММ-ДД)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    Button(
                        onClick = { budgetViewModel.addBudgetItem() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Добавить")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage != null) {
            Text(errorMessage, color = androidx.compose.ui.graphics.Color.Red)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Список записей бюджета
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(budgetItems) { item ->
                BudgetItemCard(item = item)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun BudgetItemCard(item: BudgetItem) {
    val formatter = NumberFormat.getNumberInstance(Locale.getDefault())

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(item.category, fontSize = 18.sp)
                Text("${formatter.format(item.amount)} ₽", fontSize = 18.sp)
            }

            if (!item.description.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(item.description)
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(item.date, fontSize = 12.sp)
        }
    }
}
