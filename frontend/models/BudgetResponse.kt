package com.example.homebudget.models

data class BudgetResponse(
    val date: String,
    val name: String,
    val category: String,
    val amount: Double,
    val description: String
)