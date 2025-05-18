package com.example.homebudget.models

data class Transaction(
    val date: String,
    val name: String,
    val type: String,
    val amount: Double
)