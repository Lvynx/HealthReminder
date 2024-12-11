package com.example.mobcomhealthreminder.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.mobcomhealthreminder.receiver.AlarmReceiver
import com.example.mobcomhealthreminder.receiver.AlarmReceiver.Companion.EXTRA_ALARM_DESCRIPTION
import com.example.mobcomhealthreminder.receiver.AlarmReceiver.Companion.EXTRA_ALARM_ID
import com.example.mobcomhealthreminder.receiver.AlarmReceiver.Companion.EXTRA_ALARM_TIME
import com.example.mobcomhealthreminder.receiver.AlarmReceiver.Companion.EXTRA_ALARM_TYPE
import java.util.Calendar
import android.app.NotificationManager

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
        putExtra(EXTRA_ALARM_ID, requestCode)
        putExtra(EXTRA_ALARM_DESCRIPTION, description)
        putExtra(EXTRA_ALARM_TYPE, "MEAL")
        putExtra(EXTRA_ALARM_TIME, String.format("%02d:%02d", hour, minute))
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

fun cancelAlarm(context: Context, requestCode: Int) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlarmReceiver::class.java).apply {
        putExtra(EXTRA_ALARM_TYPE, "MEAL")
        putExtra(EXTRA_ALARM_ID, requestCode) // Pastikan data ini identik
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        requestCode,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    alarmManager.cancel(pendingIntent) // Membatalkan alarm
    pendingIntent.cancel() // Membatalkan PendingIntent
}

fun cancelNotification(context: Context, notificationId: Int) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.cancel(notificationId)
}