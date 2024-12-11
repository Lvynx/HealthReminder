package com.example.mobcomhealthreminder.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.mobcomhealthreminder.R
import com.example.mobcomhealthreminder.utils.NotificationUtils

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_ALARM_ID = "ALARM_ID"
        const val EXTRA_ALARM_DESCRIPTION = "ALARM_DESCRIPTION"
        const val EXTRA_ALARM_TYPE = "ALARM_TYPE"
        const val EXTRA_ALARM_TIME = "ALARM_TIME"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val alarmType = intent.getStringExtra(EXTRA_ALARM_TYPE) ?: "Unknown"
        val time = intent.getStringExtra(EXTRA_ALARM_TIME) ?: "Unknown Time"
        val description = intent.getStringExtra(EXTRA_ALARM_DESCRIPTION) ?: "No Description"

        val notificationConfig = getNotificationConfig(alarmType)

        NotificationUtils.showNotification(
            context = context,
            channelId = notificationConfig.channelId,
            title = notificationConfig.title,
            message = "It's time for your ${alarmType.lowercase()} at $time. Details: $description",
            iconResId = notificationConfig.iconResId
        )
    }

    private fun getNotificationConfig(alarmType: String): NotificationConfig {
        return when (alarmType) {
            "WORKOUT" -> NotificationConfig(
                channelId = "workout_reminder_channel",
                title = "Workout Reminder",
                iconResId = R.drawable.ic_workout
            )
            "MEAL" -> NotificationConfig(
                channelId = "meal_reminder_channel",
                title = "Meal Reminder",
                iconResId = R.drawable.ic_food
            )
            else -> NotificationConfig(
                channelId = "default_reminder_channel",
                title = "Reminder",
                iconResId = R.drawable.ic_logo_default
            )
        }
    }

    data class NotificationConfig(
        val channelId: String,
        val title: String,
        val iconResId: Int
    )
}
