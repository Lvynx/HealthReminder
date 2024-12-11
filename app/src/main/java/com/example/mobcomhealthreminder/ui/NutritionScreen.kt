package com.example.mobcomhealthreminder.ui

import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobcomhealthreminder.viewmodel.MealScheduleViewModel
import com.example.mobcomhealthreminder.utils.setMealReminder
import java.util.*
import com.example.mobcomhealthreminder.utils.showTimePickerDialog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mobcomhealthreminder.utils.cancelAlarm
import com.example.mobcomhealthreminder.utils.cancelNotification

@Composable
fun NutritionScreen(viewModel: MealScheduleViewModel = viewModel()) {
    val schedules by viewModel.schedules.collectAsState()
    var description by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }
    val context = LocalContext.current

    // DatePicker Dialog
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
        },
        year, month, day
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Meal Schedule", style = MaterialTheme.typography.headlineSmall)

        // Input Deskripsi
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Meal Description") },
            modifier = Modifier.fillMaxWidth()
        )

        // Tombol Pilih Tanggal
        Button(onClick = { datePickerDialog.show() }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Text("Pick Date: ${if (selectedDate.isEmpty()) "Not Selected" else selectedDate}")
        }

        // Tombol Pilih Waktu
        Button(onClick = {
            showTimePickerDialog(context) { hour, minute ->
                selectedTime = String.format("%02d:%02d", hour, minute)
            }
        }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Text("Pick Time: ${if (selectedTime.isEmpty()) "Not Selected" else selectedTime}")
        }

        // Tambah Jadwal Meal
        Button(onClick = {
            if (description.isNotBlank() && selectedTime.isNotEmpty() && selectedDate.isNotEmpty()) {
                viewModel.addMealSchedule(selectedTime, description, selectedDate)
                val (hour, minute) = selectedTime.split(":").map { it.toInt() }
                val (day, month, year) = selectedDate.split("/").map { it.toInt() }

                // Atur notifikasi
                setMealReminder(
                    context = context,
                    year = year,
                    month = month - 1, // Bulan dimulai dari 0 di Calendar
                    day = day,
                    hour = hour,
                    minute = minute,
                    requestCode = schedules.size + 1,
                    description = description
                )

                // Reset input
                description = ""
                selectedTime = ""
                selectedDate = ""
            }
        }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Text("Add Meal Schedule")
        }

        // Daftar Jadwal Meal
        LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
            items(schedules.size) { index ->
                val schedule = schedules[index]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Date: ${schedule.date}", style = MaterialTheme.typography.bodyLarge)
                            Text("Time: ${schedule.time}", style = MaterialTheme.typography.bodyLarge)
                            Text("Description: ${schedule.description}", style = MaterialTheme.typography.bodyMedium)
                        }
                        Row {
                            IconButton(onClick = {
                                description = schedule.description
                                selectedTime = schedule.time
                                selectedDate = schedule.date
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit")
                            }
                            IconButton(onClick = {
                                viewModel.deleteMealSchedule(schedule.id)
                                cancelAlarm(context, schedule.id)
                                cancelNotification(context, schedule.id) // Pastikan menggunakan ID yang sama
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                        }
                    }
                }
            }
        }
    }
}
