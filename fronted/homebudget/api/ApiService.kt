package com.example.homebudget.api

import com.example.homebudget.models.AuthResponse
import com.example.homebudget.models.LoginRequest
import com.example.homebudget.models.RegisterRequest
import com.example.homebudget.models.Transaction
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST("transactions/add")
    suspend fun addTransaction(
        @Header("Authorization") token: String,
        @Body transaction: Transaction
    ): Response<Unit>

    @GET("transactions")
    suspend fun getTransactions(
        @Header("Authorization") token: String,
        @Query("start_date") startDate: String?,
        @Query("end_date") endDate: String?
    ): Response<List<Transaction>>
}