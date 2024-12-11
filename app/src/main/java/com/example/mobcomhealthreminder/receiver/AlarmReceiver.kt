package com.example.mobcomhealthreminder.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.mobcomhealthreminder.R
import com.example.mobcomhealthreminder.utils.NotificationUtils

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alarmType = intent.getStringExtra("ALARM_TYPE") ?: "UNKNOWN" // Parameter untuk menentukan jenis alarm
        val time = intent.getStringExtra("ALARM_TIME") ?: "Unknown Time" // Waktu alarm
        val description = intent.getStringExtra("ALARM_DESCRIPTION") ?: "No Description" // Deskripsi alarm

        // Menentukan channel ID, judul, pesan, dan ikon berdasarkan tipe alarm
        val (channelId, title, iconResId) = when (alarmType) {
            "WORKOUT" -> Triple(
                "workout_reminder_channel",
                "Workout Reminder",
                R.drawable.ic_workout // Ikon untuk Workout
            )
            "MEAL" -> Triple(
                "meal_reminder_channel",
                "Meal Reminder",
                R.drawable.ic_food // Ikon untuk Meal
            )
            else -> Triple(
                "default_reminder_channel",
                "Reminder",
                R.drawable.ic_logo_default// Default ikon
            )
        }

        // Menampilkan notifikasi menggunakan utilitas yang sudah diperbaiki
        NotificationUtils.showNotification(
            context = context,
            channelId = channelId,
            title = title,
            message = "It's time for your ${alarmType.toLowerCase()} at $time. Details: $description",
            iconResId = iconResId
        )
    }
}
