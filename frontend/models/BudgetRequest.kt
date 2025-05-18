package com.example.homebudget.models

data class BudgetRequest(
    val date: String,
    val name: String,
    val category: String,
    val amount: Double,
    val description: String = ""
)