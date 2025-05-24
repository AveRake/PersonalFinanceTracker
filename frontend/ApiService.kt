package com.example.budgetapp

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse

    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("budget")
    suspend fun getBudgetData(@Header("Authorization") token: String): List<BudgetItem>

    @POST("budget")
    suspend fun addBudgetItem(
        @Header("Authorization") token: String,
        @Body request: BudgetItemRequest
    ): ApiResponse

    companion object {
        private const val BASE_URL = "http://10.0.2.2:3000/" // Для эмулятора используйте 10.0.2.2 вместо localhost

        fun create(): ApiService {
            val logger = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}

data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val userId: Int
)

data class ApiResponse(
    val message: String
)

data class BudgetItem(
    val id: Int,
    val user_id: Int,
    val category: String,
    val amount: Double,
    val description: String?,
    val date: String
)

data class BudgetItemRequest(
    val category: String,
    val amount: Double,
    val description: String?,
    val date: String
)
