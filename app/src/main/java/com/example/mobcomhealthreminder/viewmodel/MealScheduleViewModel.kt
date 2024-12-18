package com.example.mobcomhealthreminder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobcomhealthreminder.database.MealScheduleDao
import com.example.mobcomhealthreminder.model.MealSchedule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

open class MealScheduleViewModel(private val dao: MealScheduleDao) : ViewModel() {
    val schedules = dao.getAllSchedules().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun addMealSchedule(time: String, description: String, date: String) {
        viewModelScope.launch {
            dao.insertMealSchedule(MealSchedule(time = time, description = description, date = date))
        }
    }

    fun deleteMealSchedule(id: Int) {
        viewModelScope.launch {
            dao.deleteScheduleById(id)
        }
    }
    fun updateMealSchedule(id: Int, time: String, description: String, date: String) {
        viewModelScope.launch {
            dao.updateMealSchedule(id, time, description, date)
        }
    }
    fun getClosestSchedule(): MealSchedule? {
        val now = Calendar.getInstance()
        val currentTime = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE) // Waktu sekarang dalam menit

        return schedules.value
            .filter { schedule ->
                val scheduleTime = schedule.time.split(":").let {
                    val hour = it[0].toInt()
                    val minute = it[1].toInt()
                    hour * 60 + minute
                }
                scheduleTime > currentTime // Hanya jadwal yang setelah waktu sekarang
            }
            .minByOrNull { schedule ->
                val scheduleTime = schedule.time.split(":").let {
                    val hour = it[0].toInt()
                    val minute = it[1].toInt()
                    hour * 60 + minute
                }
                scheduleTime - currentTime // Cari jadwal dengan selisih waktu terkecil
            }
    }
}

// Helper ViewModel untuk Preview
class PreviewMealScheduleViewModel : MealScheduleViewModel(object : MealScheduleDao {
    override fun getAllSchedules(): StateFlow<List<MealSchedule>> =
        MutableStateFlow(
            listOf(
                MealSchedule(time = "08:00", description = "Breakfast", date = "11/12/2024"),
                MealSchedule(time = "12:00", description = "Lunch", date = "11/12/2024"),
                MealSchedule(time = "19:00", description = "Dinner", date = "11/12/2024")
            )
        )

    override suspend fun insertMealSchedule(schedule: MealSchedule) {}
    override suspend fun deleteScheduleById(id: Int) {}
    override suspend fun updateMealSchedule(id: Int, time: String, description: String, date: String) {}
})