package com.example.mobcomhealthreminder.database

import androidx.room.*
import com.example.mobcomhealthreminder.model.MealSchedule
import kotlinx.coroutines.flow.Flow

@Dao
interface MealScheduleDao {
    @Query("SELECT * FROM meal_schedule ORDER BY date, time")
    fun getAllSchedules(): Flow<List<MealSchedule>>

    @Insert
    suspend fun insertMealSchedule(schedule: MealSchedule)

    @Query("DELETE FROM meal_schedule WHERE id = :id")
    suspend fun deleteScheduleById(id: Int)

    @Query("UPDATE meal_schedule SET time = :time, description = :description, date = :date WHERE id = :id")
    suspend fun updateMealSchedule(id: Int, time: String, description: String, date: String)
}