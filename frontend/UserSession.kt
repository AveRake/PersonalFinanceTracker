package com.example.homebudget

object UserSession {
    var token: String = ""
    var userId: Int = 0
    var expiresAt: Long = 0

    fun isTokenValid(): Boolean {
        return token.isNotEmpty() && expiresAt > System.currentTimeMillis() / 1000
    }
}