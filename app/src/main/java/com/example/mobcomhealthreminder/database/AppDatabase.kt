package com.example.mobcomhealthreminder.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mobcomhealthreminder.model.MealSchedule

@Database(entities = [MealSchedule::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mealScheduleDao(): MealScheduleDao
}
