package com.example.prog7314progpoe

public class UserModel {

    data class HolidayResponse(
        val response: ResponseData
    )

    data class ResponseData(
        val holidays: List<Holiday>
    )

    data class Holiday(
        val name: String,
        val description: String,
        val date: DateInfo,
        val type: List<String> // Example: ["National holiday", "Religious"]
    )

    data class DateInfo(
        val iso: String // e.g., "2025-01-01"
    )





}

    // User model class (data class for simplicity)
