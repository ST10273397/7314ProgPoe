package com.example.prog7314progpoe.database.calendar


import android.location.Location
import com.example.prog7314progpoe.database.holidays.HolidayModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object FirebaseCalendarDbHelper {

    private val db = FirebaseDatabase
        .getInstance("https://chronosync-f3425-default-rtdb.europe-west1.firebasedatabase.app/")
        .getReference("Calendar")

    fun insertCalendar(
        holidays: List<HolidayModel>,
        title: String,
        location: Location,
        userEmail: String,
        onComplete: () -> Unit = {}
    ){
        val key = db.push().key ?: return

        val calendar = CalendarModel(
            calendarId = key,
            title = title,
            holidays = holidays,
            location = location,
            userEmail = userEmail
        )

        db.child(key).setValue(calendar)
            .addOnCompleteListener { task ->
                onComplete()
            }
    }

    fun getCalendar(userEmail: String, callback: (CalendarModel?) -> Unit) {
        db.child(userEmail).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val calendar = snapshot.getValue(CalendarModel::class.java)?.apply {
                    this.userEmail = snapshot.key ?: ""
                }
                callback(calendar)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }

    fun getAllCalendars(callback: (List<CalendarModel>) -> Unit) {
        db.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<CalendarModel>()
                snapshot.children.forEach { child ->
                    child.getValue(CalendarModel::class.java)?.let { calendar ->
                        calendar.calendarId = child.key ?:""
                        list.add(calendar)
                    }
                }
                callback(list)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }

    fun updateCalendar(calendarId: String, calendar: CalendarModel, callback: (Boolean) -> Unit) {
        db.child(calendarId)

        db.setValue(calendar)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    fun deleteCalendar(calendarId: String, onComplete: () -> Unit = {}) {
        db.child(calendarId).removeValue()
            .addOnCompleteListener { task ->
            onComplete()
            }
    }
}