package com.example.prog7314progpoe

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prog7314progpoe.api.ApiClient
import com.example.prog7314progpoe.database.holidays.HolidayAdapter

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CalendarActivity: AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private val apiKey = "ZRjKfqyaZbAy9ZaKFHdmudPaFuN2hEPI"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)


        recyclerView = findViewById(R.id.holidaysRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchHolidays()
    }

    private fun fetchHolidays() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = ApiClient.api.getHolidays(
                    apiKey = apiKey,
                    country = "IN",
                    year = 2025,
                    month = 3 // Example: March
                )

                val holidays = response.response.holidays

                // Update UI (must be on Main thread)
                withContext(Dispatchers.Main) {
                    recyclerView.adapter = HolidayAdapter(holidays)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
