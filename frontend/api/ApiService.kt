package com.example.homebudget.api

import com.example.homebudget.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST("api/budget")
    suspend fun addTransaction(
        @Header("Authorization") token: String,
        @Body transaction: BudgetRequest
    ): Response<Unit>

    @GET("api/budget/day/{date}")
    suspend fun getTransactionsByDay(
        @Header("Authorization") token: String,
        @Path("date") date: String
    ): Response<List<BudgetResponse>>

    @GET("api/budget/period/{start}/{end}")
    suspend fun getTransactionsByPeriod(
        @Header("Authorization") token: String,
        @Path("start") startDate: String,
        @Path("end") endDate: String
    ): Response<List<BudgetResponse>>
}