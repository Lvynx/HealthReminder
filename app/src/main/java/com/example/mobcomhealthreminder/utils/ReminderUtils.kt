package com.example.mobcomhealthreminder.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import com.example.mobcomhealthreminder.receiver.AlarmReceiver
import java.util.Calendar

fun setWorkoutReminder(context: Context, hour: Int, minute: Int, requestCode: Int, description: String) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
    }

    val intent = Intent(context, AlarmReceiver::class.java).apply {
        action = "com.example.mobcomhealthreminder.WORKOUT_ALARM"
        putExtra("ALARM_ID", requestCode)
        putExtra("ALARM_TIME", String.format("%02d:%02d", hour, minute))
        putExtra("ALARM_DESCRIPTION", description) // Deskripsi dikirim melalui Intent
        putExtra("ALARM_TYPE", "WORKOUT") // ALARM_TYPE ditambahkan
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

    Toast.makeText(context, "Workout reminder set for ${hour}:${minute} - $description", Toast.LENGTH_SHORT).show()
}


fun requestExactAlarmPermission(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
            data = Uri.parse("package:${context.packageName}")
        }
        context.startActivity(intent)
    }
}

