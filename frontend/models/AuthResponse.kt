package com.example.homebudget.models

data class AuthResponse(
    val token: String,
    val expires_at: Long,
    val user_id: Int
)