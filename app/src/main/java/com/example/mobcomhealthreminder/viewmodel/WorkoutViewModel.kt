package com.example.mobcomhealthreminder.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mobcomhealthreminder.model.WorkoutSchedule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WorkoutViewModel : ViewModel() {
    private val _schedules = MutableStateFlow<List<WorkoutSchedule>>(emptyList())
    val schedules: StateFlow<List<WorkoutSchedule>> = _schedules

    fun addSchedule(time: String, requestCode: Int, description: String) {
        _schedules.value = _schedules.value + WorkoutSchedule(time, requestCode, description)
    }
}

