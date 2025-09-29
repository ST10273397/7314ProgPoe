package com.example.prog7314progpoe.database.holidays

import com.example.prog7314progpoe.database.calendar.FirebaseCalendarDbHelper
import com.example.prog7314progpoe.database.holidays.HolidayModel.DateInfo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object FirebaseHolidayDbHelper {

    private val db = FirebaseDatabase
        .getInstance("https://chronosync-f3425-default-rtdb.europe-west1.firebasedatabase.app/")
        .getReference("Holiday")

    fun insertHoliday(
        name: String,
        desc: String,
        date: DateInfo?,
        dateStart: DateInfo?,  //Optional if holiday extends past initial day
        dateEnd: DateInfo?,
        timeStart: Long,
        timeEnd: Long,
        repeat: List<String>? = listOf("Daily", "Weekly", "Monthly", "Annually"),
        type: List<String>?, // Example: ["National holiday", "Religious"]
        onComplete: () -> Unit = {}
    ){
        val key = db.push().key ?: return

        val holiday = HolidayModel(
            holidayId = key,
            name = name,
            desc = desc,
            date = date,
            dateStart = dateStart,
            dateEnd = dateEnd,
            timeStart = timeStart,
            timeEnd = timeEnd,
            repeat = repeat,
            type = type
        )

        db.child(key).setValue(holiday)
            .addOnCompleteListener { task ->
                onComplete()
            }
    }

    fun getAllHolidays(callback: (List<HolidayModel>) -> Unit) {
        db.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<HolidayModel>()
                snapshot.children.forEach { child ->
                    child.getValue(HolidayModel::class.java)?.let {holiday ->
                        holiday.holidayId = child.key ?: ""
                        list.add(holiday)
                    }
                }
                callback(list)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })

        fun updateHoliday(holidayId: String, holiday: HolidayModel, callback: (Boolean) -> Unit) {
            db.child(holidayId)

            db.setValue(holiday)
                .addOnSuccessListener { callback(true) }
                .addOnFailureListener { callback(false) }
        }

        fun deleteHoliday(holidayId: String, onComplete: () -> Unit = {}) {
            db.child(holidayId).removeValue()
                .addOnCompleteListener { task ->
                    onComplete()
                }
        }
    }
}