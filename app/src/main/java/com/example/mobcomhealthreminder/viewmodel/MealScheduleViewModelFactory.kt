package com.example.mobcomhealthreminder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mobcomhealthreminder.database.MealScheduleDao

class MealScheduleViewModelFactory(private val dao: MealScheduleDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MealScheduleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MealScheduleViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}