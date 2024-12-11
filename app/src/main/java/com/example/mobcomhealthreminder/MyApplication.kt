package com.example.mobcomhealthreminder


import android.app.Application
import androidx.room.Room
import com.example.mobcomhealthreminder.database.AppDatabase

class MyApplication : Application() {
    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "mobcom_health_reminder.db"
        ).build()
    }
}