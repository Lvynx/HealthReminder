package com.example.mobcomhealthreminder.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.mobcomhealthreminder.receiver.AlarmReceiver
import java.util.Calendar

fun setMealReminder(context: Context, year: Int, month: Int, day: Int, hour: Int, minute: Int, requestCode: Int, description: String) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, day)
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
    }

    val intent = Intent(context, AlarmReceiver::class.java).apply {
        putExtra("ALARM_ID", requestCode)
        putExtra("ALARM_DESCRIPTION", description)
        putExtra("ALARM_TYPE", "MEAL")
        putExtra("ALARM_TIME", String.format("%02d:%02d", hour, minute))
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        requestCode,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    alarmManager.setExact(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        pendingIntent
    )
}