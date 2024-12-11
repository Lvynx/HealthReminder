package com.example.mobcomhealthreminder.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import com.example.mobcomhealthreminder.R

object NotificationUtils {
    // Fungsi umum untuk menampilkan notifikasi
    fun showNotification(
        context: Context,
        channelId: String,
        title: String,
        message: String,
        iconResId: Int
    ) {
        createNotificationChannel(context, channelId, title)

        // Periksa izin POST_NOTIFICATIONS untuk Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // Membangun notifikasi
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(iconResId) // Ikon yang fleksibel
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        // Menampilkan notifikasi
        with(NotificationManagerCompat.from(context)) {
            notify(title.hashCode(), notification) // Menggunakan hash dari judul sebagai ID unik
        }
    }

    // Fungsi untuk membuat NotificationChannel
    private fun createNotificationChannel(context: Context, channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for $channelName notifications"
            }

            // Membuat channel di NotificationManager
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}
