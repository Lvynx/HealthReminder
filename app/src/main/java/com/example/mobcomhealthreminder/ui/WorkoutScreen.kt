package com.example.mobcomhealthreminder.ui

import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobcomhealthreminder.utils.setWorkoutReminder
import com.example.mobcomhealthreminder.viewmodel.WorkoutViewModel
import com.example.mobcomhealthreminder.utils.showTimePickerDialog
import java.util.*

@Composable
fun WorkoutScreen(viewModel: WorkoutViewModel = viewModel()) {
    val context = LocalContext.current
    val schedules by viewModel.schedules.collectAsState()

    // State untuk input deskripsi
    var description by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Workout Schedule Reminder",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Workout Description") },
            placeholder = { Text("Enter workout description") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                // Tampilkan TimePickerDialog
                showTimePickerDialog(context) { hour, minute ->
                    val time = String.format("%02d:%02d", hour, minute)
                    val requestCode = schedules.size + 1
                    viewModel.addSchedule(time, requestCode, description) // Tambahkan deskripsi
                    setWorkoutReminder(context, hour, minute, requestCode, description) // Tambahkan deskripsi
                    description = "" // Reset input deskripsi
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Add New Workout Schedule")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Scheduled Workouts:",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(schedules.size) { index ->
                val schedule = schedules[index]
                Text(
                    text = "Workout ${index + 1}: ${schedule.time} - ${schedule.description}",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        }
    }
}