package com.example.mobcomhealthreminder.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobcomhealthreminder.viewmodel.TrackingViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PhysicalActivityScreen(viewModel: TrackingViewModel = viewModel()) {
    val trackingData by viewModel.trackingData.collectAsState()

    // Panggil fungsi untuk mengambil data hanya sekali
    LaunchedEffect(Unit) {
        viewModel.fetchTrackingData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "History Daily Physical Activity",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (trackingData.isEmpty()) {
            Text(
                text = "No data available",
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(trackingData) { data ->
                    val steps = data["steps"] as? Long ?: 0L
                    val calories = data["calories"] as? Double ?: 0.0
                    val distanceMeters = data["distance_meters"] as? Double ?: 0.0
                    val dateTimestamp = data["date"] as? Timestamp

                    // Format tanggal jika tersedia
                    val formattedDate = dateTimestamp?.toDate()?.let {
                        SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(it)
                    } ?: "Unknown Date"

                    // Card for each activity record
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Judul dengan tanggal
                            Text(
                                text = formattedDate,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF616161),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "🏃 $steps Steps",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "📏 ${distanceMeters.roundToInt()} Meters",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "🔥 ${calories.roundToInt()} Calories burn",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}