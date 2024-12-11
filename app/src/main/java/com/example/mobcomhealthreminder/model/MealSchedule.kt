package com.example.mobcomhealthreminder.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meal_schedule")
data class MealSchedule(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val time: String,
    val description: String,
    val date: String
)