package com.example.mobcomhealthreminder.utils

import android.app.TimePickerDialog
import android.content.Context
import java.util.*

fun showTimePickerDialog(context: Context, onTimePicked: (Int, Int) -> Unit) {
    val calendar = Calendar.getInstance()
    val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
    val currentMinute = calendar.get(Calendar.MINUTE)

    TimePickerDialog(
        context,
        { _, hour, minute -> onTimePicked(hour, minute) },
        currentHour,
        currentMinute,
        true
    ).show()
}