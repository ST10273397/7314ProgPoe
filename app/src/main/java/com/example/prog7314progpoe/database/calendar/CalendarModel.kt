package com.example.prog7314progpoe.database.calendar

import android.location.Location
import com.example.prog7314progpoe.database.holidays.HolidayModel

data class CalendarModel(
    var calendarId: String, //Using Firebase Key as identifier
    val title: String,
    val holidays: List<HolidayModel>?,
    val location: Location?,
    var userEmail: String
){
    constructor() : this("", "", null, null, "")

    data class CalendarResponse(
        val response: CalendarModel
    )
}

