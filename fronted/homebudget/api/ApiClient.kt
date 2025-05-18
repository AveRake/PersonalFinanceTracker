package com.example.homebudget.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:8000/" // Для эмулятора
    private const val CONNECT_TIMEOUT = 15L // 15 секунд
    private const val READ_TIMEOUT = 15L // 15 секунд

    // Создаем клиент с улучшенной конфигурацией
    private val client = OkHttpClient.Builder().apply {
        connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
        readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)

        // Добавляем логгер для всех запросов/ответов
        addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Логируем всё
        })

        // Добавляем общие заголовки
        addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }
    }.build()

    // Инициализация Retrofit с ленивой загрузкой
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Ленивая инициализация сервиса
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}