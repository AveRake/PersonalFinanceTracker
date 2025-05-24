package com.example.budgetapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class BudgetViewModel(private val apiService: ApiService, private val authManager: AuthManager) : ViewModel() {
    var budgetItems by mutableStateOf<List<BudgetItem>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    var newCategory by mutableStateOf("")
    var newAmount by mutableStateOf("")
    var newDescription by mutableStateOf("")
    var newDate by mutableStateOf("")

    fun loadBudgetData() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val token = authManager.getToken()
                if (token != null) {
                    budgetItems = apiService.getBudgetData(token)
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Ошибка загрузки данных"
            } finally {
                isLoading = false
            }
        }
    }

    fun addBudgetItem() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val token = authManager.getToken()
                if (token != null) {
                    val amount = newAmount.toDoubleOrNull() ?: throw IllegalArgumentException("Неверная сумма")
                    apiService.addBudgetItem(
                        token,
                        BudgetItemRequest(
                            category = newCategory,
                            amount = amount,
                            description = newDescription.ifEmpty { null },
                            date = newDate
                        )
                    )
                    loadBudgetData()
                    newCategory = ""
                    newAmount = ""
                    newDescription = ""
                    newDate = ""
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Ошибка добавления данных"
            } finally {
                isLoading = false
            }
        }
    }
}
