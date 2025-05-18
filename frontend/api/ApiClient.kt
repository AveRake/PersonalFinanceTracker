package com.example.homebudget.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    // Для эмулятора Android Studio
    private const val BASE_URL = "http://10.0.2.2:8000/"

    // Для отладки на реальном устройстве (замените на ваш локальный IP)
    // private const val BASE_URL = "http://192.168.1.X:8000/"

    private val client = OkHttpClient.Builder().apply {
        connectTimeout(30, TimeUnit.SECONDS) // Увеличим таймаут
        readTimeout(30, TimeUnit.SECONDS)
        writeTimeout(30, TimeUnit.SECONDS)

        // Добавляем обработчик для логов
        addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })

        // Добавляем обработчик ошибок соединения
        addInterceptor { chain ->
            try {
                chain.proceed(chain.request())
            } catch (e: Exception) {
                throw Exception("Проверьте:\n1. Запущен ли сервер\n2. Правильный ли IP-адрес\n3. Разрешен ли cleartext трафик\nОшибка: ${e.message}")
            }
        }
    }.build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}