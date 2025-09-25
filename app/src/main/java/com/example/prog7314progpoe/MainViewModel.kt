package com.example.prog7314progpoe

import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val apiKey = "ZRjKfqyaZbAy9ZaKFHdmudPaFuN2hEPI"

    suspend fun fetchHolidays() {
        try {
            val response = ApiClient.api.getHolidays(
                apiKey = "YOUR_API_KEY_HERE",
                country = "IN",
                year = 2025,
                month = 3 // March
            )

            response.response.holidays.forEach {
                println("Holiday: ${it.name} | Date: ${it.date.iso} | Type: ${it.type}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    }
