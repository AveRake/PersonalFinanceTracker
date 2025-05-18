package com.example.homebudget.models

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)